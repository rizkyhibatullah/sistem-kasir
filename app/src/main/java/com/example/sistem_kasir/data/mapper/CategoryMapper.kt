package com.example.sistem_kasir.data.mapper

import com.example.sistem_kasir.data.local.entity.Category as CategoryEntity
import com.example.sistem_kasir.domain.model.Category as CategoryDomain

fun CategoryEntity.toDomain() = CategoryDomain(
    id = id,
    name = name
)

fun CategoryDomain.toEntity() = CategoryEntity(
    id = id,
    name = name
)