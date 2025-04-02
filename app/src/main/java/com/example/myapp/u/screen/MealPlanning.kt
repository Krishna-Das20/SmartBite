package com.example.myapp.u.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.myapp.ui.theme.GreenDark
import com.example.myapp.ui.theme.GreenLight
import com.example.myapp.ui.theme.GreenPrimary
import com.example.myapp.ui.theme.OrangePrimary

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MealPlanningScreen(navController: NavController) {
    var selectedDay by remember { mutableStateOf("Mon") }
    var showNutritionInfo by remember { mutableStateOf(false) }
    var showCustomizeDialog by remember { mutableStateOf(false) }

    // Meal data with image URLs
    val meals = mapOf(
        "Mon" to listOf(
            Meal(
                "Breakfast",
                "Oatmeal with Berries",
                280,
                5,
                Color(0xFFFFF8E1),
                "https://images.unsplash.com/photo-1608500218807-0a35f4f0a13e"
            ),
            Meal(
                "Lunch",
                "Seafood Platter",
                520,
                30,
                Color(0xFFE3F2FD),
                "https://images.unsplash.com/photo-1551504734-5ee1c4a1479b"
            ),
            Meal(
                "Dinner",
                "Chicken Soup",
                360,
                24,
                Color(0xFFF1F8E9),
                "https://images.unsplash.com/photo-1547592180-85f173990554"
            )
        ),
        "Tue" to listOf(
            Meal(
                "Breakfast",
                "Pancakes with Syrup",
                350,
                6,
                Color(0xFFFFF8E1),
                "https://images.unsplash.com/photo-1567620905732-2d1ec7ab7445"
            ),
            Meal(
                "Lunch",
                "Grilled Chicken Salad",
                400,
                35,
                Color(0xFFE3F2FD),
                "https://images.unsplash.com/photo-1546069901-ba9599a7e63c"
            ),
            Meal(
                "Dinner",
                "Steak with Veggies",
                600,
                50,
                Color(0xFFF1F8E9),
                "https://images.unsplash.com/photo-1603360946369-dc9bb6258143"
            )
        ),
        "Wed" to listOf(
            Meal(
                "Breakfast",
                "Smoothie Bowl",
                300,
                8,
                Color(0xFFFFF8E1),
                "https://images.unsplash.com/photo-1490645935967-10de6ba17061"
            ),
            Meal(
                "Lunch",
                "Vegetarian Wrap",
                450,
                12,
                Color(0xFFE3F2FD),
                "https://images.unsplash.com/photo-1512621776951-a57141f2eefd"
            ),
            Meal(
                "Dinner",
                "Spaghetti",
                550,
                25,
                Color(0xFFF1F8E9),
                "https://images.unsplash.com/photo-1555949258-eb67b1ef0ceb"
            )
        ),
        "Thu" to listOf(
            Meal(
                "Breakfast",
                "Avocado Toast",
                280,
                7,
                Color(0xFFFFF8E1),
                "https://images.unsplash.com/photo-1551504734-5ee1c4a1479b"
            ),
            Meal(
                "Lunch",
                "Turkey Sandwich",
                380,
                28,
                Color(0xFFE3F2FD),
                "https://images.unsplash.com/photo-1571805618149-3a772570ebcd"
            ),
            Meal(
                "Dinner",
                "Fish Tacos",
                500,
                40,
                Color(0xFFF1F8E9),
                "https://images.unsplash.com/photo-1551504734-5ee1c4a1479b"
            )
        ),
        "Fri" to listOf(
            Meal(
                "Breakfast",
                "Egg and Bacon",
                400,
                18,
                Color(0xFFFFF8E1),
                "https://images.unsplash.com/photo-1551504734-5ee1c4a1479b"
            ),
            Meal(
                "Lunch",
                "Quinoa Salad",
                450,
                15,
                Color(0xFFE3F2FD),
                "https://images.unsplash.com/photo-1512621776951-a57141f2eefd"
            ),
            Meal(
                "Dinner",
                "Grilled Salmon",
                600,
                45,
                Color(0xFFF1F8E9),
                "https://images.unsplash.com/photo-1512621776951-a57141f2eefd"
            )
        )
    )

    // Calculate daily totals
    val dailyTotals by remember {
        derivedStateOf {
            meals[selectedDay]?.let { dayMeals ->
                dayMeals.fold(0 to 0) { (totalCalories, totalProtein), meal ->
                    (totalCalories + meal.calories) to (totalProtein + meal.protein)
                }
            } ?: (0 to 0)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Meal Planner",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = GreenDark
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = GreenDark
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showNutritionInfo = !showNutritionInfo }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Nutrition Info",
                            tint = GreenDark
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = GreenDark
                )
            )
        },
        containerColor = Color(0xFFF9F9F9)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF9F9F9))
        ) {
            // Daily Totals Summary
            AnimatedVisibility(
                visible = showNutritionInfo,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                DailySummaryCard(
                    calories = dailyTotals.first,
                    protein = dailyTotals.second,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // Weekday Selector
            WeekdaySelector(
                selectedDay = selectedDay,
                onDaySelected = { selectedDay = it },
                modifier = Modifier.padding(16.dp)
            )

            // Meals List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                meals[selectedDay]?.let { dayMeals ->
                    items(dayMeals, key = { it.mealName }) { meal ->
                        MealCard(
                            meal = meal,
                            modifier = Modifier.animateItemPlacement()
                        )
                    }
                }
            }

            // Customize Button
            Button(
                onClick = { showCustomizeDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GreenPrimary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 8.dp
                )
            ) {
                Text(
                    text = "Customize Meal Plan",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }

    // Customize Dialog
    if (showCustomizeDialog) {
        AlertDialog(
            onDismissRequest = { showCustomizeDialog = false },
            title = { Text("Customize Meal Plan") },
            text = { Text("What would you like to customize?") },
            confirmButton = {
                Button(
                    onClick = {
                        // Handle customization logic
                        showCustomizeDialog = false
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showCustomizeDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun DailySummaryCard(calories: Int, protein: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NutritionInfoItem(
                value = "$calories",
                unit = "kcal",
                label = "Calories",
                color = OrangePrimary
            )
            VerticalDivider()
            NutritionInfoItem(
                value = "$protein",
                unit = "g",
                label = "Protein",
                color = GreenPrimary
            )
        }
    }
}

@Composable
private fun NutritionInfoItem(value: String, unit: String, label: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            )
            Text(
                text = unit,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = color.copy(alpha = 0.8f)
                )
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color.Gray
            )
        )
    }
}

@Composable
private fun VerticalDivider() {
    Divider(
        modifier = Modifier
            .height(40.dp)
            .width(1.dp),
        color = Color.LightGray.copy(alpha = 0.3f)
    )
}

@Composable
private fun WeekdaySelector(
    selectedDay: String,
    onDaySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri")

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        days.forEach { day ->
            val isSelected = day == selectedDay
            val backgroundColor by animateColorAsState(
                targetValue = if (isSelected) GreenPrimary else Color.White,
                animationSpec = tween(durationMillis = 200)
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) Color.White else GreenDark
            )

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .shadow(
                        elevation = if (isSelected) 8.dp else 2.dp,
                        shape = CircleShape,
                        ambientColor = GreenPrimary.copy(alpha = 0.2f),
                        spotColor = GreenPrimary.copy(alpha = 0.2f)
                    )
                    .clip(CircleShape)
                    .background(backgroundColor)
                    .border(
                        width = if (isSelected) 0.dp else 1.dp,
                        color = GreenLight.copy(alpha = 0.5f),
                        shape = CircleShape
                    )
                    .clickable { onDaySelected(day) },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = day,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                    )
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.8f))
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MealCard(meal: Meal, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Food Image
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(meal.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = meal.mealName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
            )

            // Meal Details
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f)
            ) {
                // Meal type chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    GreenPrimary.copy(alpha = 0.2f),
                                    GreenPrimary.copy(alpha = 0.1f)
                                )
                            )
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = meal.mealType.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = GreenDark
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Meal name
                Text(
                    text = meal.mealName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Nutrition info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    NutritionBadge(
                        value = "${meal.calories}",
                        unit = "kcal",
                        icon = "🔥"
                    )
                    NutritionBadge(
                        value = "${meal.protein}",
                        unit = "g protein",
                        icon = "💪"
                    )
                }
            }
        }
    }
}

@Composable
private fun NutritionBadge(value: String, unit: String, icon: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = icon,
            fontSize = 16.sp
        )
        Column {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            )
            Text(
                text = unit,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.Gray
                )
            )
        }
    }
}

data class Meal(
    val mealType: String,
    val mealName: String,
    val calories: Int,
    val protein: Int,
    val cardColor: Color,
    val imageUrl: String
)