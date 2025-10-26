// domain/usecase/product/GetProductByIdUseCase.kt
package com.example.sistem_kasir.domain.usecase.product

import com.example.sistem_kasir.domain.model.Product
import com.example.sistem_kasir.domain.repository.ProductRepository
import javax.inject.Inject

class GetProductByIdUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(productId: Long): Product? {
        return repository.getProductById(productId)
    }
}