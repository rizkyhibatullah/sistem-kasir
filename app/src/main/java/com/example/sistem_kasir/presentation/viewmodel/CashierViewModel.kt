package com.example.sistem_kasir.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sistem_kasir.domain.model.Cashier
import com.example.sistem_kasir.domain.model.Category
import com.example.sistem_kasir.domain.repository.CashierRepository
import com.example.sistem_kasir.domain.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CashierViewModel @Inject constructor(
    private val cashierRepository: CashierRepository,
    private val categoryRepository: CategoryRepository // 👈 tambahkan ini
) : ViewModel() {

    fun createDefaultCashierIfNeeded() {
        viewModelScope.launch {
            // 1. Buat kategori default
            val categories = categoryRepository.getAllCategories().first()
            if (categories.isEmpty()) {
                val defaultCategory = Category(id = 1L, name = "Umum")
                categoryRepository.insertCategory(defaultCategory)
            }

            // 2. Buat kasir default
            val cashiers = cashierRepository.getAllCashiers().first()
            if (cashiers.isEmpty()) {
                val defaultCashier = Cashier(
                    id = 1L,
                    name = "Kasir Utama",
                    pinHash = "1234".hashCode().toString()
                )
                cashierRepository.insertCashier(defaultCashier)
            }
        }
    }
}