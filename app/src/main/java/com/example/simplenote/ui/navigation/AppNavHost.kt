package com.example.simplenote.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.simplenote.ui.auth.LoginScreen
import com.example.simplenote.ui.auth.RegisterScreen
import com.example.simplenote.ui.home.HomeNotesScreen
import com.example.simplenote.ui.note.NoteEditScreen
import com.example.simplenote.ui.onboarding.OnboardingScreen
import com.example.simplenote.ui.profile.ChangePasswordScreen
import com.example.simplenote.ui.profile.ProfileScreen
import com.example.simplenote.viewmodel.SessionViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    isDarkMode: Boolean,
    onThemeChanged: (Boolean) -> Unit
) {
    val sessionViewModel: SessionViewModel = hiltViewModel()
    val isLoggedIn by sessionViewModel.isLoggedIn.collectAsState()
    val onboardingCompleted by sessionViewModel.onboardingCompleted.collectAsState()

    LaunchedEffect(isLoggedIn, onboardingCompleted) {
        if (!isLoggedIn && onboardingCompleted) {
            navController.navigate(Screen.Login.route) {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) Screen.Home.route else Screen.Onboarding.route
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(navController = navController)
        }
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController = navController)
        }
        composable(Screen.Home.route) {
            HomeNotesScreen(navController = navController)
        }
        composable(
            route = Screen.NoteEdit.route + "?noteId={noteId}",
            arguments = listOf(
                navArgument("noteId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getInt("noteId") ?: -1
            NoteEditScreen(
                navController = navController,
                noteId = if (noteId == -1) null else noteId
            )
        }
        composable(route = Screen.Profile.route) {
            ProfileScreen(
                navController = navController,
                isDarkMode = isDarkMode,
                onThemeChanged = onThemeChanged
            )
        }
        composable(Screen.ChangePassword.route) {
            ChangePasswordScreen(navController = navController)
        }
    }
}

sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Home : Screen("home")
    data object NoteEdit : Screen("note_edit")
    data object Profile : Screen("profile")
    data object ChangePassword : Screen("change_password")
}
