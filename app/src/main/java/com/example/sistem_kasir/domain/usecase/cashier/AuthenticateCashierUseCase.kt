package com.example.sistem_kasir.domain.usecase.cashier

import com.example.sistem_kasir.domain.model.Cashier
import com.example.sistem_kasir.domain.repository.CashierRepository
import javax.inject.Inject

class AuthenticateCashierUseCase @Inject constructor(
    private val repository: CashierRepository
) {
    suspend operator fun invoke(pin: String): Result<Cashier> {
        return try {
            // 🔐 Hash PIN sederhana (untuk produksi, gunakan bcrypt/scrypt)
            val pinHash = pin.hashCode().toString()
            val cashier = repository.authenticateCashier(pinHash)
            if (cashier != null) {
                Result.success(cashier)
            } else {
                Result.failure(Exception("PIN salah"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}