package com.example.sistem_kasir.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cashiers")
data class Cashier(
    @PrimaryKey val id: Long = 0,
    val name: String,
    val pinHash: String // Simpan hash PIN, bukan plain text
)