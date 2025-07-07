package com.example.simplenote.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.simplenote.ui.theme.NeutralBlack
import com.example.simplenote.ui.theme.PrimaryBase
import com.example.simplenote.ui.theme.TextBase
import compose.icons.TablerIcons
import compose.icons.tablericons.ArrowLeft

@Composable
fun BackAppBar(
    title: String,
    navController: NavController,
    onBackClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { onBackClick?.invoke() ?: navController.popBackStack() }
        ) {
            Icon(
                imageVector = TablerIcons.ArrowLeft,
                contentDescription = "Back",
                tint = PrimaryBase
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = title,
            style = TextBase.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )
    }
}