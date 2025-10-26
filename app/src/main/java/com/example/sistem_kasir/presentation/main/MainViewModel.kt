package com.example.sistem_kasir.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sistem_kasir.domain.model.Product
import com.example.sistem_kasir.domain.usecase.product.GetAllProductsUseCase
import com.example.sistem_kasir.presentation.main.MainUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getAllProductsUseCase: GetAllProductsUseCase
) : ViewModel() {

    private val _allProducts = MutableStateFlow<List<Product>>(emptyList())
    val allProducts: StateFlow<List<Product>> = _allProducts

    private val _filteredProducts = MutableStateFlow<List<Product>>(emptyList())
    val filteredProducts: StateFlow<List<Product>> = _filteredProducts

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState

    private var searchJob: Job? = null

    init {
        getAllProductsUseCase().onEach { products ->
            _allProducts.value = products
            _filteredProducts.value = products // Tampilkan semua saat pertama kali
            _uiState.value = _uiState.value.copy(products = products)
        }.launchIn(viewModelScope)
    }
    fun search(query: String) {
        // Batalkan pencarian sebelumnya
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(300) // Debounce 300ms
            val filtered = if (query.isBlank()) {
                _allProducts.value
            } else {
                _allProducts.value.filter { product ->
                    product.name.contains(query, ignoreCase = true) ||
                            product.code.contains(query, ignoreCase = true)
                }
            }
            _filteredProducts.value = filtered
        }
    }
}