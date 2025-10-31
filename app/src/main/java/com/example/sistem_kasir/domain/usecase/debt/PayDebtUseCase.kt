package com.example.sistem_kasir.domain.usecase.debt

import com.example.sistem_kasir.domain.repository.DebtRepository
import javax.inject.Inject

class PayDebtUseCase @Inject constructor(
    private val repository: DebtRepository
) {
    suspend operator fun invoke(debtId: Long, paymentAmount: Long): Result<Unit> {
        return try {
            val debt = repository.getDebtById(debtId) ?: return Result.failure(Exception("Hutang tidak ditemukan"))

            if (paymentAmount <= 0) {
                return Result.failure(IllegalArgumentException("Jumlah bayar harus lebih dari 0"))
            }
            if (paymentAmount > debt.remainingAmount) {
                return Result.failure(IllegalArgumentException("Jumlah bayar melebihi sisa hutang"))
            }

            val newRemaining = debt.remainingAmount - paymentAmount
            val newStatus = when {
                newRemaining == 0L -> "PAID"
                newRemaining < debt.totalAmount -> "PARTIAL"
                else -> "PENDING"
            }

            val updatedDebt = debt.copy(
                remainingAmount = newRemaining,
                status = newStatus,
                updatedAt = System.currentTimeMillis()
            )

            repository.updateDebt(updatedDebt)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}