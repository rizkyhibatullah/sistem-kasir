// domain/usecase/product/DeleteProductUseCase.kt
package com.example.sistem_kasir.domain.usecase.product

import com.example.sistem_kasir.domain.repository.ProductRepository
import javax.inject.Inject

class DeleteProductUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(productId: Long): Result<Unit> {
        return try {
            repository.deleteProduct(productId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}