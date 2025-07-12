package com.example.simplenote.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.simplenote.ui.auth.LoginScreen
import com.example.simplenote.ui.auth.RegisterScreen
import com.example.simplenote.ui.home.HomeNotesScreen
import com.example.simplenote.ui.note.NoteEditScreen
import com.example.simplenote.ui.onboarding.OnboardingScreen
import com.example.simplenote.ui.profile.ChangePasswordScreen
import com.example.simplenote.ui.profile.ProfileScreen
import com.example.simplenote.viewmodel.NoteViewModel
import com.example.simplenote.viewmodel.SessionViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    isLoggedIn: Boolean,
    onboardingCompleted: Boolean,
    isDarkMode: Boolean,
    onThemeChanged: (Boolean) -> Unit
) {

    // This logic now works perfectly because the initial states are correct.
    val startDestination = when {
        !onboardingCompleted -> Screen.Onboarding.route
        !isLoggedIn -> Screen.Login.route
        else -> "notes_graph"
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Onboarding.route) {
            // Hilt can still provide the ViewModel here for completing the onboarding
            val sessionViewModel: SessionViewModel = hiltViewModel()
            OnboardingScreen(
                navController = navController,
                sessionViewModel = sessionViewModel
            )
        }
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController = navController)
        }

        navigation(
            startDestination = Screen.Home.route,
            route = "notes_graph"
        ) {
            composable(Screen.Home.route) { navBackStackEntry ->
                val parentEntry = remember(navBackStackEntry) {
                    navController.getBackStackEntry("notes_graph")
                }
                val noteViewModel = hiltViewModel<NoteViewModel>(parentEntry)
                HomeNotesScreen(navController = navController, viewModel = noteViewModel)
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
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("notes_graph")
                }
                val noteViewModel = hiltViewModel<NoteViewModel>(parentEntry)
                val noteId = backStackEntry.arguments?.getInt("noteId") ?: -1
                NoteEditScreen(
                    navController = navController,
                    noteId = if (noteId == -1) null else noteId,
                    viewModel = noteViewModel
                )
            }
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
