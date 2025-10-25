// presentation/screens/checkout/CheckoutScreen.kt
package com.example.sistem_kasir.presentation.checkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.sistem_kasir.domain.model.CartItem
import com.example.sistem_kasir.presentation.main.formatRupiah
import com.example.sistem_kasir.presentation.viewmodel.SharedCartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    sharedCartViewModel: SharedCartViewModel,
    onBack: () -> Unit,
    onCheckoutSuccess: () -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val cartItems by sharedCartViewModel.cartItems.collectAsState()
    val total = cartItems.sumOf { it.price * it.quantity }

    LaunchedEffect(cartItems) {
        if (cartItems.isNotEmpty()) {
            viewModel.setCartItems(cartItems)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("main") }) {
                        Icon(Icons.Filled.Add, contentDescription = "Tambah Produk")
                    }
                }
            )
        },
        content = { padding ->
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                // Ringkasan item
                item {
                    Text("Ringkasan Belanja", style = MaterialTheme.typography.headlineSmall)
                }
                items(cartItems) { item ->
                    CartItemRow(
                        item = item,
                        onRemove = {
                            sharedCartViewModel.removeFromCart(item.productId) // ✅ pakai 'item' dari luar
                        }
                    )
                }

                // Total
                item {
                    Divider()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Total",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            total.formatRupiah(),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }

                // Metode pembayaran
                item {
                    Text(
                        "Metode Pembayaran",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    Row {
                        PaymentMethodButton(
                            text = "Tunai",
                            selected = viewModel.uiState.paymentMethod == "CASH",
                            onClick = { viewModel.onPaymentMethodChange("CASH") }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        PaymentMethodButton(
                            text = "QRIS",
                            selected = viewModel.uiState.paymentMethod == "QRIS",
                            onClick = { viewModel.onPaymentMethodChange("QRIS") }
                        )
                    }
                }

                // Input uang tunai
                if (viewModel.uiState.paymentMethod == "CASH") {
                    item {
                        OutlinedTextField(
                            value = if (viewModel.uiState.cashGiven > 0) viewModel.uiState.cashGiven.toString() else "",
                            onValueChange = { text ->
                                val amount = text.toLongOrNull() ?: 0L
                                viewModel.onCashGivenChanged(amount)
                            },
                            label = { Text("Jumlah Uang Tunai") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (viewModel.uiState.cashGiven >= total && viewModel.uiState.cashGiven > 0) {
                            val change = viewModel.uiState.cashGiven - total
                            Text(
                                text = "Kembalian: ${change.formatRupiah()}",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        } else if (viewModel.uiState.cashGiven > 0) {
                            Text(
                                text = "Uang tidak cukup!",
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }

                // Error
                if (viewModel.uiState.error != null) {
                    item {
                        Text(
                            text = "Error: ${viewModel.uiState.error}",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                // Tombol Bayar
                item {
                    Button(
                        onClick = { viewModel.processCheckout() },
                        enabled = !viewModel.uiState.isProcessing && (
                                viewModel.uiState.paymentMethod == "QRIS" ||
                                        (viewModel.uiState.paymentMethod == "CASH" && viewModel.uiState.cashGiven >= total)
                                ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (viewModel.uiState.isProcessing) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text("Selesai Transaksi", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    )
}

    // Bottom Sheet: Tambah Produk (opsional)

    @Composable
    private fun CartItemRow(item: CartItem, onRemove: () -> Unit, ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(item.name, fontWeight = FontWeight.Medium)
                Text("x${item.quantity}", style = MaterialTheme.typography.bodySmall)
            }
            Text((item.price * item.quantity).formatRupiah())
        }
    }

    @Composable
    private fun PaymentMethodButton(
        text: String,
        selected: Boolean,
        onClick: () -> Unit
    ) {
        OutlinedButton(
            onClick = onClick,
            colors = if (selected) {
                ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            } else {
                ButtonDefaults.outlinedButtonColors()
            },
        ) {
            Text(text)
        }
    }