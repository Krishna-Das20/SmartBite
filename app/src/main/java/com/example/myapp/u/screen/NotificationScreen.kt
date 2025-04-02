package com.example.myapp.u.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun NotificationScreen(navController: NavController) {
    val notifications = remember { mutableStateListOf("Order Delivered!", "New Healthy Recipe Added", "Your Meal Plan is Ready!") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Notifications", style = MaterialTheme.typography.headlineMedium)

        if (notifications.isEmpty()) {
            Text("No new notifications", modifier = Modifier.padding(16.dp))
        } else {
            LazyColumn {
                items(notifications) { notification ->
                    NotificationItem(notification)
                }
            }
        }
    }
}

@Composable
fun NotificationItem(notification: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(text = notification, modifier = Modifier.padding(16.dp))
    }
}
