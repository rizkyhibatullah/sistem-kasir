// presentation/screens/checkout/ReceiptPreview.kt
package com.example.sistem_kasir.presentation.checkout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sistem_kasir.domain.model.CartItem
import com.example.sistem_kasir.presentation.main.formatRupiah
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReceiptPreview(
    storeName: String = "Warung Sembako Maju",
    cartItems: List<CartItem>,
    totalAmount: Long,
    paymentMethod: String,
    cashGiven: Long = 0L,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale("in", "ID"))
    val currentDate = dateFormat.format(Date())

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        item {
            Text(
                text = storeName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Jl. Raya Contoh No. 123",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = currentDate,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Daftar Item
        items(cartItems) { item ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = item.name,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "x${item.quantity}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = (item.price * item.quantity).formatRupiah(),
                    fontSize = 14.sp,
                    textAlign = TextAlign.End,
                    modifier = Modifier.width(100.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        // Garis pemisah
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Total
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(totalAmount.formatRupiah(), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Kembalian (jika tunai)
        if (paymentMethod == "CASH" && cashGiven > totalAmount) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Tunai", fontSize = 14.sp)
                    Text(cashGiven.formatRupiah(), fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Kembalian", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                    Text((cashGiven - totalAmount).formatRupiah(), fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        // Metode Pembayaran
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Metode: $paymentMethod",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Terima kasih!\nSelamat berbelanja kembali 😊",
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}