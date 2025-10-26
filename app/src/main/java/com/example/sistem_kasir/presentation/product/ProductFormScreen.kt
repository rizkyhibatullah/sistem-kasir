// presentation/screens/product/ProductFormScreen.kt
package com.example.sistem_kasir.presentation.product

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.sistem_kasir.domain.model.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormScreen(
    onBack: () -> Unit,
    onSave: (Product) -> Unit,
    navController: NavController, // 👈 Terima NavController sebagai parameter
    modifier: Modifier = Modifier,
    viewModel: ProductFormViewModel = hiltViewModel()
) {
    // Ambil productId dari savedStateHandle
    val productId = navController.previousBackStackEntry?.savedStateHandle?.get<Long>("product_id")
    val isEditMode = productId != null && productId > 0

    // Muat data jika edit mode
    LaunchedEffect(productId) {
        if (isEditMode) {
            viewModel.loadProduct(productId)
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Gunakan data dari ViewModel, bukan parameter product
    var name by remember { mutableStateOf(uiState.product.name) }
    var code by remember { mutableStateOf(uiState.product.code) }
    var price by remember { mutableStateOf(uiState.product.price.toString()) }
    var costPrice by remember { mutableStateOf(uiState.product.costPrice.toString()) }
    var stock by remember { mutableStateOf(uiState.product.stock.toString()) }

    // Sinkronisasi ulang jika data dari ViewModel berubah (misal: saat loading selesai)
    LaunchedEffect(uiState.product) {
        if (isEditMode) {
            name = uiState.product.name
            code = uiState.product.code
            price = uiState.product.price.toString()
            costPrice = uiState.product.costPrice.toString()
            stock = uiState.product.stock.toString()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditMode) "Edit Produk" else "Tambah Produk",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        content = { padding ->
            if (uiState.isLoading && isEditMode) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nama Produk *") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = code,
                        onValueChange = { code = it },
                        label = { Text("Kode (Opsional)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it.filter { it.isDigit() } },
                        label = { Text("Harga Jual (Rp) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = costPrice,
                        onValueChange = { costPrice = it.filter { it.isDigit() } },
                        label = { Text("Harga Modal (Rp)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = stock,
                        onValueChange = { stock = it.filter { it.isDigit() } },
                        label = { Text("Stok *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            val priceLong = price.toLongOrNull() ?: 0L
                            val costPriceLong = costPrice.toLongOrNull() ?: 0L
                            val stockInt = stock.toIntOrNull() ?: 0

                            if (name.isBlank() || priceLong <= 0 || stockInt < 0) {
                                return@Button
                            }

                            val newProduct = Product(
                                id = if (isEditMode) uiState.product.id else 0L,
                                code = code,
                                name = name,
                                price = priceLong,
                                costPrice = costPriceLong,
                                stock = stockInt,
                                categoryId = uiState.product.categoryId // Gunakan dari ViewModel
                            )
                            onSave(newProduct)
                        },
                        enabled = name.isNotBlank() && (price.toLongOrNull() ?: 0) > 0,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (isEditMode) "Simpan Perubahan" else "Tambah Produk")
                    }
                }
            }
        }
    )
}