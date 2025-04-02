package com.example.myapp.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.inventoryapp.InventoryScreen
import com.example.myapp.u.screen.*
import com.example.myapp.u.screen.CheckoutScreen
import com.example.myapp.u.screen.OrderSuccessScreen
import com.example.myapp.ui.auth.LoginScreen
import com.example.myapp.ui.auth.SignUpScreen
import com.example.myapp.ui.screen.HomeScreen
import androidx.compose.ui.Alignment


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController, startDestination = "home") {

        // Home Screen
        composable(
            "home",
            enterTransition = { fadeIn(animationSpec = tween(500)) },
            exitTransition = { fadeOut(animationSpec = tween(500)) }
        ) { HomeScreen(navController) }

        // Meal Planning
        composable(
            "meal_planning",
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(500)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(500)) }
        ) { MealPlanningScreen(navController) }

        // Grocery List
        composable(
            "grocery_list",
            enterTransition = { slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(500)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(500)) }
        ) { GroceryListScreen(navController) }

        // Cart
        composable(
            "cart",
            enterTransition = { scaleIn(initialScale = 0.8f, animationSpec = tween(500)) },
            exitTransition = { scaleOut(targetScale = 0.8f, animationSpec = tween(500)) }
        ) { CartScreen(navController) }

        // Checkout
        composable(
            "checkout",
            enterTransition = { slideInVertically(initialOffsetY = { 1000 }) + fadeIn() },
            exitTransition = { slideOutVertically(targetOffsetY = { -1000 }) + fadeOut() }
        ) { CheckoutScreen(navController) }

        // Order Success
        composable(
            "order_success",
            enterTransition = { scaleIn(initialScale = 0.5f) + fadeIn() },
            exitTransition = { scaleOut(targetScale = 0.5f) + fadeOut() }
        ) { OrderSuccessScreen(navController) }

        // Recipes
        composable(
            "recipes",
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) }
        ) { RecipeScreen(navController) }

        // Recipe Details
        composable(
            "recipe_details/{recipeName}",
            enterTransition = { expandIn(expandFrom = Alignment.Center) + fadeIn() },
            exitTransition = { shrinkOut(shrinkTowards = Alignment.Center) + fadeOut() }
        ) { backStackEntry ->
            val recipeName = backStackEntry.arguments?.getString("recipeName") ?: "Default Recipe"
            RecipeDetailsScreen(navController, recipeName)
        }

        // Inventory
        composable(
            "inventory",
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn() },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut() }
        ) { InventoryScreen(navController) }

        // Login Screen
        composable(
            "login",
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn() },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut() }
        ) { LoginScreen(navController) }

        // Register Screen
        composable(
            "register",
            enterTransition = { slideInVertically(initialOffsetY = { 1000 }) + fadeIn() },
            exitTransition = { slideOutVertically(targetOffsetY = { -1000 }) + fadeOut() }
        ) { SignUpScreen(navController) }

        // Notifications
        composable(
            "notifications",
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn() },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut() }
        ) { NotificationScreen(navController) }
    }
}
