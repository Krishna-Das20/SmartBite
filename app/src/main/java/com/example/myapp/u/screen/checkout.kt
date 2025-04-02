package com.example.myapp.u.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

// Color Palette

val TextGray = Color(0xFF616161)

// Temporary data storage
object CheckoutData {
    var selectedAddress by mutableStateOf<Address?>(null)
    var selectedPayment by mutableStateOf<PaymentMethod?>(null)

    val addresses = mutableStateListOf(
        Address("Home", "123 Main St, Apt 4B", "New York, NY 10001", "John Doe"),
        Address("Work", "456 Business Ave, Floor 12", "New York, NY 10005", "John Doe")
    )

    val paymentMethods = mutableStateListOf(
        PaymentMethod("Credit Card", "•••• •••• •••• 4242", "VISA"),
        PaymentMethod("PayPal", "user@example.com", "PayPal")
    )
}

data class Address(
    val title: String,
    val street: String,
    val city: String,
    val recipient: String
)

data class PaymentMethod(
    val type: String,
    val details: String,
    val provider: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(navController: NavController? = null) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Checkout",
                        color = White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = PrimaryGreen
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 8.dp),
                color = White
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total",
                            fontSize = 18.sp,
                            color = TextGray
                        )
                        Text(
                            text = "$125.99",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (CheckoutData.selectedAddress != null && CheckoutData.selectedPayment != null) {
                                navController?.navigate("order_success")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryGreen,
                            contentColor = White
                        ),
                        enabled = CheckoutData.selectedAddress != null && CheckoutData.selectedPayment != null
                    ) {
                        Text(
                            text = "Place Order",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(LightGray),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                // Shipping Address Section
                SectionHeader(
                    icon = Icons.Default.Place,
                    title = "Shipping Address",
                    actionText = "Add",
                    onActionClick = { /* Navigate to add address screen */ }
                )

                Spacer(modifier = Modifier.height(8.dp))

                CheckoutData.addresses.forEach { address ->
                    AddressCard(
                        address = address,
                        isSelected = CheckoutData.selectedAddress == address,
                        onSelect = { CheckoutData.selectedAddress = address },
                        onEdit = { /* Handle edit */ }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Payment Method Section
                SectionHeader(
                    icon = Icons.Default.Payment,
                    title = "Payment Method",
                    actionText = "Add",
                    onActionClick = { /* Navigate to add payment method screen */ }
                )

                Spacer(modifier = Modifier.height(8.dp))

                CheckoutData.paymentMethods.forEach { payment ->
                    PaymentCard(
                        payment = payment,
                        isSelected = CheckoutData.selectedPayment == payment,
                        onSelect = { CheckoutData.selectedPayment = payment },
                        onEdit = { /* Handle edit */ }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Order Summary Section
                SectionHeader(
                    icon = null,
                    title = "Order Summary",
                    actionText = null,
                    onActionClick = {}
                )

                OrderSummaryItem("Subtotal", "$120.00")
                OrderSummaryItem("Shipping", "$5.99")
                OrderSummaryItem("Tax", "$0.00")
                OrderSummaryItem("Discount", "-$10.00", isDiscount = true)
            }
        }
    }
}

@Composable
fun SectionHeader(
    icon: Any?,
    title: String,
    actionText: String?,
    onActionClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(
                    imageVector = icon as androidx.compose.ui.graphics.vector.ImageVector,
                    contentDescription = null,
                    tint = DarkGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp)) // Correct - horizontal spacing in Row
            }
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGreen
            )
        }
    }

    if (actionText != null) {
        TextButton(onClick = onActionClick) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = PrimaryGreen,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = actionText,
                    color = PrimaryGreen,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}


@Composable
fun AddressCard(
    address: Address,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) LightGreen else White
        ),
        border = if (isSelected) BorderStroke(1.dp, PrimaryGreen) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = address.title,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) DarkGreen else TextGray
                )

                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Address",
                        tint = PrimaryGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = address.street,
                color = TextGray
            )

            Text(
                text = address.city,
                color = TextGray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Recipient: ${address.recipient}",
                color = TextGray,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun PaymentCard(
    payment: PaymentMethod,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) LightGreen else White
        ),
        border = if (isSelected) BorderStroke(1.dp, PrimaryGreen) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = payment.type,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) DarkGreen else TextGray
                )

                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Payment",
                        tint = PrimaryGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = payment.details,
                color = TextGray
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = payment.provider,
                color = TextGray,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun OrderSummaryItem(label: String, value: String, isDiscount: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = TextGray,
            fontSize = 16.sp
        )
        Text(
            text = value,
            color = if (isDiscount) Color(0xFFE53935) else DarkGreen,
            fontSize = 16.sp,
            fontWeight = if (isDiscount) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCheckoutScreen() {
    MaterialTheme {
        CheckoutScreen(navController = rememberNavController())
    }
}