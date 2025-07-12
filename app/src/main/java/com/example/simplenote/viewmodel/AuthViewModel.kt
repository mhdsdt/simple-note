package com.example.simplenote.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplenote.api.models.UserInfoResponse
import com.example.simplenote.data.repository.AuthRepository
import com.example.simplenote.data.repository.NoteRepository
import com.example.simplenote.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _registerState = MutableStateFlow<Resource<Boolean>>(Resource.Idle())
    val registerState: StateFlow<Resource<Boolean>> = _registerState

    private val _loginState = MutableStateFlow<Resource<Boolean>>(Resource.Idle())
    val loginState: StateFlow<Resource<Boolean>> = _loginState

    private val _userInfoState = MutableStateFlow<Resource<UserInfoResponse>>(Resource.Idle())
    val userInfoState: StateFlow<Resource<UserInfoResponse>> = _userInfoState

    private val _changePasswordState = MutableStateFlow<Resource<Boolean>>(Resource.Idle())
    val changePasswordState: StateFlow<Resource<Boolean>> = _changePasswordState

    fun clearCache() {
        viewModelScope.launch {
            noteRepository.clearLocalNotes()
        }
    }

    fun register(
        username: String,
        password: String,
        email: String,
        firstName: String,
        lastName: String
    ) {
        viewModelScope.launch {
            authRepository.register(username, password, email, firstName, lastName)
                .onEach { result ->
                    _registerState.value = result
                }.launchIn(viewModelScope)
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            authRepository.login(username, password).onEach { result ->
                _loginState.value = result
                if (result is Resource.Success) {
                    getUserInfo()
                }
            }.launchIn(viewModelScope)
        }
    }

    fun getUserInfo() {
        viewModelScope.launch {
            authRepository.getUserInfo().onEach { result ->
                _userInfoState.value = result
            }.launchIn(viewModelScope)
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            noteRepository.clearLocalNotes()
            resetRegisterState()
            resetLoginState()
            resetUserInfoState()
        }
    }

    private fun resetRegisterState() {
        _registerState.value = Resource.Idle()
    }

    private fun resetLoginState() {
        _loginState.value = Resource.Idle()
    }

    private fun resetUserInfoState() {
        _userInfoState.value = Resource.Idle()
    }

    fun changePassword(oldPassword: String, newPassword: String) {
        viewModelScope.launch {
            authRepository.changePassword(oldPassword, newPassword)
                .onEach { result ->
                    _changePasswordState.value = result
                }.launchIn(viewModelScope)
        }
    }
}