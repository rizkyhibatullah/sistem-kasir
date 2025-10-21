package com.example.sistem_kasir.domain.repository

import com.example.sistem_kasir.domain.model.Cashier
import kotlinx.coroutines.flow.Flow

interface CashierRepository {
    fun getAllCashiers(): Flow<List<Cashier>>
    suspend fun getCashierById(id: Long): Cashier?
    suspend fun authenticateCashier(pinHash: String): Cashier?
    suspend fun insertCashier(cashier: Cashier): Long
}