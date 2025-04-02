package com.example.myapp.u.screen

import androidx.compose.animation.AnimatedVisibility

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.myapp.ui.theme.GreenPrimary

import com.example.myapp.ui.theme.OrangePrimary
import com.example.myapp.ui.theme.OrangeSecondary

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun RecipeDetailsScreen(navController: NavController, recipeName: String) {
    // Get recipe details
    val (nutritionalInfo, recipeDetails) = getRecipeDetails(recipeName)
    val (ingredients, steps) = recipeDetails

    // UI states
    var userInput by remember { mutableStateOf(TextFieldValue("")) }
    val conversationHistory = remember { mutableStateListOf<String>() }
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    var expandedSections by remember { mutableStateOf(setOf<String>()) }
    var scale by remember { mutableStateOf(1f) }

    // Sample image URL (replace with actual recipe image)
    val recipeImageUrl = when (recipeName) {
        "Spaghetti" -> "https://images.unsplash.com/photo-1555949258-eb67b1ef0ceb"
        "Grilled Chicken Salad" -> "https://images.unsplash.com/photo-1546069901-ba9599a7e63c"
        "Vegetable Stir Fry" -> "https://images.unsplash.com/photo-1626700051175-6818013e1d4f"
        "Pancakes" -> "https://images.unsplash.com/photo-1567620905732-2d1ec7ab7445"
        else -> "https://images.unsplash.com/photo-1490645935967-10de6ba17061"
    }

    // Handle chatbot responses
    fun handleChatbotResponse(input: String) {
        val response = when {
            input.contains("ingredients", true) -> "Here are the ingredients for $recipeName: ${ingredients.joinToString()}"
            input.contains("steps", true) -> "Here are the steps to make $recipeName: ${steps.joinToString("\n")}"
            input.contains("hello", true) -> "Hello! How can I assist you today?"
            input.contains("thank you", true) -> "You're welcome! Let me know if you need more help."
            input.contains("help", true) -> "I can assist you with recipe ingredients, cooking steps, or anything else related to the recipe. Just ask!"
            else -> "I'm not sure about that, but I can help with ingredients and steps!"
        }
        conversationHistory.add("You: $input")
        conversationHistory.add("Bot: $response")
        dialogMessage = response
        showDialog = true
    }

    // Toggle section expansion
    fun toggleSection(section: String) {
        expandedSections = if (expandedSections.contains(section)) {
            expandedSections - section
        } else {
            expandedSections + section
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = recipeName,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GreenPrimary,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF9F9F9)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF9F9F9)),
            state = rememberLazyListState(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Recipe Image
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .pointerInput(Unit) {
                            detectTransformGestures { _, _, zoom, _ ->
                                scale = (scale * zoom).coerceIn(0.5f, 3f)
                            }
                        }
                ) {
                    AsyncImage(
                        model = recipeImageUrl,
                        contentDescription = recipeName,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale
                            ), // Ensures the image scales properly
                        contentScale = ContentScale.Crop
                    )


                    // Image overlay with cooking time
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                            .background(
                                color = GreenPrimary.copy(alpha = 0.8f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "⏱️ ${nutritionalInfo["Time"] ?: "30 mins"}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Description
            item {
                Text(
                    text = "A delicious and nutritious meal packed with rich flavors.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                )
            }

            // Quick Info Cards
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Calories Card
                    InfoCard(
                        value = nutritionalInfo["Calories"] ?: "N/A",
                        label = "Calories",
                        color = OrangePrimary,
                        modifier = Modifier.weight(1f)
                    )

                    // Protein Card
                    InfoCard(
                        value = nutritionalInfo["Protein"] ?: "N/A",
                        label = "Protein",
                        color = GreenPrimary,
                        modifier = Modifier.weight(1f)
                    )

                    // Carbs Card
                    InfoCard(
                        value = nutritionalInfo["Carbs"] ?: "N/A",
                        label = "Carbs",
                        color = OrangeSecondary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Ingredients Section
            item {
                ExpandableSection(
                    title = "Ingredients",
                    isExpanded = expandedSections.contains("ingredients"),
                    onToggle = { toggleSection("ingredients") }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ingredients.forEachIndexed { index, ingredient ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(
                                            color = GreenPrimary.copy(alpha = 0.2f),
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        color = GreenPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = ingredient,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (index < ingredients.size - 1) {
                                Divider(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    color = Color.LightGray.copy(alpha = 0.3f)
                                )
                            }
                        }
                    }
                }
            }

            // Steps Section
            item {
                ExpandableSection(
                    title = "Cooking Steps",
                    isExpanded = expandedSections.contains("steps"),
                    onToggle = { toggleSection("steps") }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        steps.forEachIndexed { index, step ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(
                                            color = OrangePrimary.copy(alpha = 0.2f),
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        color = OrangePrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = step,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            // Full Nutrition Section
            item {
                ExpandableSection(
                    title = "Nutritional Information",
                    isExpanded = expandedSections.contains("nutrition"),
                    onToggle = { toggleSection("nutrition") }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        nutritionalInfo.forEach { (name, value) ->
                            if (name !in listOf("Time", "Servings")) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = name,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = Color.Gray
                                        )
                                    )
                                    Text(
                                        text = value,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                                if (name != nutritionalInfo.keys.last()) {
                                    Divider(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        color = Color.LightGray.copy(alpha = 0.3f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Chatbot Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Recipe Assistant",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = GreenPrimary
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Chat history
                    if (conversationHistory.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .background(
                                    color = Color(0xFFF5F5F5),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(conversationHistory) { message ->
                                val isUser = message.startsWith("You:")
                                Text(
                                    text = message,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (isUser) GreenPrimary else Color.DarkGray
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Chat input
                    ChatInput(
                        value = userInput,
                        onValueChange = { userInput = it },
                        onSend = {
                            if (userInput.text.isNotBlank()) {
                                handleChatbotResponse(userInput.text)
                                userInput = TextFieldValue("")
                            }
                        }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }

    // Chatbot Response Dialog
    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            AnimatedVisibility(
                visible = showDialog,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .shadow(16.dp, shape = RoundedCornerShape(16.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(
                                    color = GreenPrimary.copy(alpha = 0.1f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🤖",
                                fontSize = 32.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Recipe Assistant",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = GreenPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = dialogMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { showDialog = false },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GreenPrimary,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Got it!")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoCard(value: String, label: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.Gray
                )
            )
        }
    }
}

@Composable
fun ExpandableSection(
    title: String,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggle() }
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            )
            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                tint = GreenPrimary
            )
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            content()
        }
    }
}

@Composable
fun ChatInput(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(
                color = Color(0xFFF5F5F5),
                shape = RoundedCornerShape(28.dp)
            )
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = Color.Black
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Send
            ),
            keyboardActions = KeyboardActions(
                onSend = {
                    onSend()
                    focusManager.clearFocus()
                }
            ),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.text.isEmpty()) {
                        Text(
                            text = "Ask about this recipe...",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.Gray
                            )
                        )
                    }
                    innerTextField()
                }
            }
        )

        IconButton(
            onClick = {
                onSend()
                focusManager.clearFocus()
            },
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = GreenPrimary,
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Send",
                tint = Color.White
            )
        }
    }
}

// Function that returns recipe details (same as before)
@Composable
fun getRecipeDetails(recipeName: String): Pair<Map<String, String>, Pair<List<String>, List<String>>> {
    return when (recipeName) {
        "Spaghetti" -> Pair(
            mapOf(
                "Calories" to "450 kcal",
                "Protein" to "20g",
                "Carbs" to "55g",
                "Fat" to "15g",
                "Time" to "30 mins",
                "Servings" to "2"
            ),
            Pair(
                listOf("200g Spaghetti", "2 Tomatoes", "Olive Oil", "Garlic", "Salt"),
                listOf(
                    "Boil water in a large pot and add spaghetti.",
                    "Heat olive oil in a pan and sauté garlic.",
                    "Add chopped tomatoes and simmer for 10 minutes.",
                    "Combine spaghetti with sauce, add salt to taste."
                )
            )
        )
        "Grilled Chicken Salad" -> Pair(
            mapOf(
                "Calories" to "300 kcal",
                "Protein" to "35g",
                "Carbs" to "10g",
                "Fat" to "8g",
                "Time" to "25 mins",
                "Servings" to "1"
            ),
            Pair(
                listOf("1 Chicken Breast", "Lettuce", "Cucumber", "Olive Oil", "Lemon"),
                listOf(
                    "Grill the chicken breast until fully cooked.",
                    "Chop the lettuce, cucumber, and slice the chicken.",
                    "Toss the salad with olive oil and lemon juice."
                )
            )
        )
        "Vegetable Stir Fry" -> Pair(
            mapOf(
                "Calories" to "250 kcal",
                "Protein" to "10g",
                "Carbs" to "40g",
                "Fat" to "5g",
                "Time" to "20 mins",
                "Servings" to "2"
            ),
            Pair(
                listOf("Broccoli", "Carrots", "Bell Peppers", "Soy Sauce", "Ginger"),
                listOf(
                    "Chop the vegetables into small pieces.",
                    "Heat a pan and stir fry vegetables for 5-7 minutes.",
                    "Add soy sauce and ginger, stir well for another 3 minutes."
                )
            )
        )
        "Pancakes" -> Pair(
            mapOf(
                "Calories" to "350 kcal",
                "Protein" to "8g",
                "Carbs" to "60g",
                "Fat" to "10g",
                "Time" to "15 mins",
                "Servings" to "4"
            ),
            Pair(
                listOf("1 Cup Flour", "1 Egg", "Milk", "Butter", "Sugar"),
                listOf(
                    "Mix the flour, egg, milk, and sugar in a bowl.",
                    "Heat a pan and melt butter.",
                    "Pour the batter into the pan and cook until both sides are golden."
                )
            )
        )
        else -> Pair(
            mapOf(
                "Calories" to "Unknown",
                "Protein" to "Unknown",
                "Carbs" to "Unknown",
                "Fat" to "Unknown",
                "Time" to "Unknown",
                "Servings" to "Unknown"
            ),
            Pair(emptyList(), emptyList())
        )
    }
}