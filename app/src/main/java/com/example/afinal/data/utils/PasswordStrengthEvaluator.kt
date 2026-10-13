package com.example.afinal.data.utils

import kotlin.math.log2
import kotlin.math.min

data class PasswordStrengthResult(
    val score: Int, // 0 - 100
    val level: String, // Very Weak, Weak, Fair, Strong, Very Strong
    val entropyBits: Double,
    val length: Int,
    val hasUppercase: Boolean,
    val hasLowercase: Boolean,
    val hasDigits: Boolean,
    val hasSymbols: Boolean,
    val warnings: List<String>
)

object PasswordStrengthEvaluator {

    fun evaluate(password: String): PasswordStrengthResult {
        if (password.isEmpty()) {
            return PasswordStrengthResult(
                score = 0,
                level = "No Password",
                entropyBits = 0.0,
                length = 0,
                hasUppercase = false,
                hasLowercase = false,
                hasDigits = false,
                hasSymbols = false,
                warnings = listOf("Please enter a password to evaluate")
            )
        }

        val len = password.length
        val hasUpper = password.any { it.isUpperCase() }
        val hasLower = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSymbol = password.any { !it.isLetterOrDigit() }

        var poolSize = 0
        if (hasUpper) poolSize += 26
        if (hasLower) poolSize += 26
        if (hasDigit) poolSize += 10
        if (hasSymbol) poolSize += 32

        val entropy = if (poolSize > 0) len * log2(poolSize.toDouble()) else 0.0

        val warnings = mutableListOf<String>()

        // Check common weak patterns
        val lower = password.lowercase()
        if (listOf("password", "123456", "qwerty", "admin", "letmein", "123456789", "iloveyou").any { lower.contains(it) }) {
            warnings.add("Contains common or easily guessable patterns")
        }
        if (len >= 3 && password.windowed(3).any { it[0] == it[1] && it[1] == it[2] }) {
            warnings.add("Contains repeating characters")
        }
        if (len < 8) {
            warnings.add("Length is less than 8 characters (vulnerable to brute force)")
        }

        // Calculate score (0-100)
        var score = 0

        // Length component (up to 40 pts)
        score += min(len * 3, 40)

        // Character diversity (up to 40 pts)
        var diversityCount = 0
        if (hasUpper) diversityCount++
        if (hasLower) diversityCount++
        if (hasDigit) diversityCount++
        if (hasSymbol) diversityCount++
        score += diversityCount * 10

        // Entropy component (up to 20 pts)
        val entropyBonus = min((entropy / 3.0).toInt(), 20)
        score += entropyBonus

        // Deduct for warnings
        score -= warnings.size * 15

        score = score.coerceIn(0, 100)

        val level = when {
            score >= 85 -> "Very Strong"
            score >= 70 -> "Strong"
            score >= 50 -> "Fair"
            score >= 30 -> "Weak"
            else -> "Very Weak"
        }

        return PasswordStrengthResult(
            score = score,
            level = level,
            entropyBits = entropy,
            length = len,
            hasUppercase = hasUpper,
            hasLowercase = hasLower,
            hasDigits = hasDigit,
            hasSymbols = hasSymbol,
            warnings = warnings
        )
    }

    fun maskPassword(password: String): String {
        if (password.length <= 3) return "***"
        val prefix = password.take(2)
        val suffix = password.takeLast(1)
        val masked = "*".repeat(password.length - 3)
        return "$prefix$masked$suffix"
    }
}
