package com.example.simplenote.ui.note

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.simplenote.ui.components.SimpleTopAppBar
import com.example.simplenote.viewmodel.NoteViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun NoteEditScreen(
    navController: NavController,
    noteId: Int?,
    viewModel: NoteViewModel
) {
    val context = LocalContext.current
    val isEditMode = noteId != null

    // MODIFIED: Observe the new selectedNote StateFlow from the ViewModel
    val noteState by viewModel.selectedNote.collectAsState()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // This effect now triggers the data load
    LaunchedEffect(key1 = noteId) {
        if (isEditMode) {
            viewModel.getNoteById(noteId!!)
        } else {
            // If creating a new note, ensure the state is clear
            viewModel.clearSelectedNote()
        }
    }

    // This effect now populates the local UI state once the data arrives
    LaunchedEffect(noteState) {
        if (noteState != null) {
            title = noteState!!.title
            description = noteState!!.description
        } else {
            // If state becomes null (e.g., navigating to create mode), clear fields
            title = ""
            description = ""
        }
    }

    val saveNoteAndExit = {
        val originalTitle = noteState?.title ?: ""
        val originalDescription = noteState?.description ?: ""
        val hasChanges = title != originalTitle || description != originalDescription

        if (hasChanges) {
            if (title.isBlank() || description.isBlank()) {
                Toast.makeText(context, "Title and description cannot be empty", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (isEditMode) {
                    viewModel.updateNote(noteId!!, title, description)
                } else {
                    viewModel.createNote(title, description)
                }
                navController.popBackStack()
            }
        } else {
            // Case 2: No changes were made, so just exit silently.
            navController.popBackStack()
        }
    }

    BackHandler { saveNoteAndExit() }

    // Clear the selected note when the screen is left
    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearSelectedNote()
        }
    }

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                navController = navController,
                title = if (isEditMode) "Edit Note" else "Create Note",
                onNavigateBack = { saveNoteAndExit() }
            )
        },
        bottomBar = {
            // Only show bottom bar in edit mode AND when the note data is available
            if (isEditMode && noteState != null) {
                val lastEdited = noteState?.updatedAt?.format(
                    DateTimeFormatter.ofPattern("HH:mm, dd MMM", Locale.getDefault())
                ) ?: ""
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(start = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (lastEdited.isNotEmpty()) {
                        Text(
                            text = "Edited: $lastEdited",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        // Show loading indicator only in edit mode while noteState is null
        if (isEditMode && noteState == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            // Show content for new notes immediately, or for edited notes once loaded
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("Title", style = MaterialTheme.typography.titleLarge) },
                    textStyle = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.onBackground),
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Start writing your note here...") },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        lineHeight = 24.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 300.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Note?") },
                text = { Text("Are you sure you want to permanently delete this note?") },
                confirmButton = {
                    TextButton(onClick = {
                        showDeleteDialog = false
                        viewModel.deleteNote(noteId!!)
                        Toast.makeText(context, "Note Deleted", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}