package com.example.sistem_kasir.domain.repository

import com.example.sistem_kasir.domain.model.Debt
import kotlinx.coroutines.flow.Flow

interface DebtRepository {
    fun getAllDebts(): Flow<List<Debt>>
    fun getDebtsByCustomer(customerId: Long): Flow<List<Debt>>
    suspend fun getDebtById(id: Long): Debt?
    suspend fun insertDebt(debt: Debt): Long
    suspend fun updateDebt(debt: Debt)
}