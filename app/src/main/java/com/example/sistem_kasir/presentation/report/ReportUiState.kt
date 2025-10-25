// presentation/screens/report/ReportUiState.kt
package com.example.sistem_kasir.presentation.report

import com.example.sistem_kasir.domain.model.Sale

data class ReportUiState(
    val sales: List<Sale> = emptyList(),
    val totalRevenue: Long = 0L,
    val totalProfit: Long = 0L,
    val topProducts: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)