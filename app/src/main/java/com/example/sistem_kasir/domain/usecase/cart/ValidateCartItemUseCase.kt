package com.example.sistem_kasir.domain.usecase.cart

import com.example.sistem_kasir.domain.model.CartItem
import com.example.sistem_kasir.domain.repository.ProductRepository
import javax.inject.Inject

class ValidateCartItemsUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(items: List<CartItem>): Result<Unit> {
        return try {
            for (item in items) {
                val product = productRepository.getProductById(item.productId)
                if (product == null) {
                    return Result.failure(Exception("Produk ${item.name} tidak ditemukan"))
                }
                if (product.stock < item.quantity) {
                    return Result.failure(Exception("Stok ${item.name} tidak cukup. Tersedia: ${product.stock}"))
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}