package com.example.sistem_kasir.data.local.dao

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import com.example.sistem_kasir.data.local.entity.Sale
import com.example.sistem_kasir.data.local.entity.SaleItem
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleDao {
    @Transaction
    @Query("SELECT * FROM sales ORDER BY timestamp DESC")
    fun getAllSalesWithItems(): Flow<List<SaleWithItems>>

    @Transaction
    @Query("SELECT * FROM sales WHERE id = :saleId")
    suspend fun getSaleWithItemsById(saleId: Long): SaleWithItems?

    @Insert
    suspend fun insertSale(sale: Sale): Long

    @Insert
    suspend fun insertSaleItem(item: SaleItem)

    @Query("SELECT * FROM sales WHERE timestamp BETWEEN :start AND :end ORDER BY timestamp DESC")
    fun getSalesByDateRange(start: Long, end: Long): Flow<List<SaleWithItems>>

    // Helper class untuk join
    data class SaleWithItems(
        @Embedded val sale: Sale,
        @Relation(
            parentColumn = "id",
            entityColumn = "saleId"
        )
        val items: List<SaleItem>
    )
}