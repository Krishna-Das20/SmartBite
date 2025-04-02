package com.example.inventoryapp

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*

// Data Classes
data class InventoryItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val purchaseDate: String,
    val expiryDate: String,
    val quantity: Int = 1
) {
    fun isExpiringSoon(): Boolean {
        return try {
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val expiryDate = dateFormat.parse(expiryDate)
            val currentDate = Calendar.getInstance().time
            val diff = expiryDate.time - currentDate.time
            val daysLeft = diff / (24 * 60 * 60 * 1000)
            daysLeft <= 1
        } catch (e: Exception) {
            false
        }
    }
}

data class Recipe(
    val name: String,
    val ingredients: List<String>,
    val prepTime: Int, // in minutes
    val difficulty: String // Easy, Medium, Hard
)

data class ChatMessage(
    val text: String,
    val isFromUser: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(navController: NavController) {
    // State Management
    val inventoryItems = remember { mutableStateListOf<InventoryItem>() }
    var showDialog by remember { mutableStateOf(false) }
    var showChatbot by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Recipe Database (100+ recipes)
    val allRecipes = remember {
        listOf(
            Recipe("Scrambled Eggs", listOf("eggs", "butter", "salt"), 5, "Easy"),
            Recipe("Vegetable Stir Fry", listOf("vegetables", "oil", "soy sauce"), 15, "Easy"),
            Recipe("Pasta with Tomato Sauce", listOf("pasta", "tomato sauce"), 15, "Easy"),
            Recipe("Grilled Cheese Sandwich", listOf("bread", "cheese", "butter"), 10, "Easy"),
            Recipe("Omelette", listOf("eggs", "cheese", "milk"), 10, "Easy"),
            Recipe("Pancakes", listOf("flour", "eggs", "milk", "sugar"), 20, "Easy"),
            Recipe("Spaghetti Carbonara", listOf("pasta", "eggs", "bacon", "cheese"), 25, "Medium"),
            Recipe("Chicken Curry", listOf("chicken", "rice", "curry powder"), 40, "Medium"),
            Recipe("Vegetable Soup", listOf("carrots", "potatoes", "onions"), 30, "Easy"),
            Recipe("Fruit Salad", listOf("apple", "banana", "orange"), 10, "Easy"),
            // ... 90+ more recipes ...
            Recipe("Beef Stew", listOf("beef", "potatoes", "carrots"), 120, "Medium")
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFF5F5F5),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showChatbot = true },
                containerColor = Color(0xFF2E7D32),
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Lightbulb, "AI Assistant") },
                text = { Text("Ask AI Chef") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Inventory",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                )

                IconButton(
                    onClick = { showDialog = true },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add item",
                        tint = Color(0xFF2E7D32)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Inventory Stats
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE8F5E9), RoundedCornerShape(12.dp))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = inventoryItems.size.toString(),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    )
                    Text("Items", color = Color(0xFF616161))
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = inventoryItems.count { it.isExpiringSoon() }.toString(),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD32F2F)
                        )
                    )
                    Text("Expiring Soon", color = Color(0xFF616161))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Inventory List
            if (inventoryItems.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Kitchen,
                        contentDescription = "Empty inventory",
                        tint = Color(0xFF9E9E9E),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Your inventory is empty",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color(0xFF616161)
                        )
                    )
                    Text(
                        text = "Add items to get started",
                        color = Color(0xFF9E9E9E)
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(inventoryItems.sortedBy { it.expiryDate }) { item ->
                        InventoryCard(
                            item = item,
                            onDelete = {
                                inventoryItems.remove(item)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "${item.name} removed",
                                        actionLabel = "Undo",
                                        duration = SnackbarDuration.Short
                                    ).also {
                                        if (it == SnackbarResult.ActionPerformed) {
                                            inventoryItems.add(item)
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }

        // Add Item Dialog
        if (showDialog) {
            AddItemDialog(
                onDismiss = { showDialog = false },
                onAdd = { name, purchaseDate, expiryDate ->
                    inventoryItems.add(InventoryItem(name = name, purchaseDate = purchaseDate, expiryDate = expiryDate))
                    showDialog = false
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("$name added to inventory")
                    }
                }
            )
        }

        // AI Chatbot Dialog
        if (showChatbot) {
            AIChatbotDialog(
                inventoryItems = inventoryItems,
                allRecipes = allRecipes,
                onDismiss = { showChatbot = false }
            )
        }
    }
}

@Composable
fun InventoryCard(
    item: InventoryItem,
    onDelete: () -> Unit
) {
    val isExpiringSoon = item.isExpiringSoon()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isExpiringSoon) Color(0xFFFFEBEE) else Color.White
        ),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isExpiringSoon) Color(0xFFD32F2F) else Color(0xFF1B5E20)
                        )
                    )

                    if (isExpiringSoon) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Expiring!",
                            color = Color(0xFFD32F2F),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Purchased", color = Color(0xFF616161))
                        Text(item.purchaseDate, color = Color.Black)
                    }

                    Column {
                        Text("Expires", color = Color(0xFF616161))
                        Text(item.expiryDate, color = if (isExpiringSoon) Color(0xFFD32F2F) else Color.Black)
                    }
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Item",
                    tint = Color(0xFFD32F2F)
                )
            }
        }
    }
}

