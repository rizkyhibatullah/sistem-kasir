package com.example.sistem_kasir.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.sistem_kasir.data.local.dao.CashierDao
import com.example.sistem_kasir.data.local.dao.CategoryDao
import com.example.sistem_kasir.data.local.dao.CustomerDao
import com.example.sistem_kasir.data.local.dao.DebtDao
import com.example.sistem_kasir.data.local.dao.ProductDao
import com.example.sistem_kasir.data.local.dao.SaleDao
import com.example.sistem_kasir.data.local.entity.Cashier
import com.example.sistem_kasir.data.local.entity.Category
import com.example.sistem_kasir.data.local.entity.Customer
import com.example.sistem_kasir.data.local.entity.Debt
import com.example.sistem_kasir.data.local.entity.Product
import com.example.sistem_kasir.data.local.entity.Sale
import com.example.sistem_kasir.data.local.entity.SaleItem

@Database(
    entities = [Cashier::class, Category::class, Product::class, Sale::class, SaleItem::class, Customer::class, Debt::class],
    version = 5,
    exportSchema = false
)
@TypeConverters
abstract class AppDatabase : RoomDatabase() {
    abstract fun cashierDao(): CashierDao
    abstract fun productDao(): ProductDao
    abstract fun saleDao(): SaleDao
    abstract fun categoryDao(): CategoryDao
    abstract fun customerDao(): CustomerDao
    abstract fun debtDao(): DebtDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pos_warung_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}