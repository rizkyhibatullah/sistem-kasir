package com.example.sistem_kasir.data.local.dao

import androidx.room.*
import com.example.sistem_kasir.data.local.entity.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY name")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: Long): Product?

    @Query("SELECT * FROM products WHERE stock <= :threshold")
    fun getLowStockProducts(threshold: Int = 5): Flow<List<Product>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product): Long

    @Update
    suspend fun updateProduct(product: Product)

    @Transaction
    suspend fun updateStock(productId: Long, newStock: Int) {
        updateStockInternal(productId, newStock)
    }

    @Query("UPDATE products SET stock = :newStock WHERE id = :productId")
    suspend fun updateStockInternal(productId: Long, newStock: Int)

    @Delete
    suspend fun deleteProduct(product: Product)
}