// presentation/screens/report/ReportScreen.kt
package com.example.sistem_kasir.presentation.report

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Laporan Penjualan", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        Box(
            modifier = modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text("Laporan akan ditampilkan di sini", style = MaterialTheme.typography.titleLarge)
        }
    }
}