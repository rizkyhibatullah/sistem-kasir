package com.example.sistem_kasir.domain.usecase.debt

import com.example.sistem_kasir.domain.model.Debt
import com.example.sistem_kasir.domain.repository.DebtRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDebtsByCustomerUseCase @Inject constructor(
    private val repository: DebtRepository
) {
    operator fun invoke(customerId: Long): Flow<List<Debt>> {
        return repository.getDebtsByCustomer(customerId)
    }
}