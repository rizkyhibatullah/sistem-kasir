package com.example.sistem_kasir.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sales")
data class Sale(
    @PrimaryKey (autoGenerate = true) val id: Long = 0,
    val cashierId: Long,
    val totalAmount: Long,
    val totalProfit: Long,
    val paymentMethod: String, // "CASH", "QRIS", "TRANSFER"
    val timestamp: Long = System.currentTimeMillis()
)
