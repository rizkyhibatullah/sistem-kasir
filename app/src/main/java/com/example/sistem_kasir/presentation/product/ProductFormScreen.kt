// presentation/screens/product/ProductFormScreen.kt
package com.example.sistem_kasir.presentation.product

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sistem_kasir.domain.model.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormScreen(
    product: Product? = null,
    onBack: () -> Unit,
    onSave: (Product) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProductViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf(product?.name ?: "") }
    var code by remember { mutableStateOf(product?.code ?: "") }
    var price by remember { mutableStateOf((product?.price ?: 0L).toString()) }
    var costPrice by remember { mutableStateOf((product?.costPrice ?: 0L).toString()) }
    var stock by remember { mutableStateOf((product?.stock ?: 0).toString()) }

    val isEditMode = product != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Produk" else "Tambah Produk", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        content = { padding ->
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

                // Error dari ViewModel (opsional)
                val error = viewModel.uiState.value.error
                if (error != null) {
                    Text(
                        text = "Error: $error",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Button(
                    onClick = {
                        val priceLong = price.toLongOrNull() ?: 0L
                        val costPriceLong = costPrice.toLongOrNull() ?: 0L
                        val stockInt = stock.toIntOrNull() ?: 0

                        if (name.isBlank() || priceLong <= 0 || stockInt < 0) {
                            // Handle validasi UI
                            return@Button
                        }

                        val newProduct = Product(
                            id = product?.id ?: 0L,
                            code = code,
                            name = name,
                            price = priceLong,
                            costPrice = costPriceLong,
                            stock = stockInt,
                            categoryId = 1L // default, bisa dikembangkan nanti
                        )
                        onSave(newProduct)
                    },
                    enabled = name.isNotBlank() && price.toLongOrNull() ?: 0 > 0,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isEditMode) "Simpan Perubahan" else "Tambah Produk")
                }
            }
        }
    )
}