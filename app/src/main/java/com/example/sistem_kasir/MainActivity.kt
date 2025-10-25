package com.example.sistem_kasir

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.sistem_kasir.presentation.checkout.CheckoutScreen
import com.example.sistem_kasir.presentation.main.MainScreen
import com.example.sistem_kasir.presentation.product.ProductFormScreen
import com.example.sistem_kasir.presentation.product.ProductManagementScreen
import com.example.sistem_kasir.presentation.screens.report.ReportScreen
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

                    // Cek & buat data default
                    CheckAndCreateDefaultCashier()

                    // Navigasi utama dengan bottom nav
                    MainNavGraph(
                        navController = navController,
                        sharedCartViewModel = sharedCartViewModel
                    )
                }
            }
        }
    }

    @Composable
    private fun CheckAndCreateDefaultCashier() {
        val lifecycleOwner = LocalLifecycleOwner.current
        val cashierViewModel: CashierViewModel = hiltViewModel()

        LaunchedEffect(Unit) {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                cashierViewModel.createDefaultCashierIfNeeded()
            }
        }
    }
}

@Composable
fun MainNavGraph(
    navController: NavHostController,
    sharedCartViewModel: SharedCartViewModel,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            if (showBottomBar(currentDestination)) {
                SistemKasirBottomNavigation(navController = navController)
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "main",
            modifier = modifier.padding(padding)
        ) {
            composable("main") {
                MainScreen(
                    onCheckout = { navController.navigate("checkout") },
                    onNavigateToProducts = { navController.navigate("product_management") },
                    sharedCartViewModel = sharedCartViewModel
                )
            }

            composable("report") {
                ReportScreen(
                    onBack = { /* tidak perlu back di bottom nav */ }
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
                    onBack = { /* tidak perlu back */ }
                )
            }

            composable("product_form") {
                val product = navController.previousBackStackEntry?.savedStateHandle?.get<com.example.sistem_kasir.domain.model.Product>("product")
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

            // Full-screen: tidak ada bottom bar
            composable("checkout") {
                CheckoutScreen(
                    sharedCartViewModel = sharedCartViewModel,
                    onBack = { navController.popBackStack() },
                    onCheckoutSuccess = {
                        sharedCartViewModel.clearCart()
                        navController.popBackStack("main", inclusive = false)
                    },
                    navController = navController
                )
            }
        }
    }
}

@Composable
private fun SistemKasirBottomNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        bottomNavItems.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = null) },
                label = { Text(screen.label) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        // Pop semua screen di atas route yang dituju
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

// Daftar item bottom nav
private val bottomNavItems = listOf(
    BottomNavItem(
        route = "main",
        label = "Beranda",
        icon = Icons.Filled.Home
    ),
    BottomNavItem(
        route = "report",
        label = "Laporan",
        icon = Icons.Filled.BarChart
    ),
    BottomNavItem(
        route = "product_management",
        label = "Produk",
        icon = Icons.Filled.Inventory
    )
)

private data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

// Sembunyikan bottom bar di screen tertentu
@Composable
private fun showBottomBar(destination: NavDestination?): Boolean {
    return when (destination?.route) {
        "checkout", "product_form" -> false
        else -> true
    }
}