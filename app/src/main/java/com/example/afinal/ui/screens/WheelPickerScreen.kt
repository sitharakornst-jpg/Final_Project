package com.example.afinal.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WheelPickerScreen(
    onBack: () -> Unit,
    onSaveResult: (String, String, String) -> Unit
) {
    var optionsInput by remember { mutableStateOf("กินหมูกระทะ, กินชาบู, ส้มตำแซ่บๆ, พิซซ่าชีสยืด") }
    var currentDisplay by remember { mutableStateOf("กดปุ่ม 'หมุนวงล้อสุ่ม' เพื่อเสี่ยงทาย!") }
    var isSpinning by remember { mutableStateOf(false) }
    var winningResult by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val scaleAnim = remember { Animatable(1f) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🎡 สุ่มตัวเลือก / วงล้อ") },
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = optionsInput,
                onValueChange = { optionsInput = it },
                label = { Text("กรอกตัวเลือก (คั่นด้วยเครื่องหมายจุลภาค ,)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                maxLines = 4
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentDisplay,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.scale(scaleAnim.value)
                    )
                }
            }

            Button(
                onClick = {
                    val list = optionsInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                    if (list.isEmpty()) {
                        currentDisplay = "กรุณากรอกตัวเลือกอย่างน้อย 1 รายการ!"
                        return@Button
                    }
                    isSpinning = true
                    winningResult = null
                    scope.launch {
                        repeat(15) {
                            currentDisplay = list.random()
                            scaleAnim.animateTo(1.1f, animationSpec = tween(50))
                            scaleAnim.animateTo(1f, animationSpec = tween(50))
                            delay(60)
                        }
                        val winner = list.random()
                        currentDisplay = "🎉 ผู้ชนะคือ: $winner 🎉"
                        winningResult = winner
                        isSpinning = false
                        scaleAnim.animateTo(1.3f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                        scaleAnim.animateTo(1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))

                        onSaveResult(
                            "สุ่มตัวเลือกจาก: ${list.joinToString(", ")}",
                            "Wheel",
                            winner
                        )
                    }
                },
                enabled = !isSpinning,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Casino, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = if (isSpinning) "กำลังหมุน..." else "หมุนวงล้อสุ่ม!", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            if (winningResult != null) {
                Text(
                    text = "✅ บันทึกลงประวัติเรียบร้อยแล้ว!",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
