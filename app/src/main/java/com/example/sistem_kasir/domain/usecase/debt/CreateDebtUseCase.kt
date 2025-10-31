package com.example.sistem_kasir.domain.usecase.debt

import com.example.sistem_kasir.domain.model.Debt
import com.example.sistem_kasir.domain.repository.DebtRepository
import javax.inject.Inject

class CreateDebtUseCase @Inject constructor(
    private val repository: DebtRepository
) {
    suspend operator fun invoke(debt: Debt): Result<Long> {
        return try {
            // Validasi
            if (debt.totalAmount <= 0) {
                return Result.failure(IllegalArgumentException("Total hutang harus lebih dari 0"))
            }
            if (debt.remainingAmount != debt.totalAmount) {
                return Result.failure(IllegalArgumentException("Sisa hutang harus sama dengan total saat dibuat"))
            }

            val id = repository.insertDebt(debt)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}