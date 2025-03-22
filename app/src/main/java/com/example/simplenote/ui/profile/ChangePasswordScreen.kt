package com.example.simplenote.ui.profile

import androidx.compose.foundation.background
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
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.simplenote.ui.components.PasswordTextField
import com.example.simplenote.ui.components.PrimaryButton
import com.example.simplenote.ui.components.TextLink
import com.example.simplenote.ui.theme.TextBase
import compose.icons.TablerIcons
import compose.icons.tablericons.ChevronLeft

@Composable
fun ChangePasswordScreen(
    navController: NavController
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Top app bar with back button and centered title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Back button aligned to the left
                Box(modifier = Modifier.weight(0.8f), contentAlignment = Alignment.CenterStart) {
                    TextLink(
                        text = "Back",
                        onClick = { navController.popBackStack() },
                        color = MaterialTheme.colorScheme.primary,
                        startIcon = {
                            Icon(
                                imageVector = TablerIcons.ChevronLeft,
                                contentDescription = "Back",
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                    )
                }

                Text(
                    text = "Change Password",
                    style = TextBase.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1.4f),
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )

                // Empty box with same weight to ensure centering
                Box(modifier = Modifier.weight(0.8f))
            }

            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Instruction text with primary color
                Text(
                    text = "Please input your current password first",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Current Password
                PasswordTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = "Current Password",
                    placeholder = "Enter your current password",
                    imeAction = ImeAction.Next
                )

                Spacer(modifier = Modifier.height(24.dp))

                Divider(
                    modifier = Modifier.fillMaxWidth().height(1.dp),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Now, create your new password",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // New Password
                PasswordTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = "New Password",
                    placeholder = "Enter your new password",
                    imeAction = ImeAction.Next
                )
                Text(
                    text = "Password should contain a-z, A-Z, 0-9",
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Retype New Password
                PasswordTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = "Retype New Password",
                    placeholder = "Confirm your new password",
                    imeAction = ImeAction.Done
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                // Push the button to the bottom
                Spacer(modifier = Modifier.weight(1f))

                // Submit button with arrow icon
                PrimaryButton(
                    text = "Submit New Password",
                    onClick = {
                        if (currentPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
                            errorMessage = "Please fill all fields"
                            return@PrimaryButton
                        }

                        if (newPassword != confirmPassword) {
                            errorMessage = "New passwords do not match"
                            return@PrimaryButton
                        }

                        // TODO: Implement change password endpoint in backend
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
