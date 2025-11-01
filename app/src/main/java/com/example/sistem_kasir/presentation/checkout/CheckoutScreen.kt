// presentation/screens/checkout/CheckoutScreen.kt
package com.example.sistem_kasir.presentation.checkout

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.sistem_kasir.domain.model.CartItem
import com.example.sistem_kasir.domain.model.Customer
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
    val uiState = viewModel.uiState

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
                            Spacer(modifier = Modifier.width(8.dp))
                            PaymentMethodButton(
                                text = "Hutang",
                                selected = uiState.paymentMethod == "DEBT",
                                onClick = { viewModel.onPaymentMethodChange("DEBT") }
                            )
                        }
                    }

                    if (uiState.paymentMethod == "DEBT") {
                        item {
                            CustomerSection(
                                customers = uiState.customers,
                                selectedCustomer = uiState.selectedCustomer,
                                isAddingNewCustomer = uiState.isAddingNewCustomer,
                                newCustomerName = uiState.newCustomerName,
                                newCustomerPhone = uiState.newCustomerPhone,
                                onSelectCustomer = viewModel::selectCustomer,
                                onToggleAddNew = viewModel::toggleAddNewCustomer,
                                onNewNameChange = viewModel::onNewCustomerNameChanged,
                                onNewPhoneChange = viewModel::onNewCustomerPhoneChanged,
                                onAddNewCustomer = viewModel::addNewCustomer
                            )
                        }
                    }

                    // Input uang tunai
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
                            enabled = isCheckoutEnabled(uiState, total),
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
        }
    )
}

@Composable
private fun isCheckoutEnabled(uiState: CheckoutUiState, total: Long): Boolean {
    return when (uiState.paymentMethod) {
        "CASH" -> !uiState.isProcessing && uiState.cashGiven >= total
        "QRIS" -> !uiState.isProcessing
        "DEBT" -> !uiState.isProcessing && uiState.selectedCustomer != null
        else -> false
    }
}

@Composable
private fun CustomerSection(
    customers: List<Customer>,
    selectedCustomer: Customer?,
    isAddingNewCustomer: Boolean,
    newCustomerName: String,
    newCustomerPhone: String,
    onSelectCustomer: (Customer) -> Unit,
    onToggleAddNew: () -> Unit,
    onNewNameChange: (String) -> Unit,
    onNewPhoneChange: (String) -> Unit,
    onAddNewCustomer: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text("Pelanggan", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))

        // Daftar pelanggan
        if (!isAddingNewCustomer) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(120.dp)
            ) {
                items(customers) { customer ->
                    CustomerItem(
                        customer = customer,
                        isSelected = selectedCustomer?.id == customer.id,
                        onClick = { onSelectCustomer(customer) }
                    )
                }
            }

            TextButton(onClick = onToggleAddNew) {
                Text("➕ Tambah Pelanggan Baru")
            }
        } else {
            // Form tambah pelanggan baru
            OutlinedTextField(
                value = newCustomerName,
                onValueChange = onNewNameChange,
                label = { Text("Nama Pelanggan *") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = newCustomerPhone,
                onValueChange = onNewPhoneChange,
                label = { Text("No. HP (Opsional)") },
                modifier = Modifier.fillMaxWidth()
            )
            Row {
                Button(onClick = onAddNewCustomer, enabled = newCustomerName.isNotBlank()) {
                    Text("Simpan")
                }
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(onClick = onToggleAddNew) {
                    Text("Batal")
                }
            }
        }
    }
}

@Composable
private fun CustomerItem(customer: Customer, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = if (isSelected) {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = null // Handled by parent
            )
            Column {
                Text(customer.name, fontWeight = FontWeight.Medium)
                if (customer.phone.isNotBlank()) {
                    Text(customer.phone, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
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