// presentation/screens/checkout/CheckoutScreen.kt
package com.example.sistem_kasir.presentation.checkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.sistem_kasir.domain.model.CartItem
import com.example.sistem_kasir.presentation.main.formatRupiah
import com.example.sistem_kasir.presentation.viewmodel.SharedCartViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

    var showReceipt by remember { mutableStateOf(false) }

    LaunchedEffect(cartItems) {
        if (cartItems.isNotEmpty()) {
            viewModel.setCartItems(cartItems)
        }
    }

    LaunchedEffect(viewModel.uiState.success) {
        if (viewModel.uiState.success) {
            showReceipt = true // ✅ Tampilkan struk setelah sukses
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (showReceipt) "Struk" else "Checkout", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    if (!showReceipt) {
                        IconButton(onClick = { navController.navigate("main") }) {
                            Icon(Icons.Filled.Add, contentDescription = "Tambah Produk")
                        }
                    }
                }
            )
        },
        content = { padding ->
            if (showReceipt) {
                // ✅ TAMPILKAN STRUK SETELAH SUKSES
                ReceiptView(
                    cartItems = cartItems,
                    totalAmount = total,
                    paymentMethod = viewModel.uiState.paymentMethod,
                    cashGiven = viewModel.uiState.cashGiven,
                    onPrint = { /* Nanti diisi integrasi printer */ },
                    onDone = onCheckoutSuccess,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                )
            } else {
                // ✅ FORM CHECKOUT BIASA
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
                                sharedCartViewModel.removeFromCart(item.productId)
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
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
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
        }
    )
}

// ✅ STRUK SETELAH TRANSAKSI
@Composable
private fun ReceiptView(
    cartItems: List<CartItem>,
    totalAmount: Long,
    paymentMethod: String,
    cashGiven: Long,
    onPrint: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale("in", "ID"))
    val currentDate = dateFormat.format(Date())

    Column(
        modifier = modifier
    ) {
        // Struk Content
        LazyColumn(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp)
        ) {
            item {
                Text("Warung Sembako Maju", fontWeight = FontWeight.Bold, fontSize = MaterialTheme.typography.titleLarge.fontSize)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Jl. Raya Contoh No. 123", fontSize = MaterialTheme.typography.bodyMedium.fontSize)
                Spacer(modifier = Modifier.height(8.dp))
                Text(currentDate, fontSize = MaterialTheme.typography.bodySmall.fontSize)
                Spacer(modifier = Modifier.height(16.dp))
                androidx.compose.material3.Divider()
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(cartItems) { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(item.name, fontSize = 14.sp)
                        Text("x${item.quantity}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text((item.price * item.quantity).formatRupiah(), fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                androidx.compose.material3.Divider()
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total", fontWeight = FontWeight.Bold)
                    Text(totalAmount.formatRupiah(), fontWeight = FontWeight.Bold)
                }

                if (paymentMethod == "CASH" && cashGiven > totalAmount) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Tunai")
                        Text(cashGiven.formatRupiah())
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Kembalian", color = MaterialTheme.colorScheme.primary)
                        Text((cashGiven - totalAmount).formatRupiah(), color = MaterialTheme.colorScheme.primary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                androidx.compose.material3.Divider()
                Spacer(modifier = Modifier.height(8.dp))
                Text("Metode: $paymentMethod\n\nTerima kasih!\nSelamat berbelanja kembali 😊", textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
        }

        // Tombol Aksi
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onPrint,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cetak")
            }

            Button(
                onClick = onDone,
                modifier = Modifier.weight(1f)
            ) {
                Text("Selesai")
            }
        }
    }
}

@Composable
private fun CartItemRow(item: CartItem, onRemove: () -> Unit) {
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