package com.example.simplenote.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.simplenote.ui.components.SimpleTopAppBar
import com.example.simplenote.ui.components.TextLink
import com.example.simplenote.ui.navigation.Screen
import com.example.simplenote.ui.theme.TextBase
import com.example.simplenote.ui.theme.TextLG
import com.example.simplenote.ui.theme.TextXS
import com.example.simplenote.util.Resource
import com.example.simplenote.viewmodel.AuthViewModel
import compose.icons.TablerIcons
import compose.icons.tablericons.ChevronRight
import compose.icons.tablericons.Lock
import compose.icons.tablericons.Logout
import compose.icons.tablericons.Mail
import compose.icons.tablericons.Moon
import compose.icons.tablericons.User

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel(),
    isDarkMode: Boolean,
    onThemeChanged: (Boolean) -> Unit
) {
    val userInfoState by viewModel.userInfoState.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.getUserInfo()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            SimpleTopAppBar(navController = navController, title = "Settings")

            when (val state = userInfoState) {
                is Resource.Idle, is Resource.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }

                is Resource.Success -> {
                    state.data?.let { userInfo ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            // User profile section
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Profile image
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = TablerIcons.User,
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                // User info
                                Column {
                                    Text(
                                        text = "${userInfo.first_name} ${userInfo.last_name}",
                                        style = TextLG.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onBackground
                                    )

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = TablerIcons.Mail,
                                            contentDescription = "Email",
                                            modifier = Modifier.size(16.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = userInfo.email,
                                            style = TextBase,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Divider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp),
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "APP SETTINGS",
                                style = TextXS,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            // Dark Mode Toggle
                            DarkModeSettingsItem(
                                isDarkMode = isDarkMode,
                                onToggleTheme = onThemeChanged
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Settings options
                            SettingsItem(
                                title = "Change Password",
                                icon = TablerIcons.Lock,
                                onClick = {
                                    navController.navigate(Screen.ChangePassword.route)
                                }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Divider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp),
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Logout button with red color and icon
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextLink(
                                    text = "Log Out",
                                    onClick = { showLogoutDialog = true },
                                    color = MaterialTheme.colorScheme.error,
                                    startIcon = {
                                        Icon(
                                            imageVector = TablerIcons.Logout,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            // App version at the bottom
                            Text(
                                text = "Simple Note v1.0",
                                style = TextBase,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(bottom = 16.dp)
                            )
                        }
                    }
                }

                is Resource.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.message ?: "An error occurred",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        TextLink(
                            text = "Try again",
                            onClick = { viewModel.getUserInfo() }
                        )
                    }
                }
            }
        }

        // Logout Confirmation Dialog
        if (showLogoutDialog) {
            Dialog(onDismissRequest = { showLogoutDialog = false }) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Dialog Title
                        Text(
                            text = "Log Out",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Dialog Message
                        Text(
                            text = "Are you sure you want to log out from the application?",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Dialog Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Cancel Button
                            OutlinedButton(
                                onClick = { showLogoutDialog = false },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "Cancel")
                            }

                            // Yes Button
                            Button(
                                onClick = {
                                    showLogoutDialog = false
                                    viewModel.logout()
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(Screen.Profile.route) { inclusive = true }
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "Yes")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DarkModeSettingsItem(
    isDarkMode: Boolean,
    onToggleTheme: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = TablerIcons.Moon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = "Dark Mode",
            style = TextBase,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f)
        )

        Switch(
            checked = isDarkMode,
            onCheckedChange = onToggleTheme
        )
    }
}

@Composable
fun SettingsItem(
    title: String,
    onClick: () -> Unit,
    icon: ImageVector? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Optional start icon
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
        }

        Text(
            text = title,
            style = TextBase,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = TablerIcons.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}