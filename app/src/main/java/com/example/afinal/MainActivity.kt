package com.example.afinal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.afinal.ui.MainViewModel
import com.example.afinal.ui.screens.HistoryVaultScreen
import com.example.afinal.ui.screens.PasswordCheckScreen
import com.example.afinal.ui.screens.PasswordGeneratorScreen
import com.example.afinal.ui.theme.FinalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinalTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val viewModel: MainViewModel = viewModel()
                    val history by viewModel.historyList.collectAsState()

                    // ฝัง API Key ไว้ในตัวแอป (เปลี่ยนเป็น OpenRouter Key ของคุณที่นี่)
                    val builtInApiKey = "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" 
                    var prefilledPassword by remember { mutableStateOf("") }

                    NavHost(navController = navController, startDestination = "check") {
                        composable("check") {
                            PasswordCheckScreen(
                                initialPassword = prefilledPassword,
                                apiKey = builtInApiKey,
                                onApiKeyChange = { }, // ไม่ต้องอัปเดตจาก UI แล้ว
                                onCheckLeak = { password ->
                                    viewModel.checkPasswordLeak(password)
                                },
                                onAnalyzeAi = { password, evalResult, breachCount ->
                                    viewModel.analyzeWithAi(password, evalResult, breachCount, builtInApiKey)
                                },
                                onSaveRecord = { password, evalResult, breachCount, aiAdvice ->
                                    viewModel.saveCheckResult(password, evalResult, breachCount, aiAdvice)
                                },
                                onNavigateGenerator = {
                                    navController.navigate("generator")
                                },
                                onNavigateHistory = {
                                    navController.navigate("vault")
                                }
                            )
                        }

                        composable("generator") {
                            PasswordGeneratorScreen(
                                onBack = { navController.popBackStack() },
                                onSendToAnalyzer = { password ->
                                    prefilledPassword = password
                                    navController.navigate("check") {
                                        popUpTo("check") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("vault") {
                            HistoryVaultScreen(
                                historyList = history,
                                onBack = { navController.popBackStack() },
                                onDelete = { id -> viewModel.deleteCheck(id) },
                                onClearAll = { viewModel.clearHistory() }
                            )
                        }
                    }
                }
            }
        }
    }
}
