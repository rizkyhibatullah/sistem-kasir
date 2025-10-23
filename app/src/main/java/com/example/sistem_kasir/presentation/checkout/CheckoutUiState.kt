package com.example.sistem_kasir.presentation.checkout

import com.example.sistem_kasir.domain.model.CartItem

data class CheckoutUiState(
    val cartItems : List<CartItem> = emptyList(),
    val paymentMethod : String = "CASH",
    val cashGiven: Long = 0L,
    val isProcessing : Boolean = false,
    val error : String? = null,
    val success : Boolean = false
)
