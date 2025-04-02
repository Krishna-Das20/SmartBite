package com.example.myapp.u.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter

// Color Scheme
val PrimaryGreen = Color(0xFF4CAF50)

val LightGray = Color(0xFFF5F5F5)

data class CartItem(
    val id: String,
    val name: String,
    val price: Double,
    val imageUrl: String,
    var quantity: Int
)

@Composable
fun CartScreen(navController: NavController) {
    val cartItems = remember { mutableStateListOf(
        CartItem(
            id = "1",
            name = "Organic Apples",
            price = 3.99,
            imageUrl = "https://images.unsplash.com/photo-1568702846914-96b305d2aaeb?w=500",
            quantity = 2
        ),
        CartItem(
            id = "2",
            name = "Almond Milk",
            price = 4.49,
            imageUrl = "https://images.unsplash.com/photo-1550583724-b2692b85b150?w=500",
            quantity = 1
        )
    )}

    val suggestedItems = remember { listOf(
        CartItem(
            id = "3",
            name = "Organic Tomatoes",
            price = 3.99,
            imageUrl = "https://images.unsplash.com/photo-1594282418426-62d45d4a3793?w=500",
            quantity = 1
        ),
        CartItem(
            id = "4",
            name = "Fresh Avocados",
            price = 2.49,
            imageUrl = "https://images.unsplash.com/photo-1601493700631-2b16ec4b4716?w=500",
            quantity = 1
        )
    )}

    val totalPrice by remember(cartItems) {
        derivedStateOf { cartItems.sumOf { it.price * it.quantity } }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGreen)
            .padding(horizontal = 16.dp)
    ) {
        // Header
        Text(
            text = "My Cart",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                color = DarkGreen
            ),
            modifier = Modifier
                .padding(top = 24.dp, bottom = 16.dp)
                .fillMaxWidth()
        )

        // Cart Items
        if (cartItems.isEmpty()) {
            EmptyCartPlaceholder()
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cartItems, key = { it.id }) { item ->
                    CartItemCard(
                        item = item,
                        onDelete = { cartItems.remove(item) },
                        onQuantityChange = { newQuantity ->
                            val index = cartItems.indexOfFirst { it.id == item.id }
                            if (index != -1) {
                                if (newQuantity > 0) {
                                    cartItems[index] = cartItems[index].copy(quantity = newQuantity)
                                } else {
                                    cartItems.removeAt(index)
                                }
                            }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Suggested Items
        Text(
            text = "You Might Also Like",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = DarkGreen
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.height(200.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(suggestedItems.filter { suggested ->
                cartItems.none { it.id == suggested.id }
            }) { item ->
                SuggestedItemCard(
                    item = item,
                    onAdd = {
                        val existingItem = cartItems.find { it.id == item.id }
                        if (existingItem != null) {
                            cartItems.replaceAll {
                                if (it.id == item.id) it.copy(quantity = it.quantity + 1) else it
                            }
                        } else {
                            cartItems.add(item.copy(quantity = 1))
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Order Summary
        OrderSummarySection(
            totalPrice = totalPrice,
            onCheckout = { navController.navigate("checkout") },
            modifier = Modifier.padding(bottom = 24.dp)
        )
    }
}

@Composable
fun CartItemCard(
    item: CartItem,
    onDelete: () -> Unit,
    onQuantityChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Image from Web
            Image(
                painter = rememberImagePainter(
                    data = item.imageUrl,
                    builder = {
                        crossfade(true)
                    }
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = DarkGreen
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$${"%.2f".format(item.price)}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = DarkGreen.copy(alpha = 0.7f)
                    )
                )
            }

            // Quantity Controls
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "$${"%.2f".format(item.price * item.quantity)}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = PrimaryGreen,
                        fontWeight = FontWeight.Bold
                    )
                )

                QuantityControls(
                    quantity = item.quantity,
                    onIncrement = { onQuantityChange(item.quantity + 1) },
                    onDecrement = { onQuantityChange(item.quantity - 1) }
                )

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove",
                        tint = PrimaryGreen
                    )
                }
            }
        }
    }
}

@Composable
fun QuantityControls(
    quantity: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(
            onClick = onDecrement,
            modifier = Modifier.size(32.dp),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = LightGreen,
                contentColor = DarkGreen
            )
        ) {
            Text("-", style = MaterialTheme.typography.bodyLarge)
        }

        Text(
            text = quantity.toString(),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium,
                color = DarkGreen
            )
        )

        IconButton(
            onClick = onIncrement,
            modifier = Modifier.size(32.dp),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = LightGreen,
                contentColor = DarkGreen
            )
        ) {
            Text("+", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun SuggestedItemCard(
    item: CartItem,
    onAdd: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberImagePainter(item.imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = DarkGreen
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "$${"%.2f".format(item.price)}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = DarkGreen.copy(alpha = 0.7f)
                    )
                )
            }

            IconButton(
                onClick = onAdd,
                modifier = Modifier.size(40.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = PrimaryGreen,
                    contentColor = White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add to cart",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun OrderSummarySection(
    totalPrice: Double,
    onCheckout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shippingCost = if (totalPrice > 30) 0.0 else 4.99
    val tax = totalPrice * 0.05
    val grandTotal = totalPrice + shippingCost + tax

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(White)
            .padding(16.dp)
    ) {
        Text(
            text = "Order Summary",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = DarkGreen
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        OrderSummaryRow("Subtotal", totalPrice)
        OrderSummaryRow("Shipping", shippingCost)
        OrderSummaryRow("Tax", tax)

        Spacer(modifier = Modifier.height(8.dp))
        Divider(color = LightGreen.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.height(8.dp))

        OrderSummaryRow("Total", grandTotal, isTotal = true)

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onCheckout,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryGreen,
                contentColor = White
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Proceed to Checkout",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            )
        }
    }
}

@Composable
fun OrderSummaryRow(
    label: String,
    amount: Double,
    isTotal: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = if (isTotal) {
                MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreen
                )
            } else {
                MaterialTheme.typography.bodyLarge.copy(
                    color = DarkGreen
                )
            }
        )
        Text(
            text = "$${"%.2f".format(amount)}",
            style = if (isTotal) {
                MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreen
                )
            } else {
                MaterialTheme.typography.bodyLarge.copy(
                    color = DarkGreen
                )
            }
        )
    }
}

@Composable
fun EmptyCartPlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Empty cart",
            modifier = Modifier.size(48.dp),
            tint = PrimaryGreen
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Your cart is empty",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = DarkGreen
            )
        )
        Text(
            text = "Add some items to get started",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = DarkGreen.copy(alpha = 0.7f)
            ),
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}