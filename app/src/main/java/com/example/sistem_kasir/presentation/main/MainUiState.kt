package com.example.sistem_kasir.presentation.main

import com.example.sistem_kasir.domain.model.CartItem
import com.example.sistem_kasir.domain.model.Product

data class MainUiState(
    val products : List<Product> = emptyList(),
    val isLoading : Boolean = false,
    val error : String? = null
)