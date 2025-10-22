package com.example.sistem_kasir.domain.model

data class CartItem(
    val productId: Long,
    val name: String,
    val price: Long,
    val costPrice: Long,
    val quantity: Int
)
