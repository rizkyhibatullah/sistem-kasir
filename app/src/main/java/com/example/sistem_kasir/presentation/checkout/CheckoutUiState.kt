package com.example.sistem_kasir.presentation.checkout

import com.example.sistem_kasir.domain.model.CartItem
import com.example.sistem_kasir.domain.model.Customer

data class CheckoutUiState(
    val cartItems : List<CartItem> = emptyList(),
    val paymentMethod : String = "CASH",
    val cashGiven: Long = 0L,
    val customers: List<Customer> = emptyList(),
    val selectedCustomer: Customer? = null,
    val newCustomerName: String = "",
    val newCustomerPhone: String = "",
    val isAddingNewCustomer: Boolean = false,
    val isProcessing : Boolean = false,
    val error : String? = null,
    val success : Boolean = false
)
