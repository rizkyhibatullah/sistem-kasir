package com.example.sistem_kasir.domain.model

data class Product(
    val id: Long = 0,
    val code: String = "",
    val name: String = "",
    val price: Long = 0L,
    val costPrice: Long = 0,
    val stock: Int = 0,
    val categoryId: Long = 1L,
    val categoryName: String = "", // untuk tampilan UI
    val imageUrl: String? = null
)
