package com.example.simplenote.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.simplenote.R
import com.example.simplenote.ui.components.AddNoteButton
import com.example.simplenote.ui.components.NoteItem
import com.example.simplenote.ui.navigation.Screen
import com.example.simplenote.ui.theme.Text2XL
import com.example.simplenote.ui.theme.TextBase
import com.example.simplenote.viewmodel.NoteViewModel
import compose.icons.TablerIcons
import compose.icons.tablericons.Home
import compose.icons.tablericons.Refresh
import compose.icons.tablericons.Search
import compose.icons.tablericons.Settings

@Composable
fun HomeNotesScreen(
    navController: NavController,
    viewModel: NoteViewModel
) {
    var searchQuery by remember { mutableStateOf("") }
    // MODIFIED: allNotes is now a nullable list (List<NoteResponse>?)
    val allNotes by viewModel.allNotes.collectAsState()

    val filteredNotes = remember(searchQuery, allNotes) {
        if (allNotes == null) {
            emptyList()
        } else if (searchQuery.isBlank()) {
            allNotes!!
        } else {
            allNotes!!.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                        it.description.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // MODIFIED: Main content area now handles the loading state
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Case 1: Show a loading indicator while the initial list is null
            if (allNotes == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // Once loaded, show the search bar, title, and content
                val shouldShowContent = allNotes!!.isNotEmpty() || searchQuery.isNotBlank()
                if (shouldShowContent) {
                    // Search Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = TablerIcons.Search,
                            contentDescription = "Search Icon",
                            modifier = Modifier.size(28.dp),
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = {
                                Text(
                                    "Search...",
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                cursorColor = MaterialTheme.colorScheme.primary,
                                focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                            singleLine = true,
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = "Notes",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.align(Alignment.Center),
                            textAlign = TextAlign.Center
                        )

                        // This IconButton MUST be inside the Box to use Modifier.align()
                        IconButton(
                            onClick = { viewModel.triggerSync() },
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) {
                            Icon(TablerIcons.Refresh, contentDescription = "Sync Notes")
                        }
                    }
                }

                // Case 2: Data is loaded, but the list is empty
                if (filteredNotes.isEmpty()) {
                    if (searchQuery.isNotBlank()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No notes found for '$searchQuery'")
                        }
                    } else {
                        HomeEmptyContent(onSyncClick = { viewModel.triggerSync() })
                    }
                } else {
                    // Case 3: Data is loaded and we have notes to show
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(2),
                        contentPadding = PaddingValues(bottom = 90.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalItemSpacing = 8.dp
                    ) {
                        items(filteredNotes, key = { it.id }) { note ->
                            NoteItem(
                                note = note,
                                onClick = { navController.navigate(Screen.NoteEdit.route + "?noteId=${note.id}") }
                            )
                        }
                    }
                }
            }
        }

        // Bottom Navigation remains the same, but is only visible after loading
        if (allNotes != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                NavigationBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp),
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    NavigationBarItem(
                        selected = true,
                        onClick = { /* Already on Home */ },
                        icon = {
                            Icon(
                                TablerIcons.Home,
                                "Home",
                                Modifier.size(32.dp),
                                MaterialTheme.colorScheme.primary
                            )
                        },
                        label = {
                            Text(
                                "Home",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        },
                        alwaysShowLabel = true,
                        colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.surface)
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { navController.navigate(Screen.Profile.route) },
                        icon = {
                            Icon(
                                TablerIcons.Settings,
                                "Settings",
                                Modifier.size(32.dp),
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        },
                        label = {
                            Text(
                                "Settings",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        },
                        alwaysShowLabel = true,
                        colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.surface)
                    )
                }
                Box(
                    modifier = Modifier.offset(y = (-45).dp)
                ) {
                    AddNoteButton(
                        onClick = { navController.navigate(Screen.NoteEdit.route) }
                    )
                }
            }
        }
    }
}

@Composable
fun HomeEmptyContent(onSyncClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.home),
            contentDescription = "Home illustration",
            modifier = Modifier.size(280.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Start Your Journey",
            style = Text2XL.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Every big step starts with a small step. Note your first idea and start your journey!",
            style = TextBase,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(48.dp))

        IconButton(onClick = onSyncClick) {
            Icon(
                imageVector = TablerIcons.Refresh,
                contentDescription = "Sync Notes",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}