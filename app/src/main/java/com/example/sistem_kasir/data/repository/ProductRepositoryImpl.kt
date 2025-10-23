package com.example.sistem_kasir.data.repository

import com.example.sistem_kasir.data.local.dao.ProductDao
import com.example.sistem_kasir.data.mapper.toDomain
import com.example.sistem_kasir.data.mapper.toEntity
import com.example.sistem_kasir.domain.model.Product
import com.example.sistem_kasir.domain.repository.ProductRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val dao: ProductDao
) : ProductRepository {

    override fun getAllProducts() = dao.getAllProducts().map { list ->
        list.map { it.toDomain() }
    }

    override suspend fun getProductById(id: Long) = dao.getProductById(id)?.toDomain()

    override fun getLowStockProducts(threshold: Int) = dao.getLowStockProducts(threshold).map { list ->
        list.map { it.toDomain() }
    }

    override suspend fun insertProduct(product: Product): Long {
        return dao.insertProduct(product.toEntity())
    }

    override suspend fun updateStock(productId: Long, newStock: Int) {
        dao.updateStock(productId, newStock)
    }

    override suspend fun updateProduct(product: Product) {
        dao.updateProduct(product.toEntity())
    }

    override suspend fun deleteProduct(productId: Long) {
        val product = dao.getProductById(productId) ?: return
        dao.deleteProduct(product)
    }
}