package com.example.sistem_kasir.domain.model

data class Sale(
    val id: Long = 0,
    val cashierName: String,
    val customerId: Long? = null,
    val totalAmount: Long,
    val totalProfit: Long,
    val paymentMethod: String,
    val isDebt: Boolean = false,
    val timestamp: Long,
    val items: List<SaleItem>
)
