package com.example.afinal.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.afinal.data.utils.PasswordGenerator
import com.example.afinal.data.utils.PasswordStrengthEvaluator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordGeneratorScreen(
    onBack: () -> Unit,
    onSendToAnalyzer: (String) -> Unit
) {
    val context = LocalContext.current
    var length by remember { mutableFloatStateOf(16f) }
    var includeUpper by remember { mutableStateOf(true) }
    var includeLower by remember { mutableStateOf(true) }
    var includeDigits by remember { mutableStateOf(true) }
    var includeSymbols by remember { mutableStateOf(true) }
    var excludeSimilar by remember { mutableStateOf(true) }

    var generatedPassword by remember {
        mutableStateOf(
            PasswordGenerator.generate(
                length = 16,
                includeUpper = true,
                includeLower = true,
                includeDigits = true,
                includeSymbols = true,
                excludeSimilar = true
            )
        )
    }

    val evalResult = remember(generatedPassword) {
        PasswordStrengthEvaluator.evaluate(generatedPassword)
    }

    val generateAction = {
        generatedPassword = PasswordGenerator.generate(
            length = length.toInt(),
            includeUpper = includeUpper,
            includeLower = includeLower,
            includeDigits = includeDigits,
            includeSymbols = includeSymbols,
            excludeSimilar = excludeSimilar
        )
    }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("⚡ Password Generator") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
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
            // Password Display Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = generatedPassword,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = true,
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Password", generatedPassword)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Password copied!", Toast.LENGTH_SHORT).show()
                            },
                            label = { Text("Copy") },
                            leadingIcon = { Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        )

                        FilterChip(
                            selected = true,
                            onClick = { onSendToAnalyzer(generatedPassword) },
                            label = { Text("Analyze") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        )
                    }
                }
            }

            // Strength Badge
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Security Level: ${evalResult.level}", fontWeight = FontWeight.SemiBold)
                    Text(text = "${evalResult.score}/100", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }

            // Controls
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Password Length:", fontWeight = FontWeight.Bold)
                        Text(text = "${length.toInt()} chars", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }

                    Slider(
                        value = length,
                        onValueChange = {
                            length = it
                            generateAction()
                        },
                        valueRange = 8f..64f,
                        steps = 55
                    )

                    HorizontalDivider()

                    OptionRow("Uppercase (A-Z)", includeUpper) {
                        includeUpper = it
                        generateAction()
                    }
                    OptionRow("Lowercase (a-z)", includeLower) {
                        includeLower = it
                        generateAction()
                    }
                    OptionRow("Digits (0-9)", includeDigits) {
                        includeDigits = it
                        generateAction()
                    }
                    OptionRow("Symbols (!@#$%^&*)", includeSymbols) {
                        includeSymbols = it
                        generateAction()
                    }
                    OptionRow("Exclude similar characters (i, l, 1, L, o, 0, O)", excludeSimilar) {
                        excludeSimilar = it
                        generateAction()
                    }
                }
            }

            Button(
                onClick = generateAction,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Generate Password", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun OptionRow(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 14.sp)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
