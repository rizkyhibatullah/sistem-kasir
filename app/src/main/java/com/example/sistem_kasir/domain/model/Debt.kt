package com.example.sistem_kasir.domain.model

data class Debt(
    val id: Long = 0,
    val customerId: Long,
    val saleId: Long,
    val totalAmount: Long,
    val remainingAmount: Long,
    val status: String, // "PENDING", "PAID", "PARTIAL"
    val dueDate: Long = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)