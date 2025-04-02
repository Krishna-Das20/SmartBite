package com.example.myapp.u.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

// Custom Green Color Palette
val GreenLight = Color(0xFF81C784)   // Lighter Green
val GreenDark = Color(0xFF388E3C)    // Darker Green
val WhiteColor = Color(0xFFFFFFFF)   // White
val GreenContainer = Color(0xFFC8E6C9) // Light Green Container for Card Background

// Custom Color Scheme
val customColorScheme = lightColorScheme(
    primary = GreenDark,
    onPrimary = WhiteColor,
    secondary = GreenLight,
    onSecondary = WhiteColor,
    background = WhiteColor,
    surface = WhiteColor,
    onSurface = GreenDark,
    onBackground = GreenDark,
    error = Color.Red
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroceryListScreen(navController: NavController) {
    // Sample grocery data
    val groceryItems = remember {
        mutableStateListOf(
            "Organic Apples", "Almond Milk", "Whole Wheat Bread",
            "Free-range Eggs", "Cherry Tomatoes", "Avocados",
            "Greek Yogurt", "Spinach", "Chicken Breast", "Salmon Fillet",
            "Quinoa", "Extra Virgin Olive Oil", "Dark Chocolate"
        )
    }

    val itemPrices = remember {
        mutableStateListOf(
            4.99, 3.49, 2.99, 3.99, 2.49,
            1.99, 3.29, 1.79, 8.99, 12.99,
            5.49, 7.99, 4.49
        )
    }

    val itemQuantities = remember { mutableStateListOf(*Array(groceryItems.size) { 0 }) }

    // Calculate totals
    val totalItems by remember { derivedStateOf { itemQuantities.sum() } }
    val totalPrice by remember {
        derivedStateOf {
            itemQuantities.mapIndexed { index, qty -> qty * itemPrices[index] }.sum()
        }
    }

    // Use custom green color scheme in MaterialTheme
    MaterialTheme(colorScheme = customColorScheme) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Grocery List",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                )
            },
            bottomBar = {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "$totalItems items",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            )
                            Text(
                                text = "$${"%.2f".format(totalPrice)}",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }

                        Button(
                            onClick = { navController.navigate("cart") },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 4.dp,
                                pressedElevation = 8.dp
                            )
                        ) {
                            Text("Proceed to Checkout")
                        }
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp)
            ) {
                // Search Bar
                OutlinedTextField(
                    value = "",
                    onValueChange = { /* Handle search */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    placeholder = { Text("Search groceries...") },
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    },
                    singleLine = true
                )

                // Grocery Items List
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(groceryItems.size) { index ->
                        GroceryItemCard(
                            itemName = groceryItems[index],
                            price = itemPrices[index],
                            quantity = itemQuantities[index],
                            onAdd = { itemQuantities[index]++ },
                            onSubtract = {
                                if (itemQuantities[index] > 0) itemQuantities[index]--
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GroceryItemCard(
    itemName: String,
    price: Double,
    quantity: Int,
    onAdd: () -> Unit,
    onSubtract: () -> Unit
) {
    val containerColor = if (quantity > 0) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, shape = RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = itemName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$${"%.2f".format(price)} each",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            ) {
                // Subtract Button (only shown when quantity > 0)
                if (quantity > 0) {
                    IconButton(
                        onClick = onSubtract,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Remove,
                            contentDescription = "Remove Item",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Quantity Display with animation
                    Box(
                        modifier = Modifier
                            .width(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$quantity",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }

                // Add Button
                IconButton(
                    onClick = onAdd,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Item",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            // Total Price (only shown when quantity > 0)
            if (quantity > 0) {
                Text(
                    text = "$${"%.2f".format(price * quantity)}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}