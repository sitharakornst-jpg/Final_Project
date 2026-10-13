package com.example.afinal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateWheel: () -> Unit,
    onNavigateAi: () -> Unit,
    onNavigateBored: () -> Unit,
    onNavigateHistory: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🎯 DecisionMaker AI", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "เลือกโหมดผู้ช่วยตัดสินใจของคุณ 👇",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MenuCard(
                    modifier = Modifier.weight(1f),
                    title = "สุ่มตัวเลือก / วงล้อ",
                    description = "กรอกตัวเลือกแล้วให้แอปสุ่มให้ทันที",
                    icon = Icons.Default.Refresh,
                    color1 = Color(0xFF6200EE),
                    color2 = Color(0xFFBB86FC),
                    onClick = onNavigateWheel
                )
                MenuCard(
                    modifier = Modifier.weight(1f),
                    title = "AI วิเคราะห์โจทย์",
                    description = "ให้ Gemini AI วิเคราะห์ Pros & Cons",
                    icon = Icons.Default.SmartToy,
                    color1 = Color(0xFF03DAC6),
                    color2 = Color(0xFF018786),
                    onClick = onNavigateAi
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MenuCard(
                    modifier = Modifier.weight(1f),
                    title = "สุ่มกิจกรรมแก้เบื่อ",
                    description = "เชื่อมต่อ API แนะนำกิจกรรมฟรี",
                    icon = Icons.Default.SportsEsports,
                    color1 = Color(0xFFFF6F00),
                    color2 = Color(0xFFFFCA28),
                    onClick = onNavigateBored
                )
                MenuCard(
                    modifier = Modifier.weight(1f),
                    title = "ประวัติการตัดสินใจ",
                    description = "ดูบันทึกย้อนหลัง (Room DB)",
                    icon = Icons.Default.History,
                    color1 = Color(0xFFB00020),
                    color2 = Color(0xFFCF6679),
                    onClick = onNavigateHistory
                )
            }
        }
    }
}

@Composable
fun MenuCard(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    icon: ImageVector,
    color1: Color,
    color2: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(160.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(listOf(color1, color2)))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
                Column {
                    Text(
                        text = title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = description,
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 12.sp,
                        maxLines = 2
                    )
                }
            }
        }
    }
}
