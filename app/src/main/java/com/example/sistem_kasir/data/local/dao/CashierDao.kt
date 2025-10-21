package com.example.sistem_kasir.data.local.dao

import androidx.room.*
import com.example.sistem_kasir.data.local.entity.Cashier
import kotlinx.coroutines.flow.Flow

@Dao
interface CashierDao {
    @Query("SELECT * FROM cashiers")
    fun getAllCashiers(): Flow<List<Cashier>>

    @Query("SELECT * FROM cashiers WHERE id = :id")
    suspend fun getCashierById(id: Long): Cashier?

    @Query("SELECT * FROM cashiers WHERE pinHash = :pinHash LIMIT 1")
    suspend fun getCashierByPin(pinHash: String): Cashier?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCashier(cashier: Cashier): Long

    @Update
    suspend fun updateCashier(cashier: Cashier)

    @Delete
    suspend fun deleteCashier(cashier: Cashier)
}