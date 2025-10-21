package com.example.sistem_kasir.data.repository

import com.example.sistem_kasir.data.local.dao.CashierDao
import com.example.sistem_kasir.data.mapper.toDomain
import com.example.sistem_kasir.data.mapper.toEntity
import com.example.sistem_kasir.domain.model.Cashier
import com.example.sistem_kasir.domain.repository.CashierRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CashierRepositoryImpl @Inject constructor(
    private val dao: CashierDao
) : CashierRepository {

    override fun getAllCashiers() = dao.getAllCashiers().map { list -> list.map { it.toDomain() } }

    override suspend fun getCashierById(id: Long) = dao.getCashierById(id)?.toDomain()

    override suspend fun authenticateCashier(pinHash: String) = dao.getCashierByPin(pinHash)?.toDomain()

    override suspend fun insertCashier(cashier: Cashier): Long {
        return dao.insertCashier(cashier.toEntity())
    }
}