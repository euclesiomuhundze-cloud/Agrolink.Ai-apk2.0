package com.agon.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Yard
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.agon.app.ui.screens.AuthScreen
import com.agon.app.ui.screens.CreateProfileScreen
import com.agon.app.ui.screens.CropDiagnosisScreen
import com.agon.app.ui.screens.DiagnosisResultScreen
import com.agon.app.ui.screens.HomeScreen
import com.agon.app.ui.screens.ImpactScreen
import com.agon.app.ui.screens.AgroHubScreen
import com.agon.app.ui.screens.BiscatoHubScreen
import com.agon.app.ui.screens.ChatScreen
import com.agon.app.ui.screens.JobDetailScreen
import com.agon.app.ui.screens.JobsListScreen
import com.agon.app.ui.screens.MarketScreen
import com.agon.app.ui.screens.MarketplaceScreen
import com.agon.app.ui.screens.PostJobScreen
import com.agon.app.ui.screens.SettingsScreen
import com.agon.app.ui.screens.TipsScreen
import com.agon.app.ui.screens.WeatherScreen
import com.agon.app.ui.screens.WorkerDetailScreen
import com.agon.app.ui.screens.WorkersListScreen
import com.agon.app.ui.theme.AgonAppTheme
import com.agon.app.viewmodel.AppViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            AgonAppTheme {
                MainApp()
            }
        }
    }
}

private val TOP_LEVEL_ROUTES = setOf("home", "agro", "biscato", "impact", "settings")

@Composable
fun MainApp() {
    val navController = rememberNavController()
    val viewModel: AppViewModel = viewModel()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in TOP_LEVEL_ROUTES

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
            ) {
                BottomNav(navController, currentRoute)
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (viewModel.currentUser.value != null) "home" else "auth",
            modifier = Modifier.padding(innerPadding),
        ) {
            composable("auth") { AuthScreen(navController, viewModel) }
            composable("home") { HomeScreen(navController, viewModel) }
            composable("agro") { AgroHubScreen(navController, viewModel) }
            composable("biscato") { BiscatoHubScreen(navController, viewModel) }
            composable("impact") { ImpactScreen(navController, viewModel) }
            composable("settings") { SettingsScreen(navController, viewModel) }

            // AgroIA sub-screens
            composable("agro/diagnosis") { CropDiagnosisScreen(navController, viewModel) }
            composable("agro/diagnosis_result") { DiagnosisResultScreen(navController, viewModel) }
            composable("agro/weather") { WeatherScreen(navController) }
            composable("agro/market") { MarketScreen(navController) }
            composable("agro/tips") { TipsScreen(navController) }
            composable("agro/marketplace") { MarketplaceScreen(navController) }

            // BiscatoHub sub-screens
            composable("biscato/jobs") { JobsListScreen(navController, viewModel) }
            composable("biscato/workers") { WorkersListScreen(navController, viewModel) }
            composable(
                "biscato/job/{jobId}",
                arguments = listOf(navArgument("jobId") { type = NavType.StringType }),
            ) { backStackEntry ->
                val jobId = backStackEntry.arguments?.getString("jobId") ?: ""
                JobDetailScreen(navController, viewModel, jobId)
            }
            composable(
                "biscato/worker/{workerId}",
                arguments = listOf(navArgument("workerId") { type = NavType.StringType }),
            ) { backStackEntry ->
                val workerId = backStackEntry.arguments?.getString("workerId") ?: ""
                WorkerDetailScreen(navController, viewModel, workerId)
            }
            composable(
                "biscato/chat/{workerId}",
                arguments = listOf(navArgument("workerId") { type = NavType.StringType }),
            ) { backStackEntry ->
                val workerId = backStackEntry.arguments?.getString("workerId") ?: ""
                ChatScreen(navController, viewModel, workerId)
            }
            composable("biscato/post_job") { PostJobScreen(navController, viewModel) }
            composable("biscato/create_profile") { CreateProfileScreen(navController, viewModel) }
        }
    }
}

@Composable
fun BottomNav(navController: NavHostController, currentRoute: String?) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Início") },
            label = { Text("Início") },
            selected = currentRoute == "home",
            onClick = {
                navController.navigate("home") { popUpTo("home") { inclusive = true } }
            },
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Yard, contentDescription = "AgroIA") },
            label = { Text("AgroIA") },
            selected = currentRoute == "agro",
            onClick = {
                navController.navigate("agro") { popUpTo("home") }
            },
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Handshake, contentDescription = "BiscatoHub") },
            label = { Text("Biscatos") },
            selected = currentRoute == "biscato",
            onClick = {
                navController.navigate("biscato") { popUpTo("home") }
            },
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Insights, contentDescription = "Impacto") },
            label = { Text("Impacto") },
            selected = currentRoute == "impact",
            onClick = {
                navController.navigate("impact") { popUpTo("home") }
            },
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = "Definições") },
            label = { Text("Definições") },
            selected = currentRoute == "settings",
            onClick = {
                navController.navigate("settings") { popUpTo("home") }
            },
        )
    }
}
