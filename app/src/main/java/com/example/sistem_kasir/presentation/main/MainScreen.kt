package com.example.sistem_kasir.presentation.main

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sistem_kasir.domain.model.CartItem
import com.example.sistem_kasir.domain.model.Product
import com.example.sistem_kasir.domain.usecase.cart.CalculateCartTotalUseCase
import com.example.sistem_kasir.presentation.main.MainViewModel
import com.example.sistem_kasir.presentation.viewmodel.SharedCartViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onCheckout: () -> Unit,
    onNavigateToProducts: () -> Unit,
    sharedCartViewModel: SharedCartViewModel,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel()
) {
    val filteredProducts by viewModel.filteredProducts.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val cartItems by sharedCartViewModel.cartItems.collectAsState()

    var showCartDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(searchQuery) {
        viewModel.search(searchQuery)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Kasir Warung",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                ),
//                actions = {
//                    IconButton(onClick = onNavigateToProducts) {
//                        Icon(
//                            imageVector = Icons.Filled.Inventory,
//                            contentDescription = "Manajemen Produk",
//                            tint = Color.White
//                        )
//                    }
//                }
            )
        },
        content = { padding ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // 🔍 Kolom Pencarian
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Cari produk...") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    shape = MaterialTheme.shapes.medium
                )

                // Daftar Produk
                if (filteredProducts.isEmpty()) {
                    EmptyProductState(searchQuery.isNotBlank())
                } else {
                    MainContent(
                        products = filteredProducts,
                        onAddToCart = { product ->
                            sharedCartViewModel.addToCart(
                                CartItem(
                                    productId = product.id,
                                    name = product.name,
                                    price = product.price,
                                    costPrice = product.costPrice,
                                    quantity = 1
                                )
                            )
                        }
                    )
                }
            }
        },
        bottomBar = {
            BottomSummaryBar(
                cartItemCount = cartItems.size,
                totalAmount = CalculateCartTotalUseCase()(cartItems).subTotal,
                onOpenCart = { showCartDialog = true },
                onCheckout = onCheckout
            )
        }
    )

    // Dialog Keranjang (tetap sama)
    if (showCartDialog && cartItems.isNotEmpty()) {
        CartDialog(
            cartItems = cartItems,
            onDismiss = { showCartDialog = false },
            onRemoveItem = { sharedCartViewModel.removeFromCart(it.productId) },
            onClearCart = sharedCartViewModel::clearCart,
            totalAmount = CalculateCartTotalUseCase()(cartItems).subTotal
        )
    }
}

@Composable
private fun CartDialog(
    cartItems: List<CartItem>,
    onDismiss: () -> Unit,
    onRemoveItem: (CartItem) -> Unit,
    onClearCart: () -> Unit,
    totalAmount: Long,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Keranjang", style = MaterialTheme.typography.headlineSmall)
                    TextButton(onClick = onClearCart) {
                        Text("Kosongkan", color = MaterialTheme.colorScheme.error)
                    }
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(cartItems) { item ->
                        CartItemRow(
                            item = item,
                            onRemove = { onRemoveItem(item) }
                        )
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(
                        totalAmount.formatRupiah(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Tutup")
                    }
                }
            }
        }
    }
}

@Composable
private fun MainContent(
    products: List<Product>,
    onAddToCart: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(products) { product ->
            ProductCard(product = product, onAddToCart = onAddToCart)
        }
    }
}

@Composable
private fun ProductCard(
    product: Product,
    onAddToCart: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp)
            .clickable { onAddToCart(product) },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp), // ✅ Flat modern
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Nama Produk
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Harga
            Text(
                text = product.price.formatRupiah(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // Stok (jika rendah)
            if (product.stock <= 5) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Stok: ${product.stock}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomSummaryBar(
    cartItemCount: Int,
    totalAmount: Long,
    onOpenCart: () -> Unit,
    onCheckout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Total: ${totalAmount.formatRupiah()}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (cartItemCount > 0) {
                    Text(
                        "$cartItemCount item",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onOpenCart,
                    enabled = cartItemCount > 0,
                    shape = MaterialTheme.shapes.small,
                    border = ButtonDefaults.outlinedButtonBorder,
                    colors = ButtonDefaults.outlinedButtonColors()
                ) {
                    Text("Keranjang", style = MaterialTheme.typography.labelMedium)
                }

                Button(
                    onClick = onCheckout,
                    enabled = cartItemCount > 0,
                    shape = MaterialTheme.shapes.small,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    )
                ) {
                    Text("Bayar", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun CartItemRow(
    item: CartItem,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(item.name, fontWeight = FontWeight.Medium)
            Text(item.price.formatRupiah(), style = MaterialTheme.typography.bodyMedium)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Hapus"
                )
            }
            Text("x${item.quantity}", modifier = Modifier.width(32.dp), textAlign = TextAlign.Center)
        }
    }
}

fun Long.formatRupiah(): String {
    val formatter = java.text.NumberFormat.getNumberInstance(java.util.Locale("in", "ID"))
    return "Rp${formatter.format(this)}"
}

@Composable
private fun EmptyProductState(isSearching: Boolean = false) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (isSearching) Icons.Default.Search else Icons.Default.Inventory,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            if (isSearching) "Produk tidak ditemukan" else "Belum ada produk",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            if (isSearching) "Coba kata kunci lain" else "Tambah produk di menu Manajemen Produk",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}