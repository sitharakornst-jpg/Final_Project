package com.example.afinal.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.afinal.data.utils.PasswordStrengthEvaluator
import com.example.afinal.data.utils.PasswordStrengthResult
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordCheckScreen(
    initialPassword: String = "",
    apiKey: String,
    onApiKeyChange: (String) -> Unit,
    onCheckLeak: suspend (String) -> Int,
    onAnalyzeAi: suspend (String, PasswordStrengthResult, Int) -> String,
    onSaveRecord: (String, PasswordStrengthResult, Int, String) -> Unit,
    onNavigateGenerator: () -> Unit,
    onNavigateHistory: () -> Unit
) {
    var password by remember { mutableStateOf(initialPassword) }
    var isPasswordVisible by remember { mutableStateOf(false) }

    val evalResult = remember(password) {
        PasswordStrengthEvaluator.evaluate(password)
    }

    var breachCount by remember { mutableStateOf<Int?>(null) }
    var isCheckingLeak by remember { mutableStateOf(false) }

    var aiAdviceText by remember { mutableStateOf("") }
    var isAnalyzingAi by remember { mutableStateOf(false) }

    var isSaved by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    val progressColor by animateColorAsState(
        targetValue = when {
            evalResult.score >= 80 -> Color(0xFF10B981) // Emerald
            evalResult.score >= 60 -> Color(0xFF3B82F6) // Blue
            evalResult.score >= 40 -> Color(0xFFF59E0B) // Amber
            evalResult.score >= 20 -> Color(0xFFF97316) // Orange
            else -> Color(0xFFEF4444) // Red
        },
        animationSpec = tween(400),
        label = "color"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🛡️ AI Password Analyzer", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                actions = {
                    IconButton(onClick = onNavigateGenerator) {
                        Icon(Icons.Default.Key, contentDescription = "Generator")
                    }
                    IconButton(onClick = onNavigateHistory) {
                        Icon(Icons.Default.History, contentDescription = "History")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    breachCount = null
                    aiAdviceText = ""
                    isSaved = false
                },
                label = { Text("Enter password to check") },
                placeholder = { Text("e.g., MyP@ssw0rd!2026") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = if (isPasswordVisible) KeyboardType.Text else KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle Visibility"
                        )
                    }
                },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()
            )

            // Strength Score Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Strength: ${evalResult.level}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = progressColor
                        )
                        Text(
                            text = "${evalResult.score} / 100",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = progressColor
                        )
                    }

                    LinearProgressIndicator(
                        progress = { (evalResult.score / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = progressColor,
                        trackColor = MaterialTheme.colorScheme.outlineVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Length: ${evalResult.length} chars", fontSize = 13.sp)
                        Text(text = "Entropy: ${"%.1f".format(evalResult.entropyBits)} bits", fontSize = 13.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        CompositionBadge(label = "A-Z", active = evalResult.hasUppercase)
                        CompositionBadge(label = "a-z", active = evalResult.hasLowercase)
                        CompositionBadge(label = "0-9", active = evalResult.hasDigits)
                        CompositionBadge(label = "!@#", active = evalResult.hasSymbols)
                    }

                    if (evalResult.warnings.isNotEmpty()) {
                        HorizontalDivider()
                        evalResult.warnings.forEach { warning ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = warning, fontSize = 12.sp, color = Color(0xFFD32F2F))
                            }
                        }
                    }
                }
            }

            // HIBP Leak Checker
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "🌐 Data Breach Check (HIBP API)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "Uses k-Anonymity (only sends the first 5 characters of SHA-1 hash) for maximum security. Your actual password is never sent.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                    )

                    Button(
                        onClick = {
                            if (password.isBlank()) return@Button
                            isCheckingLeak = true
                            scope.launch {
                                breachCount = onCheckLeak(password)
                                isCheckingLeak = false
                            }
                        },
                        enabled = password.isNotBlank() && !isCheckingLeak,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isCheckingLeak) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Checking databases...")
                        } else {
                            Icon(Icons.Default.Security, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Check Data Breaches")
                        }
                    }

                    breachCount?.let { count ->
                        HorizontalDivider()
                        if (count > 0) {
                            Text(
                                text = "⚠️ Found in $count data breaches!",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEF4444),
                                fontSize = 14.sp
                            )
                        } else {
                            Text(
                                text = "✅ No breaches found (Safe)",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // Gemini AI Advice Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "🤖 AI Security Analysis",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )

                    Button(
                        onClick = {
                            if (password.isBlank()) return@Button
                            isAnalyzingAi = true
                            aiAdviceText = "🤖 Analyzing security recommendations with AI..."
                            scope.launch {
                                val result = onAnalyzeAi(password, evalResult, breachCount ?: 0)
                                aiAdviceText = result
                                isAnalyzingAi = false
                            }
                        },
                        enabled = password.isNotBlank() && !isAnalyzingAi,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isAnalyzingAi) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Analyzing with AI...")
                        } else {
                            Icon(Icons.Default.SmartToy, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Analyze with AI")
                        }
                    }

                    if (aiAdviceText.isNotBlank()) {
                        HorizontalDivider()
                        Text(
                            text = aiAdviceText,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }

            // Save Entry
            if (password.isNotBlank()) {
                Button(
                    onClick = {
                        onSaveRecord(
                            password,
                            evalResult,
                            breachCount ?: 0,
                            if (aiAdviceText.isNotBlank()) aiAdviceText else "Evaluated successfully"
                        )
                        isSaved = true
                    },
                    enabled = !isSaved,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(imageVector = if (isSaved) Icons.Default.Check else Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = if (isSaved) "Saved to Vault!" else "Save to Vault", fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun CompositionBadge(label: String, active: Boolean) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (active) Color(0xFF10B981).copy(alpha = 0.15f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (active) Icons.Default.CheckCircle else Icons.Default.Cancel,
                contentDescription = null,
                tint = if (active) Color(0xFF10B981) else MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (active) Color(0xFF10B981) else MaterialTheme.colorScheme.outline
            )
        }
    }
}
