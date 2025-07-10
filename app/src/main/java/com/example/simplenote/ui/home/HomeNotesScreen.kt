package com.example.simplenote.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.simplenote.R
import com.example.simplenote.ui.components.AddNoteButton
import com.example.simplenote.ui.components.NoteItem
import com.example.simplenote.ui.components.SearchTextField
import com.example.simplenote.ui.navigation.Screen
import com.example.simplenote.ui.theme.Text2XL
import com.example.simplenote.ui.theme.TextBase
import com.example.simplenote.viewmodel.NoteViewModel
import compose.icons.TablerIcons
import compose.icons.tablericons.Refresh

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeNotesScreen(
    navController: NavController,
    viewModel: NoteViewModel
) {
    var searchQuery by remember { mutableStateOf("") }
    val allNotes by viewModel.allNotes.collectAsState()

    // Filter notes based on search query
    val filteredNotes = remember(searchQuery, allNotes) {
        if (searchQuery.isBlank()) {
            allNotes
        } else {
            allNotes.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                        it.description.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    val shouldShowSearchBar = allNotes.isNotEmpty() || searchQuery.isNotBlank()

    Scaffold(
        topBar = {
            if (shouldShowSearchBar) {
                TopAppBar(
                    title = { Text("Notes", fontWeight = FontWeight.Bold) },
                    actions = {
                        IconButton(onClick = { viewModel.triggerSync() }) {
                            Icon(TablerIcons.Refresh, contentDescription = "Sync Notes")
                        }
                    },
//                    colors = TopAppBarDefaults.topAppBarColors(
//                        containerColor = MaterialTheme.colorScheme.background
//                    )
                )
            }
        },
        floatingActionButton = {
            AddNoteButton(onClick = { navController.navigate(Screen.NoteEdit.route) })
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            if (shouldShowSearchBar) {
                SearchTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = "Search notes...",
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            if (filteredNotes.isEmpty()) {
                if (searchQuery.isNotBlank()) {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        Text("No notes found for '$searchQuery'")
                    }
                } else {
                    HomeEmptyContent()
                }
            } else {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    contentPadding = PaddingValues(bottom = 80.dp),
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
}

// HomeEmptyContent composable remains the same
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