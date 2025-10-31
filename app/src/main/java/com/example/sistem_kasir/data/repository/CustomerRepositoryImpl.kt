package com.example.sistem_kasir.data.repository

import com.example.sistem_kasir.data.local.dao.CustomerDao
import com.example.sistem_kasir.data.mapper.toDomain
import com.example.sistem_kasir.data.mapper.toEntity
import com.example.sistem_kasir.domain.model.Customer
import com.example.sistem_kasir.domain.repository.CustomerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CustomerRepositoryImpl @Inject constructor(
    private val dao: CustomerDao
) : CustomerRepository {
    override fun getAllCustomers(): Flow<List<Customer>> {
        return dao.getAllCustomers().map { it.map { customer -> customer.toDomain() } }
    }

    override suspend fun getCustomerById(id: Long): Customer? {
        return dao.getCustomerById(id)?.toDomain()
    }

    override suspend fun insertCustomer(customer: Customer): Long {
        return dao.insertCustomer(customer.toEntity())
    }

    override suspend fun updateCustomer(customer: Customer) {
        dao.updateCustomer(customer.toEntity())
    }

    override suspend fun deleteCustomer(id: Long) {
        val customer = dao.getCustomerById(id) ?: return
        dao.deleteCustomer(customer)
    }


}