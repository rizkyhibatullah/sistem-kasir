package com.example.sistem_kasir.domain.model

data class Sale(
    val id: Long = 0,
    val cashierName: String,
    val totalAmount: Long,
    val totalProfit: Long,
    val paymentMethod: String,
    val timestamp: Long,
    val items: List<SaleItem>
)
