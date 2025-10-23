// presentation/screens/checkout/CheckoutViewModel.kt
package com.example.sistem_kasir.presentation.checkout

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sistem_kasir.domain.model.CartItem
import com.example.sistem_kasir.domain.usecase.cart.ValidateCartItemsUseCase
import com.example.sistem_kasir.domain.usecase.sale.CreateSaleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val validateCartItemsUseCase: ValidateCartItemsUseCase,
    private val createSaleUseCase: CreateSaleUseCase
) : ViewModel() {

    var uiState by mutableStateOf(CheckoutUiState())
        private set

    fun setCartItems(items: List<CartItem>) {
        uiState = uiState.copy(cartItems = items)
    }

    fun onPaymentMethodChange(method: String) {
        uiState = uiState.copy(paymentMethod = method)
    }

    fun onCashGivenChanged(amount: Long) {
        uiState = uiState.copy(cashGiven = amount)
    }

    fun processCheckout(cashierId: Long = 1L) {
        if (uiState.isProcessing) return

        val items = uiState.cartItems
        if (items.isEmpty()) return

        viewModelScope.launch {
            uiState = uiState.copy(isProcessing = true, error = null)

            // 1. Validasi stok
            val validation = validateCartItemsUseCase(items)
            if (validation.isFailure) {
                uiState = uiState.copy(isProcessing = false, error = validation.exceptionOrNull()?.message ?: "Gagal validasi")
                return@launch
            }

            // 2. Simpan transaksi
            val result = createSaleUseCase(
                cashierId = cashierId,
                items = items,
                paymentMethod = uiState.paymentMethod
            )

            if (result.isSuccess) {
                uiState = uiState.copy(isProcessing = false, success = true)
            } else {
                uiState = uiState.copy(
                    isProcessing = false,
                    error = result.exceptionOrNull()?.message ?: "Gagal menyimpan transaksi"
                )
            }
        }
    }

    fun reset() {
        uiState = CheckoutUiState()
    }
}