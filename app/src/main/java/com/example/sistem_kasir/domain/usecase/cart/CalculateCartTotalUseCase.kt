package com.example.sistem_kasir.domain.usecase.cart

import com.example.sistem_kasir.domain.model.CartItem
import javax.inject.Inject
import com.example.sistem_kasir.domain.model.CartSummary

class CalculateCartTotalUseCase @Inject constructor() {
    operator fun invoke(items: List<CartItem>): CartSummary {
        var subtotal = 0L
        var totalProfit = 0L
        var itemCount = 0

        for (item in items) {
            subtotal += item.price * item.quantity
            totalProfit += (item.price - item.costPrice) * item.quantity
            itemCount += item.quantity
        }

        return CartSummary(subtotal, totalProfit, itemCount)
    }
}