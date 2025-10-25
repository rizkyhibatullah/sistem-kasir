// data/mapper/SaleMapper.kt
package com.example.sistem_kasir.data.mapper

import com.example.sistem_kasir.data.local.dao.SaleDao
import com.example.sistem_kasir.domain.model.Sale
import com.example.sistem_kasir.domain.model.SaleItem

suspend fun SaleDao.SaleWithItems.toDomain(
    productDao: com.example.sistem_kasir.data.local.dao.ProductDao
): Sale {
    val items = items.map { item ->
        val product = productDao.getProductById(item.productId)
        SaleItem(
            id = item.id,
            productId = item.productId,
            productName = product?.name ?: "Produk Dihapus",
            quantity = item.quantity,
            priceAtSale = item.priceAtSale
        )
    }
    return Sale(
        id = sale.id,
        cashierName = "Kasir", // Nanti bisa ganti dengan join cashier
        totalAmount = sale.totalAmount,
        totalProfit = sale.totalProfit,
        paymentMethod = sale.paymentMethod,
        timestamp = sale.timestamp,
        items = items
    )
}