package com.example.sistem_kasir.domain.usecase.customer

import com.example.sistem_kasir.domain.model.Customer
import com.example.sistem_kasir.domain.repository.CustomerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllCustomersUseCase @Inject constructor(
    private val repository: CustomerRepository
) {
    operator fun invoke(): Flow<List<Customer>> {
        return repository.getAllCustomers()
    }
}