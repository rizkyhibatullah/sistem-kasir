package com.example.sistem_kasir.domain.usecase.sale

import com.example.sistem_kasir.domain.model.Sale
import com.example.sistem_kasir.domain.repository.SaleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllSalesUseCase @Inject constructor(
    private val repository: SaleRepository
) {
    operator fun invoke(): Flow<List<Sale>> {
        return repository.getAllSales()
    }
}