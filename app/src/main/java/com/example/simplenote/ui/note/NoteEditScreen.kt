package com.example.simplenote.ui.note

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.simplenote.util.Resource
import com.example.simplenote.viewmodel.NoteViewModel
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditScreen(
    navController: NavController,
    noteId: Int?,
    viewModel: NoteViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val colors = MaterialTheme.colorScheme

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val isEditMode = noteId != null

    val noteState by viewModel.noteState.collectAsState()
    val createState by viewModel.createNoteState.collectAsState()
    val updateState by viewModel.updateNoteState.collectAsState()
    val deleteState by viewModel.deleteNoteState.collectAsState()

    val isLoading = noteState is Resource.Loading ||
            createState is Resource.Loading ||
            updateState is Resource.Loading ||
            deleteState is Resource.Loading

    LaunchedEffect(Unit) {
        if (isEditMode) {
            noteId?.let { viewModel.getNoteById(it) }
        } else {
            viewModel.resetNoteState()
            viewModel.resetCreateNoteState()
            viewModel.resetUpdateNoteState()
            viewModel.resetDeleteNoteState()
        }
    }

    LaunchedEffect(noteState) {
        if (noteState is Resource.Success) {
            noteState.data?.let {
                title = it.title
                description = it.description
            }
        } else if (noteState is Resource.Error) {
            Toast.makeText(context, noteState.message ?: "Failed to load note", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(createState, updateState, deleteState) {
        val combinedState: Resource<out Any> = when {
            createState !is Resource.Idle -> createState
            updateState !is Resource.Idle -> updateState
            deleteState !is Resource.Idle -> deleteState
            else -> Resource.Idle()
        }

        when (combinedState) {
            is Resource.Success -> {
                val message = when {
                    createState is Resource.Success -> "Note created"
                    updateState is Resource.Success -> "Note updated"
                    deleteState is Resource.Success -> "Note deleted"
                    else -> "Success"
                }
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            }
            is Resource.Error -> {
                Toast.makeText(context, combinedState.message ?: "An error occurred", Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    val lastEdited = remember(noteState) {
        (noteState as? Resource.Success)?.data?.updatedAt?.format(
            DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm", Locale.getDefault())
        ) ?: "Not saved yet"
    }

    Scaffold(
        modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.surface),
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = colors.primary)
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            if (title.isBlank()) {
                                Toast.makeText(context, "Title cannot be empty", Toast.LENGTH_SHORT).show()
                                return@TextButton
                            }
                            if (isEditMode) {
                                noteId?.let { viewModel.updateNote(it, title, description) }
                            } else {
                                viewModel.createNote(title, description)
                            }
                        },
                        enabled = !isLoading
                    ) {
                        Text("Save", color = colors.primary)
                    }
                    if (isEditMode) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, "Delete", tint = colors.error)
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (isEditMode) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(48.dp).background(colors.surface),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Last edited: $lastEdited", fontSize = 12.sp, color = colors.onSurfaceVariant)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(colors.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            if (isLoading && noteState is Resource.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("Title", style = MaterialTheme.typography.titleLarge) },
                    textStyle = MaterialTheme.typography.titleLarge.copy(color = colors.onBackground),
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = colors.surfaceVariant,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Start writing your note here...") },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp, color = colors.onBackground),
                    modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 300.dp),
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
                        noteId?.let { viewModel.deleteNote(it) }
                    }) {
                        Text("Delete", color = colors.error)
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