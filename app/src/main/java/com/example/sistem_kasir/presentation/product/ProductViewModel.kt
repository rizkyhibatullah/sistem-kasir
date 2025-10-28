// presentation/screens/product/ProductViewModel.kt
package com.example.sistem_kasir.presentation.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sistem_kasir.domain.model.Product
import com.example.sistem_kasir.domain.usecase.product.DeleteProductUseCase
import com.example.sistem_kasir.domain.usecase.product.GetAllProductsUseCase
import com.example.sistem_kasir.domain.usecase.product.InsertProductUseCase
import com.example.sistem_kasir.domain.usecase.product.UpdateProductUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val getAllProductsUseCase: GetAllProductsUseCase,
    private val insertProductUseCase: InsertProductUseCase,
    private val updateProductUseCase: UpdateProductUseCase,
    private val deleteProductUseCase: DeleteProductUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductUiState())
    val uiState: StateFlow<ProductUiState> = _uiState

    private var searchJob: Job? = null

    init {
        loadProducts()
    }

    private fun loadProducts() {
        getAllProductsUseCase().onEach { products ->
            _uiState.value = _uiState.value.copy(
                products = products,
                filteredProducts = products,
                isLoading = false
            )
        }.launchIn(viewModelScope)
    }

    fun search(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300) // Debounce
            val filtered = if (query.isBlank()) {
                _uiState.value.products
            } else {
                _uiState.value.products.filter { product ->
                    product.name.contains(query, ignoreCase = true) ||
                            product.code.contains(query, ignoreCase = true)
                }
            }
            _uiState.value = _uiState.value.copy(filteredProducts = filtered)
        }
    }

    fun insertProduct(product: Product) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isProcessing = true)
            val result = insertProductUseCase(product)
            _uiState.value = _uiState.value.copy(
                isProcessing = false,
                error = if (result.isFailure) result.exceptionOrNull()?.message else null
            )
            if (result.isSuccess) {
                loadProducts() // refresh list
            }
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isProcessing = true)
            val result = updateProductUseCase(product)
            _uiState.value = _uiState.value.copy(
                isProcessing = false,
                error = if (result.isFailure) result.exceptionOrNull()?.message else null
            )
            if (result.isSuccess) {
                loadProducts()
            }
        }
    }

    fun deleteProduct(productId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isProcessing = true)
            val result = deleteProductUseCase(productId)
            _uiState.value = _uiState.value.copy(
                isProcessing = false,
                error = if (result.isFailure) result.exceptionOrNull()?.message else null
            )
            if (result.isSuccess) {
                loadProducts()
            }
        }
    }
}