@Composable
fun AddItemDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String) -> Unit
) {
    var itemName by remember { mutableStateOf("") }
    var purchaseDate by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Add New Item",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = itemName,
                    onValueChange = { itemName = it },
                    label = { Text("Item Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedLabelColor = Color(0xFF2E7D32),
                        focusedIndicatorColor = Color(0xFF2E7D32)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                DatePickerField(
                    label = "Purchase Date",
                    date = purchaseDate,
                    onDateSelected = { purchaseDate = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                DatePickerField(
                    label = "Expiry Date",
                    date = expiryDate,
                    onDateSelected = { expiryDate = it }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color(0xFF616161)
                        )
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            if (itemName.isNotBlank() && purchaseDate.isNotBlank() && expiryDate.isNotBlank()) {
                                onAdd(itemName, purchaseDate, expiryDate)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E7D32),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Add Item")
                    }
                }
            }
        }
    }
}

@Composable
fun DatePickerField(
    label: String,
    date: String,
    onDateSelected: (String) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    OutlinedTextField(
        value = date,
        onValueChange = {},
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDatePicker = true },
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = { showDatePicker = true }) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Pick date"
                )
            }
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedLabelColor = Color(0xFF2E7D32),
            focusedIndicatorColor = Color(0xFF2E7D32)
        )
    )

    if (showDatePicker) {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                calendar.set(year, month, day)
                onDateSelected(dateFormat.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}

@Composable

fun AIChatbotDialog(
    inventoryItems: List<InventoryItem>,
    allRecipes: List<Recipe>,
    onDismiss: () -> Unit
) {
    val messages = remember { mutableStateListOf<ChatMessage>() }
    var userInput by remember { mutableStateOf("") }
    var isTyping by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Initial message


    // Typing simulation function
    fun simulateTyping(
        text: String,
        scope: CoroutineScope,
        onTypingChange: (Boolean) -> Unit,
        onUpdate: (String) -> Unit
    ) {
        scope.launch {
            onTypingChange(true)
            var displayedText = ""
            text.forEach { char ->
                delay(30) // Typing speed
                displayedText += char
                onUpdate(displayedText)
            }
            onTypingChange(false)
        }
    }

    // Recipe suggestion logic
    fun getSuggestedRecipes(): List<Recipe> {
        val inventoryItemNames = inventoryItems.map { it.name.lowercase() }
        return allRecipes.filter { recipe ->
            recipe.ingredients.any { ingredient ->
                inventoryItemNames.any { it.contains(ingredient, ignoreCase = true) }
            }
        }.take(5)
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("AI Chef Assistant", style = MaterialTheme.typography.titleLarge)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, "Close")
                    }
                }

                // Chat messages
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(vertical = 8.dp)
                ) {
                    items(messages) { message ->
                        ChatBubble(
                            message = message.text,
                            isFromUser = message.isFromUser
                        )
                    }
                    if (isTyping) {
                        item {
                            ChatBubble(
                                message = "Typing...",
                                isFromUser = false,
                                isTyping = true
                            )
                        }
                    }
                }

                // Input field
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = userInput,
                        onValueChange = { userInput = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Ask for recipes...") }
                    )
                    IconButton(
                        onClick = {
                            if (userInput.isNotBlank()) {
                                // Add user message
                                messages.add(ChatMessage(userInput, true))

                                // Generate response
                                val response = if (userInput.contains("recipe", ignoreCase = true)) {
                                    val recipes = getSuggestedRecipes()
                                    if (recipes.isNotEmpty()) {
                                        "Here are recipes you can make:\n\n" +
                                                recipes.joinToString("\n\n") { recipe ->
                                                    "🍴 ${recipe.name}\n" +
                                                            "⏱ ${recipe.prepTime} mins | ${recipe.difficulty}\n" +
                                                            "🥗 Ingredients: ${recipe.ingredients.joinToString()}"
                                                }
                                    } else {
                                        "No matching recipes found. Try adding more ingredients!"
                                    }
                                } else {
                                    "I can help with recipe suggestions. Try asking 'What can I cook?'"
                                }

                                // Clear input
                                userInput = ""

                                // Show response with typing effect
                                simulateTyping(
                                    text = response,
                                    scope = coroutineScope,
                                    onTypingChange = { isTyping = it },
                                    onUpdate = { typedText ->
                                        if (messages.lastOrNull()?.isFromUser == true) {
                                            messages.add(ChatMessage(typedText, false))
                                        } else if (messages.isNotEmpty()) {
                                            messages[messages.lastIndex] = ChatMessage(typedText, false)
                                        }
                                    }
                                )
                            }
                        }
                    ) {
                        Icon(Icons.Default.Send, "Send")
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(
    message: String,
    isFromUser: Boolean,
    isTyping: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        contentAlignment = if (isFromUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Card(
            shape = RoundedCornerShape(
                topStart = if (isFromUser) 16.dp else 4.dp,
                topEnd = if (isFromUser) 4.dp else 16.dp,
                bottomStart = 16.dp,
                bottomEnd = 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (isFromUser) Color(0xFFE3F2FD) else Color(0xFFE8F5E9)
            ),
            modifier = Modifier.padding(4.dp)
        ) {
            Text(
                text = message,
                modifier = Modifier.padding(12.dp),
                color = if (isFromUser) Color(0xFF0D47A1) else Color.Black
            )
        }
    }
}