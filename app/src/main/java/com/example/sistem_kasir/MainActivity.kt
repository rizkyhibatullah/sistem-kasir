package com.example.sistem_kasir

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sistem_kasir.presentation.checkout.CheckoutScreen
import com.example.sistem_kasir.presentation.main.MainScreen
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

                    NavHost(navController, startDestination = "main") {
                        composable("main") {
                            MainScreen(
                                onCheckout = { navController.navigate("checkout") },
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
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Sistem_kasirTheme {
        Greeting("Android")
    }
}