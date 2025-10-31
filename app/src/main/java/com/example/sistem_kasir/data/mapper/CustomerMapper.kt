package com.example.sistem_kasir.data.mapper

import com.example.sistem_kasir.data.local.entity.Customer as CustomerEntity
import com.example.sistem_kasir.domain.model.Customer as CustomerDomain

fun CustomerEntity.toDomain() = CustomerDomain(
    id = id,
    name = name,
    phone = phone,
    address = address,
    createdAt = createdAt
)

fun CustomerDomain.toEntity() = CustomerEntity(
    id = id,
    name = name,
    phone = phone,
    address = address,
    createdAt = createdAt
)