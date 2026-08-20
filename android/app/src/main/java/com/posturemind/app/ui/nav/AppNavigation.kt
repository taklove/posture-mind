package com.posturemind.app.ui.nav

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.posturemind.app.ui.about.AboutScreen
import com.posturemind.app.ui.capture.CaptureScreen
import com.posturemind.app.ui.exercise.ExerciseScreen
import com.posturemind.app.ui.home.HomeScreen
import com.posturemind.app.ui.progress.ProgressScreen
import com.posturemind.app.ui.result.ResultScreen
import com.posturemind.app.ui.training.TrainingScreen
import com.posturemind.app.viewmodel.PostureViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: PostureViewModel = viewModel()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            // 只在主页面显示底部导航
            if (currentRoute in listOf(
                    Routes.HOME, Routes.CAPTURE, Routes.TRAINING, Routes.PROGRESS
                )
            ) {
                BottomNavBar(
                    currentRoute = currentRoute ?: Routes.HOME,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    onStartCapture = { navController.navigate(Routes.CAPTURE) },
                    onAbout = { navController.navigate(Routes.ABOUT) }
                )
            }
            composable(Routes.CAPTURE) {
                CaptureScreen(
                    viewModel = viewModel,
                    onAnalyze = {
                        viewModel.runFinalAnalysis()
                        navController.navigate(Routes.RESULT)
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.RESULT) {
                ResultScreen(
                    viewModel = viewModel,
                    onStartTraining = { navController.navigate(Routes.TRAINING) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.TRAINING) {
                TrainingScreen(
                    viewModel = viewModel,
                    onExerciseClick = { id ->
                        navController.navigate(Routes.exercise(id))
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = Routes.EXERCISE,
                arguments = listOf(navArgument("exerciseId") { type = NavType.StringType })
            ) { entry ->
                val id = entry.arguments?.getString("exerciseId") ?: ""
                ExerciseScreen(
                    exerciseId = id,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.PROGRESS) {
                ProgressScreen(viewModel = viewModel)
            }
            composable(Routes.ABOUT) {
                AboutScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}

@Composable
private fun BottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar {
        BottomNavItem(currentRoute, Routes.HOME, "首页", Icons.Filled.Home, onNavigate)
        BottomNavItem(currentRoute, Routes.CAPTURE, "评估", Icons.Filled.PhotoCamera, onNavigate)
        BottomNavItem(currentRoute, Routes.TRAINING, "训练", Icons.Filled.FitnessCenter, onNavigate)
        BottomNavItem(currentRoute, Routes.PROGRESS, "记录", Icons.Filled.Timeline, onNavigate)
    }
}

@Composable
private fun RowScope.BottomNavItem(
    currentRoute: String,
    route: String,
    label: String,
    icon: ImageVector,
    onNavigate: (String) -> Unit
) {
    NavigationBarItem(
        selected = currentRoute == route,
        onClick = { onNavigate(route) },
        icon = { Icon(icon, contentDescription = label) },
        label = { Text(label) }
    )
}
