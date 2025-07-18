package com.example.simplenote.ui.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.simplenote.ui.components.PasswordTextField
import com.example.simplenote.ui.components.PrimaryButton
import com.example.simplenote.ui.components.SimpleTopAppBar
import com.example.simplenote.util.Resource
import com.example.simplenote.viewmodel.AuthViewModel

@Composable
fun ChangePasswordScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val changePasswordState by authViewModel.changePasswordState.collectAsState()

    LaunchedEffect(changePasswordState) {
        if (changePasswordState is Resource.Success) {
            Toast.makeText(
                context,
                "Password changed successfully",
                Toast.LENGTH_SHORT
            ).show()
            navController.popBackStack()
        }
        if (changePasswordState is Resource.Error) {
            Toast.makeText(
                context,
                changePasswordState.message ?: "Failed to change password",
                Toast.LENGTH_SHORT
            ).show()
            errorMessage = (changePasswordState as Resource.Error).message
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            SimpleTopAppBar(navController = navController, title = "Change Password")
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Please input your current password first",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                PasswordTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = "Current Password",
                    placeholder = "Enter your current password",
                    imeAction = ImeAction.Next
                )
                Spacer(modifier = Modifier.height(24.dp))
                androidx.compose.material3.Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Now, create your new password",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
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
                Spacer(modifier = Modifier.weight(1f))
                PrimaryButton(
                    text = "Submit New Password",
                    isLoading = changePasswordState is Resource.Loading,
                    onClick = {
                        if (currentPassword.isBlank() ||
                            newPassword.isBlank() ||
                            confirmPassword.isBlank()
                        ) {
                            errorMessage = "Please fill all fields"
                            return@PrimaryButton
                        }
                        if (newPassword != confirmPassword) {
                            errorMessage = "New passwords do not match"
                            return@PrimaryButton
                        }
                        authViewModel.changePassword(currentPassword, newPassword)
                    }
                )
            }
        }
    }
}