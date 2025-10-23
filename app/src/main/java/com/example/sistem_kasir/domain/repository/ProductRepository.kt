package com.example.sistem_kasir.domain.repository

import com.example.sistem_kasir.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getAllProducts(): Flow<List<Product>>
    suspend fun getProductById(id: Long): Product?
    fun getLowStockProducts(threshold: Int = 5): Flow<List<Product>>
    suspend fun insertProduct(product: Product): Long
    suspend fun updateStock(productId: Long, newStock: Int)
    suspend fun updateProduct(product: Product)
    suspend fun deleteProduct(productId: Long)
}