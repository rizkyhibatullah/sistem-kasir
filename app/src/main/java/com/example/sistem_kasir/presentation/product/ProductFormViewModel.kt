// presentation/screens/product/ProductFormViewModel.kt
package com.example.sistem_kasir.presentation.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sistem_kasir.domain.model.Product
import com.example.sistem_kasir.domain.usecase.product.GetProductByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProductFormUiState(
    val product: Product = Product(),
    val isLoading: Boolean = false
)

@HiltViewModel
class ProductFormViewModel @Inject constructor(
    private val getProductByIdUseCase: GetProductByIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductFormUiState())
    val uiState: StateFlow<ProductFormUiState> = _uiState

    fun loadProduct(productId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val product = getProductByIdUseCase(productId)
            _uiState.value = ProductFormUiState(
                product = product ?: Product(),
                isLoading = false
            )
        }
    }
}