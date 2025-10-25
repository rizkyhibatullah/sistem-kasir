// presentation/screens/report/ReportViewModel.kt
package com.example.sistem_kasir.presentation.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sistem_kasir.domain.usecase.sale.GetDailySalesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val getDailySalesUseCase: GetDailySalesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportUiState())
    val uiState: StateFlow<ReportUiState> = _uiState

    private var currentDaysAgo = 0

    init {
        loadSales(currentDaysAgo)
    }

    fun loadSales(daysAgo: Int) {
        currentDaysAgo = daysAgo
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)
        val startOfDay = calendar.timeInMillis / 86400000 * 86400000 // Mulai jam 00:00
        val endOfDay = startOfDay + 86400000 - 1 // Sampai jam 23:59:59

        viewModelScope.launch {
            // Untuk MVP, kita ambil semua lalu filter
            getDailySalesUseCase().collect { allSales ->
                val filteredSales = allSales.filter { sale ->
                    sale.timestamp in startOfDay..endOfDay
                }
                processSales(filteredSales)
            }
        }
    }

    private fun processSales(sales: List<com.example.sistem_kasir.domain.model.Sale>) {
        val totalRevenue = sales.sumOf { it.totalAmount }
        val totalProfit = sales.sumOf { it.totalProfit }

        // Hitung produk terlaris
        val productCount = mutableMapOf<String, Int>()
        for (sale in sales) {
            for (item in sale.items) {
                productCount[item.productName] = productCount.getOrDefault(item.productName, 0) + item.quantity
            }
        }
        val topProducts = productCount.entries
            .sortedByDescending { it.value }
            .take(5)
            .map { it.key }

        _uiState.value = ReportUiState(
            sales = sales,
            totalRevenue = totalRevenue,
            totalProfit = totalProfit,
            topProducts = topProducts
        )
    }
}