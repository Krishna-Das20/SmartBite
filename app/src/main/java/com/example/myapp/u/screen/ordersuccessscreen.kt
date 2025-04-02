package com.example.myapp.u.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun OrderSuccessScreen(navController: NavController) {
    var isVisible by remember { mutableStateOf(false) }

    // Scale animation from 0.5x to 1.0x smoothly
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.5f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "Scale Animation"
    )

    // Trigger animation with a delay
    LaunchedEffect(Unit) {
        delay(300) // Delay for smooth entry
        isVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(visible = isVisible) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.scale(scale)
            ) {
                // Animated Success Icon
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Order Success",
                    modifier = Modifier.size(100.dp),
                    tint = Color(0xFF4CAF50)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Animated Text
                Text(
                    text = "Order Confirmed!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Thank you for your purchase.\nYour order will be delivered soon!",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Button with smooth entry
                Button(
                    onClick = { navController.navigate("home") },
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(text = "Back to Home", fontSize = 18.sp, color = Color.White)
                }
            }
        }
    }
}
