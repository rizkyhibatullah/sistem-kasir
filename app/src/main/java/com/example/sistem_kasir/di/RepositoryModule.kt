package com.example.sistem_kasir.di

import com.example.sistem_kasir.data.repository.CashierRepositoryImpl
import com.example.sistem_kasir.data.repository.CategoryRepositoryImpl
import com.example.sistem_kasir.data.repository.CustomerRepositoryImpl
import com.example.sistem_kasir.data.repository.DebtRepositoryImpl
import com.example.sistem_kasir.data.repository.ProductRepositoryImpl
import com.example.sistem_kasir.data.repository.SaleRepositoryImpl
import com.example.sistem_kasir.domain.repository.CashierRepository
import com.example.sistem_kasir.domain.repository.CategoryRepository
import com.example.sistem_kasir.domain.repository.CustomerRepository
import com.example.sistem_kasir.domain.repository.DebtRepository
import com.example.sistem_kasir.domain.repository.ProductRepository
import com.example.sistem_kasir.domain.repository.SaleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindCashierRepository(impl: CashierRepositoryImpl): CashierRepository

    @Binds
    abstract fun bindProductRepository(impl: ProductRepositoryImpl): ProductRepository

    @Binds
    abstract fun bindSaleRepository(impl: SaleRepositoryImpl): SaleRepository

    @Binds
    abstract fun bindCategoryRepository(impl: CategoryRepositoryImpl): CategoryRepository

    @Binds
    abstract fun bindCustomerRepository(impl: CustomerRepositoryImpl): CustomerRepository

    @Binds
    abstract fun bindDebtRepository(impl: DebtRepositoryImpl): DebtRepository
}