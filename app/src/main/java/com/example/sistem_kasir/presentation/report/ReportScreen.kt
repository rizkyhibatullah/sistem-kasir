// presentation/screens/report/ReportScreen.kt
package com.example.sistem_kasir.presentation.screens.report

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sistem_kasir.domain.model.Sale
import com.example.sistem_kasir.presentation.main.formatRupiah
import com.example.sistem_kasir.presentation.report.ReportViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReportViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Laporan Penjualan", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            // Ringkasan
            item {
                ReportSummary(
                    totalRevenue = uiState.totalRevenue,
                    totalProfit = uiState.totalProfit
                )
            }

            // Produk Terlaris
            if (uiState.topProducts.isNotEmpty()) {
                item {
                    Text("Produk Terlaris", style = MaterialTheme.typography.headlineSmall)
                }
                items(uiState.topProducts) { product ->
                    ProductItem(product = product)
                }
            }

            // Daftar Transaksi
            if (uiState.sales.isNotEmpty()) {
                item {
                    Text("Transaksi Hari Ini", style = MaterialTheme.typography.headlineSmall)
                }
                items(uiState.sales) { sale ->
                    SaleItem(sale = sale)
                }
            }

            // Jika tidak ada data
            if (uiState.sales.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Belum ada transaksi hari ini")
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportSummary(totalRevenue: Long, totalProfit: Long) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Total Omzet", style = MaterialTheme.typography.titleMedium)
                    Text(totalRevenue.formatRupiah(), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                }
                Column {
                    Text("Keuntungan", style = MaterialTheme.typography.titleMedium)
                    Text(totalProfit.formatRupiah(), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleLarge)
                }
            }
        }
    }
}

@Composable
private fun ProductItem(product: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(product, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun SaleItem(sale: Sale) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("ID: ${sale.id}", style = MaterialTheme.typography.bodyMedium)
                Text(sale.totalAmount.formatRupiah(), fontWeight = FontWeight.Bold)
            }
            Text("Kasir: ${sale.cashierName}", style = MaterialTheme.typography.bodySmall)
            Text("Metode: ${sale.paymentMethod}", style = MaterialTheme.typography.bodySmall)
        }
    }
}