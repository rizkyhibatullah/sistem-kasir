package com.example.sistem_kasir.data.repository

import com.example.sistem_kasir.data.local.dao.DebtDao
import com.example.sistem_kasir.data.mapper.toDomain
import com.example.sistem_kasir.data.mapper.toEntity
import com.example.sistem_kasir.domain.model.Debt
import com.example.sistem_kasir.domain.repository.DebtRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DebtRepositoryImpl @Inject constructor(
    private val dao: DebtDao
) : DebtRepository {
    override fun getAllDebts(): Flow<List<Debt>> {
        return dao.getAllDebts().map { it.map { debt -> debt.toDomain() } }
    }

    override fun getDebtsByCustomer(customerId: Long): Flow<List<Debt>> {
        return dao.getDebtsByCustomer(customerId).map { it.map { debt -> debt.toDomain() } }
    }

    override suspend fun getDebtById(id: Long): Debt? {
        return dao.getDebtById(id)?.toDomain()
    }

    override suspend fun insertDebt(debt: Debt): Long {
        return dao.insertDebt(debt.toEntity())
    }

    override suspend fun updateDebt(debt: Debt) {
        dao.updateDebt(debt.toEntity())
    }

}