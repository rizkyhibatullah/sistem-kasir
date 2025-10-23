package com.example.sistem_kasir

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sistem_kasir.presentation.checkout.CheckoutScreen
import com.example.sistem_kasir.presentation.main.MainScreen
import com.example.sistem_kasir.presentation.product.ProductFormScreen
import com.example.sistem_kasir.presentation.product.ProductManagementScreen
import com.example.sistem_kasir.presentation.viewmodel.CashierViewModel
import com.example.sistem_kasir.presentation.viewmodel.SharedCartViewModel
import com.example.sistem_kasir.ui.theme.Sistem_kasirTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Sistem_kasirTheme {
                Surface(Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    val sharedCartViewModel: SharedCartViewModel = hiltViewModel()

                    // ✅ Cek & buat kasir default saat pertama kali
                    CheckAndCreateDefaultCashier()

                    NavHost(navController = navController, startDestination = "main") {
                        composable("main") {
                            MainScreen(
                                onCheckout = { navController.navigate("checkout") },
                                onNavigateToProducts = { navController.navigate("product_management") },
                                sharedCartViewModel = sharedCartViewModel
                            )
                        }

                        composable("checkout") {
                            CheckoutScreen(
                                sharedCartViewModel = sharedCartViewModel,
                                onBack = { navController.popBackStack() },
                                onCheckoutSuccess = {
                                    sharedCartViewModel.clearCart()
                                    navController.popBackStack("main", inclusive = false)
                                }
                            )
                        }

                        composable("product_management") {
                            ProductManagementScreen(
                                onNavigateToForm = { product ->
                                    if (product == null) {
                                        navController.navigate("product_form")
                                    } else {
                                        navController.currentBackStackEntry?.savedStateHandle?.set("product", product)
                                        navController.navigate("product_form")
                                    }
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("product_form") {
                            val product = navController.previousBackStackEntry?.savedStateHandle?.get<com.example.sistem_kasir.domain.model.Product>("product")

                            // ✅ hiltViewModel() dipanggil di dalam composable → aman
                            val formViewModel: com.example.sistem_kasir.presentation.product.ProductViewModel = hiltViewModel()

                            ProductFormScreen(
                                product = product,
                                onBack = { navController.popBackStack() },
                                onSave = { newProduct ->
                                    if (newProduct.id == 0L) {
                                        formViewModel.insertProduct(newProduct)
                                    } else {
                                        formViewModel.updateProduct(newProduct)
                                    }
                                    navController.popBackStack("product_management", inclusive = false)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun CheckAndCreateDefaultCashier() {
        val lifecycleOwner = LocalLifecycleOwner.current // ✅ Ambil LifecycleOwner dari konteks Compose
        val cashierViewModel: CashierViewModel = hiltViewModel()

        LaunchedEffect(Unit) {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                cashierViewModel.createDefaultCashierIfNeeded()
            }
        }
    }
}