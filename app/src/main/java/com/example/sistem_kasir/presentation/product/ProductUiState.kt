// presentation/screens/product/ProductUiState.kt
package com.example.sistem_kasir.presentation.product

import com.example.sistem_kasir.domain.model.Product

data class ProductUiState(
    val products: List<Product> = emptyList(),
    val filteredProducts: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isProcessing: Boolean = false
)