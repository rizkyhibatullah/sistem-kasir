// domain/usecase/product/UpdateProductUseCase.kt
package com.example.sistem_kasir.domain.usecase.product

import com.example.sistem_kasir.domain.model.Product
import com.example.sistem_kasir.domain.repository.ProductRepository
import javax.inject.Inject

class UpdateProductUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(product: Product): Result<Unit> {
        return try {
            repository.updateProduct(product)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}