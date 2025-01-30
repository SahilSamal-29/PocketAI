package com.example.pocketai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pocketai.ui.theme.PocketAiTheme

const val START_SCREEN = "start_screen"
const val CHAT_SCREEN = "chat_screen"
const val SETTINGS_SCREEN = "settings_screen"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PocketAiTheme  {
                val navController = rememberNavController() //  Move navigation controller here

                Scaffold(
                    topBar = {
                        // Get current navigation route
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentRoute = navBackStackEntry?.destination?.route

                        // Only show AppBar if NOT on Settings Screen
                        if (currentRoute != SETTINGS_SCREEN) {
                            AppBar(navController)
                        }
                    }
                ) { innerPadding ->
                    // Existing NavHost setup
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = START_SCREEN
                        ) {
                            composable(START_SCREEN) {
                                LoadingRoute(
                                    onModelLoaded = {
                                        navController.navigate(CHAT_SCREEN) {
                                            popUpTo(START_SCREEN) { inclusive = true }
                                            launchSingleTop = true
                                        }
                                    }
                                )
                            }

                            composable(CHAT_SCREEN) {
                                ChatRoute()
                            }

                            composable(SETTINGS_SCREEN) {
                                SettingsScreen(navController, LocalContext.current) // ✅ Use LocalContext.current
                            }
                        }
                    }
                }
            }
        }
    }

    // TopAppBar is marked as experimental in Material 3
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AppBar(navController: NavController) {
        TopAppBar(
            title = { Text(stringResource(R.string.app_name)) },
            colors = TopAppBarDefaults.topAppBarColors(
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
            actions = {
                IconButton(onClick = { navController.navigate(SETTINGS_SCREEN) }) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = "settings")
                }
            }
        )
    }
}