package com.example.myapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest

// Light theme colors
private val LightPrimaryGreen = Color(0xFF2E7D32)
private val LightDarkGreen = Color(0xFF1B5E20)
private val LightLightGreen = Color(0xFFC8E6C9)
private val LightBackgroundGradient = Brush.verticalGradient(
    colors = listOf(Color.White, LightLightGreen)
)

// Dark theme colors
private val DarkPrimaryGreen = Color(0xFF81C784)
private val DarkDarkGreen = Color(0xFF4CAF50)
private val DarkLightGreen = Color(0xFF1E3521)
private val DarkBackgroundGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF121212), DarkLightGreen)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    val isDarkTheme = remember { mutableStateOf(false) }

    val currentPrimaryGreen = if (isDarkTheme.value) DarkPrimaryGreen else LightPrimaryGreen
    val currentDarkGreen = if (isDarkTheme.value) DarkDarkGreen else LightDarkGreen
    val currentBackgroundGradient = if (isDarkTheme.value) DarkBackgroundGradient else LightBackgroundGradient

    MaterialTheme(
        colorScheme = if (isDarkTheme.value) darkColorScheme() else lightColorScheme(),
        typography = MaterialTheme.typography
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(currentBackgroundGradient)
        ) {
            item { HomeHeader(navController, isDarkTheme, currentDarkGreen, currentPrimaryGreen) }
            item { Spacer(modifier = Modifier.height(24.dp)) }
            item { TodaySpecialSection(currentDarkGreen) }
            item { Spacer(modifier = Modifier.height(24.dp)) }
            item { FoodCategoriesSection(currentDarkGreen) }
            item { Spacer(modifier = Modifier.height(24.dp)) }
            item { EatingHistorySection(currentDarkGreen) }
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun HomeHeader(
    navController: NavHostController,
    isDarkTheme: androidx.compose.runtime.MutableState<Boolean>,
    darkGreen: Color,
    primaryGreen: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Good Morning!",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = darkGreen
                )
            )
            Text(
                text = "Let's eat something healthy",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Theme toggle button
            IconButton(
                onClick = { isDarkTheme.value = !isDarkTheme.value },
                modifier = Modifier
                    .size(48.dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(12.dp),
                        clip = true
                    )
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Icon(
                    imageVector = if (isDarkTheme.value) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Toggle Theme",
                    tint = primaryGreen,
                    modifier = Modifier.size(24.dp))
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = { navController.navigate("login") },
                modifier = Modifier
                    .size(48.dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(12.dp),
                        clip = true
                    )
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Login",
                    tint = primaryGreen,
                    modifier = Modifier.size(24.dp))
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = { navController.navigate("notifications") }, // Navigate to NotificationScreen
                modifier = Modifier
                    .size(48.dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(12.dp),
                        clip = true
                    )
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = primaryGreen,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun TodaySpecialSection(darkGreen: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("https://images.unsplash.com/photo-1546069901-ba9599a7e63c") // Healthy bowl
                    .crossfade(true)
                    .build(),
                contentDescription = "Today's special meal",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Today's Special",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = darkGreen
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Superfood grain bowl with quinoa, kale, and avocado",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun FoodCategoriesSection(darkGreen: Color) {
    val foodCategories = listOf(
        "Salads" to "https://images.unsplash.com/photo-1540420773420-3366772f4999",
        "Protein" to "https://images.unsplash.com/photo-1555939594-58d7cb561ad1",
        "Carbs" to "https://images.unsplash.com/photo-1512621776951-a57141f2eefd",
        "Italian" to "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38",
        "Mexician" to "https://images.unsplash.com/photo-1551504734-5ee1c4a1479b"
    )

    Column(modifier = Modifier.padding(start = 24.dp)) {
        Text(
            text = "Food Categories",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = darkGreen
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(end = 24.dp)
        ) {
            items(foodCategories) { (title, imageUrl) ->
                FoodCategoryItem(title, imageUrl)
            }
        }
    }
}

@Composable
private fun FoodCategoryItem(title: String, imageUrl: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(120.dp)
    ) {
        Card(
            modifier = Modifier.size(100.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun EatingHistorySection(darkGreen: Color) {
    val mealHistory = listOf(
        Triple("Samosa", "Today, 7:30 AM", "https://images.unsplash.com/photo-1601050690597-df0568f70950"),
        Triple("Salmon with Rice", "Yesterday, 3:45 PM", "https://images.unsplash.com/photo-1563379926898-05f4575a45d8"),
        Triple("Vegetable Pasta", "Yesterday, 12:30 PM", "https://images.unsplash.com/photo-1547592180-85f173990554")
    )

    Column(modifier = Modifier.padding(start = 24.dp)) {
        Text(
            text = "Recent Meals",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = darkGreen
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        mealHistory.forEach { (title, time, imageUrl) ->
            MealHistoryCard(title, time, imageUrl)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun MealHistoryCard(title: String, time: String, imageUrl: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 24.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = time,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}