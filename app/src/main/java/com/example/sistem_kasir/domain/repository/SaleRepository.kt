package com.example.sistem_kasir.domain.repository

import com.example.sistem_kasir.domain.model.Sale
import kotlinx.coroutines.flow.Flow

interface SaleRepository {
    fun getAllSales(): Flow<List<Sale>>
    suspend fun getSaleById(id: Long): Sale?
    suspend fun insertSale(sale: Sale): Long
}