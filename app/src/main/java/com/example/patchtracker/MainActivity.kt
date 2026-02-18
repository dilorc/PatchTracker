package com.example.patchtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.patchtracker.ui.LogsScreen
import com.example.patchtracker.ui.SettingsScreen
import com.example.patchtracker.ui.theme.PatchTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PatchTrackerTheme {
                PatchTrackerApp()
            }
        }
    }
}

@Composable
fun PatchTrackerApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "main",
        modifier = Modifier.fillMaxSize()
    ) {
        composable("main") {
            MainScreen(
                onNavigateToSettings = {
                    navController.navigate("settings")
                },
                onNavigateToLogs = {
                    navController.navigate("logs")
                }
            )
        }

        composable("logs") {
            LogsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("settings") {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

