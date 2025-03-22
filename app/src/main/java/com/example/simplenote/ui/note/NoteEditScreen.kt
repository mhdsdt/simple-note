package com.example.simplenote.ui.note

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.simplenote.ui.components.BackAppBar
import com.example.simplenote.ui.components.DangerButton
import com.example.simplenote.ui.components.PrimaryButton
import com.example.simplenote.ui.components.SimpleTextField
import com.example.simplenote.ui.theme.PrimaryBackground
import com.example.simplenote.ui.theme.PrimaryBase
import com.example.simplenote.util.Resource
import com.example.simplenote.viewmodel.NoteViewModel
import compose.icons.TablerIcons
import compose.icons.tablericons.Trash

@Composable
fun NoteEditScreen(
    navController: NavController,
    noteId: Int?,
    viewModel: NoteViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val isEditMode by remember { mutableStateOf(noteId != null) }

    val noteState by viewModel.noteState.collectAsState()
    val createNoteState by viewModel.createNoteState.collectAsState()
    val updateNoteState by viewModel.updateNoteState.collectAsState()
    val deleteNoteState by viewModel.deleteNoteState.collectAsState()

    LaunchedEffect(Unit) {
        if (noteId != null) {
            viewModel.getNoteById(noteId)
        }
    }

    LaunchedEffect(noteState) {
        when (noteState) {
            is Resource.Idle -> {
                isLoading = false
            }
            is Resource.Loading -> {
                isLoading = true
            }
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
            }
        }
    }

    LaunchedEffect(createNoteState) {
        when (createNoteState) {
            is Resource.Idle -> {
                isLoading = false
            }
            is Resource.Loading -> {
                isLoading = false
            }
            is Resource.Success -> {
                isLoading = false
                navController.popBackStack()
            }
            is Resource.Error -> {
                isLoading = false
                errorMessage = createNoteState.message
            }
        }
    }

    LaunchedEffect(updateNoteState) {
        when (updateNoteState) {
            is Resource.Idle -> {
                isLoading = false
            }
            is Resource.Loading -> {
                isLoading = false
            }
            is Resource.Success -> {
                isLoading = false
                navController.popBackStack()
            }
            is Resource.Error -> {
                isLoading = false
                errorMessage = updateNoteState.message
            }
        }
    }

    LaunchedEffect(deleteNoteState) {
        when (deleteNoteState) {
            is Resource.Idle -> {
                isLoading = false
            }
            is Resource.Loading -> {
                isLoading = false
            }
            is Resource.Success -> {
                isLoading = false
                navController.popBackStack()
            }
            is Resource.Error -> {
                isLoading = false
                errorMessage = deleteNoteState.message
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp)
        ) {
            BackAppBar(
                title = if (isEditMode) "Edit Note" else "Create Note",
                navController = navController
            )

            if (isLoading && isEditMode) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryBase)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    SimpleTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = "Title",
                        placeholder = "Enter note title",
                        imeAction = ImeAction.Next
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SimpleTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = "Description",
                        placeholder = "Enter note description",
                        imeAction = ImeAction.Done,
                        singleLine = false,
                        maxLines = 10,
                        modifier = Modifier.weight(1f)
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage!!,
                            color = androidx.compose.ui.graphics.Color.Red,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    PrimaryButton(
                        text = if (isEditMode) "Update Note" else "Save Note",
                        onClick = {
                            if (title.isBlank() || description.isBlank()) {
                                errorMessage = "Please fill all fields"
                                return@PrimaryButton
                            }

                            isLoading = true
                            errorMessage = null

                            if (isEditMode && noteId != null) {
                                viewModel.updateNote(noteId, title, description)
                            } else {
                                viewModel.createNote(title, description)
                            }
                        },
                        enabled = !isLoading
                    )

                    if (isEditMode) {
                        Spacer(modifier = Modifier.height(16.dp))

                        DangerButton(
                            text = "Delete Note",
                            onClick = {
                                noteId?.let {
                                    isLoading = true
                                    errorMessage = null
                                    viewModel.deleteNote(it)
                                }
                            },
                            icon = TablerIcons.Trash
                        )
                    }
                }
            }
        }
    }
}
