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
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
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
import com.example.simplenote.ui.components.TextLink
import com.example.simplenote.ui.navigation.Screen
import com.example.simplenote.ui.theme.Text2XL
import com.example.simplenote.ui.theme.TextBase
import com.example.simplenote.util.Resource
import com.example.simplenote.viewmodel.NoteViewModel
import compose.icons.TablerIcons
import compose.icons.tablericons.Home
import compose.icons.tablericons.Search
import compose.icons.tablericons.Settings
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

@Composable
fun HomeNotesScreen(
    navController: NavController,
    viewModel: NoteViewModel
) {
    var searchQuery by remember { mutableStateOf("") }

    val paginatedNotes by viewModel.paginatedNotes.collectAsState()
    val screenState by viewModel.screenState.collectAsState()
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()
    val searchState by viewModel.searchState.collectAsState()
    val gridState = rememberLazyStaggeredGridState()

    // Pagination observer
    LaunchedEffect(gridState) {
        snapshotFlow { gridState.layoutInfo }
            .map { it.visibleItemsInfo.lastOrNull()?.index }
            .distinctUntilChanged()
            .filter { it != null && it >= paginatedNotes.size - 3 } // threshold of 3 items from the end
            .collect {
                viewModel.loadMoreNotes()
            }
    }

    // Search observer
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotBlank()) {
            viewModel.searchNotes(searchQuery)
        } else {
            viewModel.clearSearch()
        }
    }

    val shouldShowSearchBar = searchQuery.isNotBlank() || paginatedNotes.isNotEmpty()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            if (shouldShowSearchBar) {
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
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search...") },
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
            }

            if (searchQuery.isNotBlank()) {
                // Search Content
                when (val state = searchState) {
                    is Resource.Loading -> Box(
                        Modifier.fillMaxSize(),
                        Alignment.Center
                    ) { CircularProgressIndicator() }

                    is Resource.Success -> {
                        if (state.data?.results.isNullOrEmpty()) {
                            Box(
                                Modifier.fillMaxSize(),
                                Alignment.Center
                            ) { Text("No notes found for '$searchQuery'") }
                        } else {
                            LazyVerticalStaggeredGrid(
                                columns = StaggeredGridCells.Fixed(2),
                                contentPadding = PaddingValues(bottom = 80.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalItemSpacing = 8.dp
                            ) {
                                items(state.data?.results ?: emptyList(), key = { it.id }) { note ->
                                    NoteItem(
                                        note = note,
                                        onClick = { navController.navigate(Screen.NoteEdit.route + "?noteId=${note.id}") })
                                }
                            }
                        }
                    }

                    is Resource.Error -> Box(
                        Modifier.fillMaxSize(),
                        Alignment.Center
                    ) { Text(state.message ?: "Search failed") }

                    else -> {}
                }
            } else {
                // Paginated Content
                when (screenState) {
                    is Resource.Loading -> Box(
                        Modifier.fillMaxSize(),
                        Alignment.Center
                    ) { CircularProgressIndicator() }

                    is Resource.Error -> {
                        Column(
                            Modifier.fillMaxSize(),
                            Arrangement.Center,
                            Alignment.CenterHorizontally
                        ) {
                            Text((screenState as Resource.Error).message ?: "An error occurred")
                            Spacer(Modifier.height(16.dp))
                            TextLink(text = "Try again", onClick = { viewModel.refreshNotes() })
                        }
                    }

                    is Resource.Success, is Resource.Idle -> {
                        if (paginatedNotes.isEmpty()) {
                            HomeEmptyContent()
                        } else {
                            Text(
                                text = "Notes",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp)
                            )
                            LazyVerticalStaggeredGrid(
                                state = gridState,
                                columns = StaggeredGridCells.Fixed(2),
                                contentPadding = PaddingValues(bottom = 90.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalItemSpacing = 8.dp
                            ) {
                                items(paginatedNotes, key = { it.id }) { note ->
                                    NoteItem(
                                        note = note,
                                        onClick = { navController.navigate(Screen.NoteEdit.route + "?noteId=${note.id}") })
                                }
                                if (isLoadingMore) {
                                    item {
                                        Box(
                                            Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            Alignment.Center
                                        ) {
                                            CircularProgressIndicator(strokeWidth = 3.dp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom Navigation
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
                    onClick = { },
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


@Composable
fun HomeEmptyContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 100.dp, start = 24.dp, end = 24.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.home),
                contentDescription = "Home illustration",
                modifier = Modifier.size(280.dp)
            )
        }
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
    }
}