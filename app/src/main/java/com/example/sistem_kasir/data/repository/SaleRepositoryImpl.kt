package com.example.sistem_kasir.data.repository

import com.example.sistem_kasir.data.local.dao.ProductDao
import com.example.sistem_kasir.data.local.dao.SaleDao
import com.example.sistem_kasir.domain.model.Sale
import com.example.sistem_kasir.domain.model.SaleItem
import com.example.sistem_kasir.domain.repository.SaleRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SaleRepositoryImpl @Inject constructor(
    private val saleDao: SaleDao,
    private val productDao: ProductDao // 👈 tambahkan ini
) : SaleRepository {

    override fun getAllSales() = saleDao.getAllSalesWithItems().map { saleWithItemsList ->
        saleWithItemsList.map { saleWithItems ->
            val items = saleWithItems.items.map { item ->
                val product = productDao.getProductById(item.productId)
                SaleItem(
                    id = item.id,
                    saleId = item.saleId,
                    productId = item.productId,
                    productName = product?.name ?: "Produk Dihapus",
                    quantity = item.quantity,
                    priceAtSale = item.priceAtSale
                )
            }
            Sale(
                id = saleWithItems.sale.id,
                cashierName = "Kasir", // Nanti bisa ganti dengan join cashier
                totalAmount = saleWithItems.sale.totalAmount,
                totalProfit = saleWithItems.sale.totalProfit,
                paymentMethod = saleWithItems.sale.paymentMethod,
                timestamp = saleWithItems.sale.timestamp,
                items = items
            )
        }
    }

    override suspend fun getSaleById(id: Long): Sale? {
        val saleWithItems = saleDao.getSaleWithItemsById(id) ?: return null
        val items = saleWithItems.items.map { item ->
            val product = productDao.getProductById(item.productId)
            SaleItem(
                id = item.id,
                saleId = item.saleId,
                productId = item.productId,
                productName = product?.name ?: "Produk Dihapus",
                quantity = item.quantity,
                priceAtSale = item.priceAtSale
            )
        }
        return Sale(
            id = saleWithItems.sale.id,
            cashierName = "Kasir",
            totalAmount = saleWithItems.sale.totalAmount,
            totalProfit = saleWithItems.sale.totalProfit,
            paymentMethod = saleWithItems.sale.paymentMethod,
            timestamp = saleWithItems.sale.timestamp,
            items = items
        )
    }

    override suspend fun insertSale(sale: Sale): Long {
        val saleId = saleDao.insertSale(
            com.example.sistem_kasir.data.local.entity.Sale(
                id = sale.id,
                cashierId = 1, // nanti ganti dengan ID kasir aktif
                totalAmount = sale.totalAmount,
                totalProfit = sale.totalProfit,
                paymentMethod = sale.paymentMethod,
                timestamp = sale.timestamp
            )
        )
        sale.items.forEach { item ->
            saleDao.insertSaleItem(
                com.example.sistem_kasir.data.local.entity.SaleItem(
                    saleId = saleId,
                    productId = item.productId,
                    quantity = item.quantity,
                    priceAtSale = item.priceAtSale
                )
            )
        }
        return saleId
    }
}