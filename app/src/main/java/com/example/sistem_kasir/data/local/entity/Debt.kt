package com.example.sistem_kasir.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "debts")
data class Debt(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerId: Long,      // Relasi ke pelanggan
    val saleId: Long,          // Relasi ke transaksi
    val totalAmount: Long,     // Total hutang
    val remainingAmount: Long, // Sisa hutang (bisa berubah saat dibayar)
    val status: String,        // "PENDING", "PAID", "PARTIAL"
    val dueDate: Long = 0,     // Tanggal jatuh tempo (opsional)
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)