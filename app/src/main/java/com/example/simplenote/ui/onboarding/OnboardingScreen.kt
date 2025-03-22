package com.example.simplenote.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.simplenote.R
import com.example.simplenote.ui.components.SecondaryButton
import com.example.simplenote.ui.navigation.Screen
import com.example.simplenote.ui.theme.NeutralWhite
import com.example.simplenote.ui.theme.PrimaryBase
import com.example.simplenote.ui.theme.TextLG
import com.example.simplenote.viewmodel.SessionViewModel

@Composable
fun OnboardingScreen(
    navController: NavController,
    sessionViewModel: SessionViewModel = hiltViewModel()
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryBase)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp, 72.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.onboarding),
                    contentDescription = "Onboarding illustration",
                    modifier = Modifier.size(280.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Jot Down anything you want to achieve, today or in the future",
                style = TextLG.copy(fontWeight = FontWeight.Bold),
                color = NeutralWhite,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.weight(1f))

            SecondaryButton(
                text = "Let's Get Started",
                onClick = {
                    sessionViewModel.completeOnboarding()
                    navController.navigate(Screen.Login.route)
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
