// presentation/screens/checkout/CheckoutViewModel.kt
package com.example.sistem_kasir.presentation.checkout

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sistem_kasir.domain.model.CartItem
import com.example.sistem_kasir.domain.model.Customer
import com.example.sistem_kasir.domain.usecase.cart.ValidateCartItemsUseCase
import com.example.sistem_kasir.domain.usecase.customer.GetAllCustomersUseCase
import com.example.sistem_kasir.domain.usecase.customer.InsertCustomerUseCase
import com.example.sistem_kasir.domain.usecase.debt.CreateDebtUseCase
import com.example.sistem_kasir.domain.usecase.sale.CreateSaleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val validateCartItemsUseCase: ValidateCartItemsUseCase,
    private val createSaleUseCase: CreateSaleUseCase,
    private val createDebtUseCase: CreateDebtUseCase,
    private val getAllCustomersUseCase: GetAllCustomersUseCase,
    private val insertCustomerUseCase: InsertCustomerUseCase
) : ViewModel() {

    var uiState by mutableStateOf(CheckoutUiState())
        private set

    init {
        loadCustomers()
    }

    private fun loadCustomers() {
        viewModelScope.launch {
            getAllCustomersUseCase().collect { customers ->
                uiState = uiState.copy(customers = customers)
            }
        }
    }

    fun setCartItems(items: List<CartItem>) {
        uiState = uiState.copy(cartItems = items)
    }

    fun onPaymentMethodChange(method: String) {
        uiState = uiState.copy(
            paymentMethod = method,
            selectedCustomer = if (method != "DEBT") null else uiState.selectedCustomer
        )
    }

    fun onCashGivenChanged(amount: Long) {
        uiState = uiState.copy(cashGiven = amount)
    }

    fun selectCustomer(customer: Customer) {
        uiState = uiState.copy(selectedCustomer = customer, isAddingNewCustomer = false)
    }

    fun toggleAddNewCustomer() {
        uiState = uiState.copy(
            isAddingNewCustomer = !uiState.isAddingNewCustomer,
            newCustomerName = "",
            newCustomerPhone = "",
            selectedCustomer = null
        )
    }

    fun onNewCustomerNameChanged(name: String) {
        uiState = uiState.copy(newCustomerName = name)
    }

    fun onNewCustomerPhoneChanged(phone: String) {
        uiState = uiState.copy(newCustomerPhone = phone)
    }

    fun addNewCustomer() {
        if (uiState.newCustomerName.isBlank()) return

        viewModelScope.launch {
            val result = insertCustomerUseCase(
                Customer(
                    name = uiState.newCustomerName,
                    phone = uiState.newCustomerPhone
                )
            )
            if (result.isSuccess) {
                loadCustomers() // Refresh daftar
                val newCustomer = uiState.customers.find { it.name == uiState.newCustomerName }
                if (newCustomer != null) {
                    uiState = uiState.copy(
                        selectedCustomer = newCustomer,
                        isAddingNewCustomer = false,
                        newCustomerName = "",
                        newCustomerPhone = ""
                    )
                }
            } else {
                uiState = uiState.copy(error = "Gagal menambah pelanggan")
            }
        }
    }

    fun processCheckout(cashierId: Long = 1L) {
        if (uiState.isProcessing) return

        val items = uiState.cartItems
        if (items.isEmpty()) return

        // Validasi untuk hutang
        if (uiState.paymentMethod == "DEBT" && uiState.selectedCustomer == null) {
            uiState = uiState.copy(error = "Pilih pelanggan untuk transaksi hutang")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isProcessing = true, error = null)

            // Validasi stok
            val validation = validateCartItemsUseCase(items)
            if (validation.isFailure) {
                uiState = uiState.copy(isProcessing = false, error = validation.exceptionOrNull()?.message ?: "Gagal validasi")
                return@launch
            }

            // Simpan transaksi
            val saleResult = createSaleUseCase(
                cashierId = cashierId,
                customerId = uiState.selectedCustomer?.id,
                items = items,
                paymentMethod = uiState.paymentMethod,
                isDebt = uiState.paymentMethod == "DEBT"
            )

            if (saleResult.isFailure) {
                uiState = uiState.copy(
                    isProcessing = false,
                    error = saleResult.exceptionOrNull()?.message ?: "Gagal menyimpan transaksi"
                )
                return@launch
            }

            val saleId = saleResult.getOrNull()!!

            // Jika transaksi hutang, simpan data hutang
            if (uiState.paymentMethod == "DEBT") {
                val debtResult = createDebtUseCase(
                    com.example.sistem_kasir.domain.model.Debt(
                        customerId = uiState.selectedCustomer!!.id,
                        saleId = saleId,
                        totalAmount = items.sumOf { it.price * it.quantity },
                        remainingAmount = items.sumOf { it.price * it.quantity },
                        status = "PENDING"
                    )
                )
                if (debtResult.isFailure) {
                    uiState = uiState.copy(
                        isProcessing = false,
                        error = "Transaksi berhasil, tapi gagal simpan hutang: ${debtResult.exceptionOrNull()?.message}"
                    )
                    return@launch
                }
            }

            uiState = uiState.copy(isProcessing = false, success = true)
        }
    }

    fun reset() {
        uiState = CheckoutUiState()
    }
}