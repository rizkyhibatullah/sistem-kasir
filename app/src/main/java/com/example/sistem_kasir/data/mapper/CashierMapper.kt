package com.example.sistem_kasir.data.mapper

import com.example.sistem_kasir.data.local.entity.Cashier as CashierEntity
import com.example.sistem_kasir.domain.model.Cashier as CashierDomain

fun CashierEntity.toDomain() = CashierDomain(
    id = id,
    name = name,
    pinHash = pinHash
)

fun CashierDomain.toEntity() = CashierEntity(
    id = id,
    name = name,
    pinHash = pinHash
)