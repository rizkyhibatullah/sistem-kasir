package com.example.sistem_kasir.presentation.screens.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sistem_kasir.domain.model.CartItem
import com.example.sistem_kasir.domain.model.Product
import com.example.sistem_kasir.domain.usecase.cart.CalculateCartTotalUseCase
import com.example.sistem_kasir.domain.usecase.product.GetAllProductsUseCase
import com.example.sistem_kasir.presentation.main.MainUiState
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
    val uiState by viewModel.uiState.collectAsState()
    val cartItems by sharedCartViewModel.cartItems.collectAsState()

    // State untuk bottom sheet
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kasir Warung", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = onNavigateToProducts) {
                        Icon(Icons.Filled.Inventory, contentDescription = "Manajemen Produk")
                    }
                }
            )
        },
        content = { padding ->
            MainContent(
                products = uiState.products,
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
                },
                modifier = modifier.padding(padding)
            )
        },
        bottomBar = {
            BottomSummaryBar(
                cartItemCount = cartItems.size,
                totalAmount = CalculateCartTotalUseCase()(cartItems).subTotal,
                onOpenCart = {
                    scope.launch {
                        bottomSheetState.show()
                    }
                },
                onCheckout = onCheckout
            )
        }
    )

    if (cartItems.isNotEmpty()) {
        ModalBottomSheet(
            onDismissRequest = { scope.launch { bottomSheetState.hide() } },
            sheetState = bottomSheetState,
            dragHandle = { BottomSheetDefaults.DragHandle() }
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
                    TextButton(onClick = sharedCartViewModel::clearCart) {
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
                            onRemove = { sharedCartViewModel.removeFromCart(item.productId) }
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
                        CalculateCartTotalUseCase()(cartItems).subTotal.formatRupiah(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
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
        columns = GridCells.Adaptive(minSize = 160.dp),
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
            .height(140.dp)
            .clickable { onAddToCart(product) },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "Rp${product.price.formatRupiah()}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            if (product.stock <= 5) {
                Text(
                    text = "Stok: ${product.stock}",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.SemiBold
                )
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
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Total: ${totalAmount.formatRupiah()}", fontWeight = FontWeight.Bold)
                if (cartItemCount > 0) {
                    Text("$cartItemCount item", style = MaterialTheme.typography.bodySmall)
                }
            }

            Row() {
                OutlinedButton(
                    onClick = onOpenCart,
                    enabled = cartItemCount > 0
                ) {
                    Text("Keranjang")
                }

                Button(
                    onClick = onCheckout,
                    enabled = cartItemCount > 0
                ) {
                    Text("Bayar", fontWeight = FontWeight.SemiBold)
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
            Text("Rp${item.price.formatRupiah()}", style = MaterialTheme.typography.bodyMedium)
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