package com.example.afinal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoredActivityScreen(
    onBack: () -> Unit,
    onFetchActivity: suspend () -> Pair<String, String>,
    onSaveResult: (String, String, String) -> Unit
) {
    var activityText by remember { mutableStateOf("กดปุ่มด้านล่างเพื่อสุ่มกิจกรรมแก้เบื่อจาก API 🎲") }
    var activityMeta by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val fetchAction: () -> Unit = {
        isLoading = true
        scope.launch {
            val result = onFetchActivity()
            activityText = result.first
            activityMeta = result.second
            isLoading = false
            onSaveResult("สุ่มกิจกรรมแก้เบื่อ", "Bored API", "${result.first} (${result.second})")
        }
    }

    LaunchedEffect(Unit) {
        fetchAction()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🎲 สุ่มกิจกรรมแก้เบื่อ (Bored API)") },
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
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (isLoading) "กำลังสุ่มกิจกรรม..." else activityText,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        if (activityMeta.isNotEmpty() && !isLoading) {
                            Text(
                                text = activityMeta,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = fetchAction,
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = if (isLoading) "กำลังสุ่ม..." else "สุ่มกิจกรรมใหม่!", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
