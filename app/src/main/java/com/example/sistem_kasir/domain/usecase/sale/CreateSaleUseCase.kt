package com.example.sistem_kasir.domain.usecase.sale

import com.example.sistem_kasir.domain.model.CartItem
import com.example.sistem_kasir.domain.model.Sale
import com.example.sistem_kasir.domain.model.SaleItem
import com.example.sistem_kasir.domain.repository.ProductRepository
import com.example.sistem_kasir.domain.repository.SaleRepository
import javax.inject.Inject

class CreateSaleUseCase @Inject constructor(
    private val saleRepository: SaleRepository,
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(
        cashierId: Long,
        customerId: Long? = null,
        items: List<CartItem>,
        paymentMethod: String,
        isDebt: Boolean = false
    ): Result<Long> {
        return try {
            // 1. Validasi stok (double-check)
            for (item in items) {
                val product = productRepository.getProductById(item.productId)
                    ?: return Result.failure(Exception("Produk tidak valid"))
                if (product.stock < item.quantity) {
                    return Result.failure(Exception("Stok ${item.name} tidak cukup"))
                }
            }

            // 2. Hitung total
            var totalAmount = 0L
            var totalProfit = 0L
            val saleItems = mutableListOf<SaleItem>()
            for (item in items) {
                totalAmount += item.price * item.quantity
                totalProfit += (item.price - item.costPrice) * item.quantity
                saleItems.add(
                    SaleItem(
                        productId = item.productId,
                        productName = item.name,
                        quantity = item.quantity,
                        priceAtSale = item.price
                    )
                )
            }

            // 3. Buat objek Sale
            val sale = Sale(
                cashierName = "Kasir", // Nanti ganti dengan nama asli
                totalAmount = totalAmount,
                totalProfit = totalProfit,
                paymentMethod = paymentMethod,
                timestamp = System.currentTimeMillis(),
                items = saleItems
            )

            // 4. Simpan transaksi
            val saleId = saleRepository.insertSale(sale)

            // 5. Kurangi stok (setelah transaksi sukses)
            for (item in items) {
                val current = productRepository.getProductById(item.productId)!!
                productRepository.updateStock(item.productId, current.stock - item.quantity)
            }

            Result.success(saleId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}