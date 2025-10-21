package com.example.sistem_kasir.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "products",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("categoryId")]
)
data class Product(
    @PrimaryKey val id: Long = 0,
    val code: String = "",
    val name: String,
    val price: Long,          // dalam satuan rupiah (misal: 5000)
    val costPrice: Long = 0,  // harga modal (untuk hitung profit)
    var stock: Int = 0,
    val categoryId: Long,
    val imageUrl: String? = null
)
