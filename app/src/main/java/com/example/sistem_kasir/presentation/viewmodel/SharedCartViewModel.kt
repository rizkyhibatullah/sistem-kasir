
package com.example.sistem_kasir.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.sistem_kasir.domain.model.CartItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SharedCartViewModel @Inject constructor() : ViewModel() {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    fun updateCart(items: List<CartItem>) {
        _cartItems.value = items
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    fun addToCart(item: CartItem) {
        val existing = _cartItems.value.find { it.productId == item.productId }
        val newCart = if (existing != null) {
            _cartItems.value.map {
                if (it.productId == item.productId) {
                    it.copy(quantity = it.quantity + item.quantity)
                } else {
                    it
                }
            }
        } else {
            _cartItems.value + item
        }
        _cartItems.value = newCart
    }

    fun removeFromCart(productId: Long) {
        val existing = _cartItems.value.find { it.productId == productId } ?: return
        val newCart = if (existing.quantity > 1) {
            _cartItems.value.map {
                if (it.productId == productId) {
                    it.copy(quantity = it.quantity - 1)
                } else {
                    it
                }
            }
        } else {
            _cartItems.value.filter { it.productId != productId }
        }
        _cartItems.value = newCart
    }
}