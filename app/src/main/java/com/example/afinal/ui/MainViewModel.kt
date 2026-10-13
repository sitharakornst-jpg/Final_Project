package com.example.afinal.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.afinal.data.ai.GeminiHelper
import com.example.afinal.data.local.PasswordCheckEntity
import com.example.afinal.data.local.PasswordCheckRepository
import com.example.afinal.data.remote.HibpHelper
import com.example.afinal.data.utils.PasswordStrengthEvaluator
import com.example.afinal.data.utils.PasswordStrengthResult
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PasswordCheckRepository(application)

    val historyList: StateFlow<List<PasswordCheckEntity>> = repository.history

    suspend fun checkPasswordLeak(password: String): Int {
        return HibpHelper.checkPasswordBreach(password)
    }

    suspend fun analyzeWithAi(
        password: String,
        evalResult: PasswordStrengthResult,
        breachCount: Int,
        apiKey: String
    ): String {
        val helper = GeminiHelper(apiKey)
        return helper.analyzePasswordSecurity(
            length = evalResult.length,
            score = evalResult.score,
            strengthLevel = evalResult.level,
            entropyBits = evalResult.entropyBits,
            breachCount = breachCount,
            hasUpper = evalResult.hasUppercase,
            hasLower = evalResult.hasLowercase,
            hasDigit = evalResult.hasDigits,
            hasSymbol = evalResult.hasSymbols,
            warnings = evalResult.warnings
        )
    }

    fun saveCheckResult(
        password: String,
        evalResult: PasswordStrengthResult,
        breachCount: Int,
        aiAdvice: String
    ) {
        viewModelScope.launch {
            val masked = PasswordStrengthEvaluator.maskPassword(password)
            repository.insert(
                PasswordCheckEntity(
                    maskedPassword = masked,
                    score = evalResult.score,
                    strengthLevel = evalResult.level,
                    length = evalResult.length,
                    breachCount = breachCount,
                    aiAdvice = aiAdvice
                )
            )
        }
    }

    fun deleteCheck(id: Long) {
        viewModelScope.launch {
            repository.delete(id)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }
}
