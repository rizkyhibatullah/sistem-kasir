package com.example.sistem_kasir.domain.usecase.customer

import com.example.sistem_kasir.domain.model.Customer
import com.example.sistem_kasir.domain.repository.CustomerRepository
import javax.inject.Inject

class GetCustomerByIdUseCase @Inject constructor(
    private val repository: CustomerRepository
) {
    suspend operator fun invoke(customerId: Long): Customer? {
        return repository.getCustomerById(customerId)
    }
}