package com.example.sistem_kasir.data.mapper

import com.example.sistem_kasir.data.local.entity.Debt as DebtEntity
import com.example.sistem_kasir.domain.model.Debt as DebtDomain

fun DebtEntity.toDomain() = DebtDomain(
    id = id,
    customerId = customerId,
    saleId = saleId,
    totalAmount = totalAmount,
    remainingAmount = remainingAmount,
    status = status,
    dueDate = dueDate,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun DebtDomain.toEntity() = DebtEntity(
    id = id,
    customerId = customerId,
    saleId = saleId,
    totalAmount = totalAmount,
    remainingAmount = remainingAmount,
    status = status,
    dueDate = dueDate,
    createdAt = createdAt,
    updatedAt = updatedAt
)