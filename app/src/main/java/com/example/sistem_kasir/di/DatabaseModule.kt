package com.example.sistem_kasir.di

import android.content.Context
import com.example.sistem_kasir.data.local.AppDatabase
import com.example.sistem_kasir.data.local.dao.CashierDao
import com.example.sistem_kasir.data.local.dao.ProductDao
import com.example.sistem_kasir.data.local.dao.SaleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return AppDatabase.getDatabase(appContext)
    }

    @Provides
    @Singleton
    fun provideCashierDao(database: AppDatabase): CashierDao = database.cashierDao()

    @Provides
    @Singleton
    fun provideProductDao(database: AppDatabase): ProductDao = database.productDao()

    @Provides
    @Singleton
    fun provideSaleDao(database: AppDatabase): SaleDao = database.saleDao()
}