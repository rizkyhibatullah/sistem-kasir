// presentation/screens/checkout/CheckoutScreen.kt
package com.example.sistem_kasir.presentation.checkout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.sistem_kasir.domain.model.CartItem
import com.example.sistem_kasir.presentation.main.formatRupiah
import com.example.sistem_kasir.presentation.viewmodel.SharedCartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    sharedCartViewModel: SharedCartViewModel,
    onBack: () -> Unit,
    onCheckoutSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val cartItems by sharedCartViewModel.cartItems.collectAsState()

    LaunchedEffect(cartItems) {
        if (cartItems.isNotEmpty()) {
            viewModel.setCartItems(cartItems)
        }
    }

    // Handle sukses
    LaunchedEffect(viewModel.uiState.success) {
        if (viewModel.uiState.success) {
            onCheckoutSuccess()
        }
    }

    val uiState = viewModel.uiState
    val total = cartItems.sumOf { it.price * it.quantity }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
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
                    CartItemRow(item = item)
                }

                // Total
                item {
                    Divider()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                        Text(total.formatRupiah(), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                    }
                }

                // Metode pembayaran
                item {
                    Text("Metode Pembayaran", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))
                    Row {
                        PaymentMethodButton(
                            text = "Tunai",
                            selected = uiState.paymentMethod == "CASH",
                            onClick = { viewModel.onPaymentMethodChange("CASH") }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        PaymentMethodButton(
                            text = "QRIS",
                            selected = uiState.paymentMethod == "QRIS",
                            onClick = { viewModel.onPaymentMethodChange("QRIS") }
                        )
                    }
                }

                // Input uang tunai (hanya jika tunai)
                if (uiState.paymentMethod == "CASH") {
                    item {
                        OutlinedTextField(
                            value = if (uiState.cashGiven > 0) uiState.cashGiven.toString() else "",
                            onValueChange = { text ->
                                val amount = text.toLongOrNull() ?: 0L
                                viewModel.onCashGivenChanged(amount)
                            },
                            label = { Text("Jumlah Uang Tunai") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Tampilkan kembalian
                        if (uiState.cashGiven >= total && uiState.cashGiven > 0) {
                            val change = uiState.cashGiven - total
                            Text(
                                text = "Kembalian: ${change.formatRupiah()}",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        } else if (uiState.cashGiven > 0) {
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
                if (uiState.error != null) {
                    item {
                        Text(
                            text = "Error: ${uiState.error}",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                // Tombol Bayar
                item {
                    Button(
                        onClick = { viewModel.processCheckout() },
                        enabled = !uiState.isProcessing && (
                                uiState.paymentMethod == "QRIS" ||
                                        (uiState.paymentMethod == "CASH" && uiState.cashGiven >= total)
                                ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (uiState.isProcessing) {
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

@Composable
private fun CartItemRow(item: CartItem) {
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