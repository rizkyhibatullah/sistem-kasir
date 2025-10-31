package com.example.sistem_kasir.domain.model

data class Customer(
    val id: Long = 0,
    val name: String,
    val phone: String = "",
    val address: String = "",
    val createdAt: Long = System.currentTimeMillis()
)