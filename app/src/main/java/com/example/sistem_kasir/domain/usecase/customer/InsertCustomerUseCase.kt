package com.example.sistem_kasir.domain.usecase.customer

import com.example.sistem_kasir.domain.model.Customer
import com.example.sistem_kasir.domain.repository.CustomerRepository
import javax.inject.Inject

class InsertCustomerUseCase @Inject constructor(
    private val repository: CustomerRepository
) {
    suspend operator fun invoke(customer: Customer): Result<Long> {
        return try {
            val id = repository.insertCustomer(customer)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}