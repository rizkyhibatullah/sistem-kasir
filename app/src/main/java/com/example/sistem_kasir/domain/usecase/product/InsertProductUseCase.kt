// domain/usecase/product/InsertProductUseCase.kt
package com.example.sistem_kasir.domain.usecase.product

import com.example.sistem_kasir.domain.model.Product
import com.example.sistem_kasir.domain.repository.ProductRepository
import javax.inject.Inject

class InsertProductUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(product: Product): Result<Long> {
        return try {
            val id = repository.insertProduct(product)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}