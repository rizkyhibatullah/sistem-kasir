package com.example.sistem_kasir.domain.model

data class SaleItem(
    val id: Long = 0,
    val saleId: Long,
    val productId: Long,
    val productName: String, // untuk struk & laporan
    val quantity: Int,
    val priceAtSale: Long
)
