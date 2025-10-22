package com.example.sistem_kasir.domain.usecase.product

import com.example.sistem_kasir.domain.model.Product
import com.example.sistem_kasir.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllProductsUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    operator fun invoke(): Flow<List<Product>> {
        return repository.getAllProducts()
    }
}