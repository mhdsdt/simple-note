package com.example.simplenote.ui.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.simplenote.ui.components.PasswordTextField
import com.example.simplenote.ui.components.PrimaryButton
import com.example.simplenote.ui.components.SimpleTextField
import com.example.simplenote.ui.components.TextLink
import com.example.simplenote.ui.navigation.Screen
import com.example.simplenote.ui.theme.Text2XL
import com.example.simplenote.ui.theme.TextBase
import com.example.simplenote.ui.theme.TextSM
import com.example.simplenote.util.Resource
import com.example.simplenote.viewmodel.AuthViewModel
import compose.icons.TablerIcons
import compose.icons.tablericons.ChevronLeft

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val registerState by viewModel.registerState.collectAsState()

    // Initialize isLoading based on the current state
    LaunchedEffect(registerState) {
        isLoading = registerState is Resource.Loading

        if (registerState is Resource.Success) {
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Register.route) { inclusive = true }
            }
        } else if (registerState is Resource.Error) {
            errorMessage = registerState.message
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(bottom = 12.dp)
                .offset(x = ((-16).dp))
        ) {
            TextLink(
                text = "Back to Login",
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
            text = "Register",
            style = Text2XL.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "And start taking notes",
            style = TextBase,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        SimpleTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = "First Name",
            placeholder = "Example: Taha",
            imeAction = ImeAction.Next,
        )

        Spacer(modifier = Modifier.height(16.dp))

        SimpleTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = "Last Name",
            placeholder = "Example: Hamifar",
            imeAction = ImeAction.Next,
        )

        Spacer(modifier = Modifier.height(16.dp))

        SimpleTextField(
            value = username,
            onValueChange = { username = it },
            label = "Username",
            placeholder = "Example: @HamifarTaha",
            imeAction = ImeAction.Next,
        )

        Spacer(modifier = Modifier.height(16.dp))

        SimpleTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email Address",
            placeholder = "Example: hamifar.taha@gmail.com",
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next,
        )

        Spacer(modifier = Modifier.height(16.dp))

        PasswordTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            placeholder = "********",
            imeAction = ImeAction.Next,
        )

        Spacer(modifier = Modifier.height(16.dp))

        PasswordTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Retype Password",
            placeholder = "********",
            imeAction = ImeAction.Done,
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        PrimaryButton(
            text = "Register",
            onClick = {
                if (firstName.isBlank() || lastName.isBlank() || username.isBlank() ||
                    email.isBlank() || password.isBlank() || confirmPassword.isBlank()
                ) {
                    errorMessage = "Please fill all fields"
                    return@PrimaryButton
                }

                if (password != confirmPassword) {
                    errorMessage = "Passwords do not match"
                    return@PrimaryButton
                }

                errorMessage = null
                viewModel.register(username, password, email, firstName, lastName)
            },
            isLoading = isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Divider(
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
            )

            Text(
                text = "Or",
                style = TextSM,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Divider(
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextLink(
            text = "Already have an account? Login here",
            onClick = { navController.navigate(Screen.Login.route) },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
