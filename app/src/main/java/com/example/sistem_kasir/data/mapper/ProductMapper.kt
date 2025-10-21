package com.example.sistem_kasir.data.mapper

import com.example.sistem_kasir.data.local.entity.Product as ProductEntity
import com.example.sistem_kasir.domain.model.Product as ProductDomain

fun ProductEntity.toDomain(categoryName: String = "") = ProductDomain(
    id = id,
    code = code,
    name = name,
    price = price,
    costPrice = costPrice,
    stock = stock,
    categoryId = categoryId,
    categoryName = categoryName,
    imageUrl = imageUrl
)

fun ProductDomain.toEntity() = ProductEntity(
    id = id,
    code = code,
    name = name,
    price = price,
    costPrice = costPrice,
    stock = stock,
    categoryId = categoryId,
    imageUrl = imageUrl
)