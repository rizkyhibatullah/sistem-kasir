package com.example.sistem_kasir.domain.model

data class Product(
    val id: Long = 0,
    val code: String = "",
    val name: String,
    val price: Long,
    val costPrice: Long = 0,
    val stock: Int = 0,
    val categoryId: Long,
    val categoryName: String = "", // untuk tampilan UI
    val imageUrl: String? = null
)
