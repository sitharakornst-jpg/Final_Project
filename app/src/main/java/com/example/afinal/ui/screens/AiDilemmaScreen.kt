package com.example.afinal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiDilemmaScreen(
    onBack: () -> Unit,
    onAskGemini: suspend (String, String) -> String,
    onSaveResult: (String, String, String) -> Unit
) {
    var question by remember { mutableStateOf("เย็นนี้กินอะไรดีระหว่างชาบูหมูกระทะกับส้มตำแซ่บๆ?") }
    var apiKey by remember { mutableStateOf("") }
    var aiResult by remember { mutableStateOf("กดปุ่ม 'ให้ Gemini AI ตัดสินใจ' เพื่อเริ่มปรึกษาผู้ช่วยอัจฉริยะ 🤖") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🤖 AI ผู้ช่วยตัดสินใจ (Gemini)") },
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
            OutlinedTextField(
                value = apiKey,
                onValueChange = { apiKey = it },
                label = { Text("Gemini API Key (เว้นว่างไว้ใช้โหมด AI อัตโนมัติ)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = question,
                onValueChange = { question = it },
                label = { Text("พิมพ์เรื่องที่คุณลังเลอยู่ตอนนี้...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                maxLines = 4
            )

            Button(
                onClick = {
                    if (question.isBlank()) return@Button
                    isLoading = true
                    aiResult = "🤖 กำลังให้ Gemini AI วิเคราะห์ข้อดี-ข้อเสีย..."
                    scope.launch {
                        val result = onAskGemini(apiKey, question)
                        aiResult = result
                        isLoading = false
                        onSaveResult(question, "AI", result)
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.SmartToy, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = if (isLoading) "กำลังวิเคราะห์..." else "ให้ Gemini AI ตัดสินใจ!", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "ผลการวิเคราะห์จาก AI:",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 16.sp
                    )
                    HorizontalDivider()
                    Text(
                        text = aiResult,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
