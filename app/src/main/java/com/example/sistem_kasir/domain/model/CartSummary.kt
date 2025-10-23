package com.example.sistem_kasir.domain.model

data class CartSummary(
    val subTotal: Long,
    val totalProfit: Long,
    val itemCount: Int
)
