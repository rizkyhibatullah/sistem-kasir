package com.example.sistem_kasir.presentation.screens.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sistem_kasir.domain.model.CartItem
import com.example.sistem_kasir.domain.model.Product
import com.example.sistem_kasir.domain.usecase.cart.CalculateCartTotalUseCase
import com.example.sistem_kasir.domain.usecase.product.GetAllProductsUseCase
import com.example.sistem_kasir.presentation.main.MainUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    getAllProductsUseCase: GetAllProductsUseCase,
    private val calculateCartTotalUseCase: CalculateCartTotalUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState

    init {
        getAllProductsUseCase().onEach { products ->
            _uiState.value = _uiState.value.copy(products = products)
        }.launchIn(viewModelScope)
    }

    fun addToCart(product: Product) {
        val existingItem = _uiState.value.cartItems.find { it.productId == product.id }
        val newCartItems = if (existingItem != null) {
            _uiState.value.cartItems.map {
                if (it.productId == product.id) {
                    it.copy(quantity = it.quantity + 1)
                } else {
                    it
                }
            }
        } else {
            _uiState.value.cartItems + CartItem(
                productId = product.id,
                name = product.name,
                price = product.price,
                costPrice = product.costPrice,
                quantity = 1
            )
        }
        _uiState.value = _uiState.value.copy(cartItems = newCartItems)
    }

    fun removeFromCart(productId: Long) {
        val existingItem = _uiState.value.cartItems.find { it.productId == productId } ?: return
        val newCartItems = if (existingItem.quantity > 1) {
            _uiState.value.cartItems.map {
                if (it.productId == productId) {
                    it.copy(quantity = it.quantity - 1)
                } else {
                    it
                }
            }
        } else {
            _uiState.value.cartItems.filter { it.productId != productId }
        }
        _uiState.value = _uiState.value.copy(cartItems = newCartItems)
    }

    fun calculateCartTotal() = calculateCartTotalUseCase(_uiState.value.cartItems)

    fun clearCart() {
        _uiState.value = _uiState.value.copy(cartItems = emptyList())
    }
}