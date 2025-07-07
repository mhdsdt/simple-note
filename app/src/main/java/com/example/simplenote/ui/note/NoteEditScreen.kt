package com.example.simplenote.ui.note

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.simplenote.util.Resource
import com.example.simplenote.viewmodel.NoteViewModel
import java.text.SimpleDateFormat
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

    //── 1) UI state
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val isEditMode = noteId != null

    //── 2) ViewModel flows
    val noteState by viewModel.noteState.collectAsState()
    val createState by viewModel.createNoteState.collectAsState()
    val updateState by viewModel.updateNoteState.collectAsState()
    val deleteState by viewModel.deleteNoteState.collectAsState()

    //── 3) Trigger load once
    LaunchedEffect(noteId) {
        noteId?.let { viewModel.getNoteById(it) }
    }

    //── 4) Populate form on load
    LaunchedEffect(noteState) {
        when (noteState) {
            is Resource.Loading -> isLoading = true
            is Resource.Success -> {
                isLoading = false
                noteState.data?.let {
                    title = it.title
                    description = it.description
                }
            }

            is Resource.Error -> {
                isLoading = false
                errorMessage = noteState.message
                Toast
                    .makeText(context, errorMessage ?: "Failed to load note", Toast.LENGTH_SHORT)
                    .show()
            }

            else -> { /* idle */
            }
        }
    }

    //── 5) Handle create/update
    LaunchedEffect(createState, updateState) {
        when {
            createState is Resource.Loading || updateState is Resource.Loading -> {
                isLoading = true
            }

            createState is Resource.Success || updateState is Resource.Success -> {
                isLoading = false
                Toast
                    .makeText(
                        context,
                        if (isEditMode) "Note updated successfully" else "Note created successfully",
                        Toast.LENGTH_SHORT
                    )
                    .show()
                navController.popBackStack()
            }

            createState is Resource.Error -> {
                isLoading = false
                errorMessage = (createState as Resource.Error).message
                Toast.makeText(context, errorMessage ?: "Failed to create", Toast.LENGTH_SHORT)
                    .show()
            }

            updateState is Resource.Error -> {
                isLoading = false
                errorMessage = (updateState as Resource.Error).message
                Toast.makeText(context, errorMessage ?: "Failed to update", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    //── 6) Handle delete
    LaunchedEffect(deleteState) {
        when (deleteState) {
            is Resource.Loading -> isLoading = true
            is Resource.Success -> {
                isLoading = false
                Toast.makeText(context, "Note deleted", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            }

            is Resource.Error -> {
                isLoading = false
                val msg = (deleteState as Resource.Error).message
                Toast.makeText(context, msg ?: "Failed to delete", Toast.LENGTH_SHORT).show()
            }

            else -> { /* idle */
            }
        }
    }

    val timeFormatter = remember {
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH.mm", Locale.getDefault())
    }

    val lastEdited = remember(noteState) {
        (noteState as? Resource.Success)
            ?.data
            ?.updatedAt
            // format the LocalDateTime
            ?.format(timeFormatter)
        // fallback if we don’t have an updatedAt yet
            ?: ""
    }

    Scaffold(
        modifier = Modifier.height(56.dp)
            // draw under the status‐bar
            .windowInsetsPadding(WindowInsets.statusBars),
        topBar = {
            TopAppBar(
                modifier = Modifier.height(52.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.surface,
                    titleContentColor = colors.primary,
                ),
                title = {},
                navigationIcon = {
                    // Make a Box (or IconButton) that fills the app‐bar's height:
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .clickable { navController.popBackStack() }
                            .padding(start = 16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector    = Icons.Filled.ArrowBack,
                                tint           = colors.primary,
                                modifier = Modifier.size(16.dp),
                                contentDescription = "Back"
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text  = "Back",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.primary
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            Column {
                Divider(color = colors.surfaceVariant, thickness = 1.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(colors.surface),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Last edited on $lastEdited",
                        fontSize = 12.sp,
                        color = colors.onSurfaceVariant,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                    Spacer(Modifier.weight(1f))
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier
                            .size(40.dp)
                            .padding(end = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete",
                            tint = colors.error,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(colors.background)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = colors.primary
                )
            } else {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    // Title row
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Lightbulb,
                            contentDescription = null,
                            tint = colors.tertiary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = title,
                            fontSize = 24.sp,
                            color = colors.onBackground,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    Spacer(Modifier.height(16.dp))

                    // Body paragraphs
                    description.split("\n\n").forEachIndexed { idx, para ->
                        Text(
                            text = para,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            color = colors.onBackground.copy(alpha = 0.8f),
                        )
                        if (idx < description.split("\n\n").lastIndex) {
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                    Spacer(Modifier.height(16.dp))

                    // inset divider
                    Divider(
                        color = colors.surfaceVariant,
                        thickness = 1.dp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            // error at bottom
            errorMessage?.let { msg ->
                Text(
                    text = msg,
                    color = colors.error,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                )
            }

            // Compose AlertDialog for “Are you sure?”
            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Are you sure?") },
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
                            Text("Cancel", color = colors.primary)
                        }
                    },
                    containerColor = colors.surface,
                    iconContentColor = colors.primary,
                    titleContentColor = colors.onSurface,
                    textContentColor = colors.onSurface
                )
            }
        }
    }
}
