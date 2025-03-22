package com.example.simplenote.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplenote.api.models.UserInfoResponse
import com.example.simplenote.data.repository.AuthRepository
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
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _registerState = MutableStateFlow<Resource<Boolean>>(Resource.Idle())
    val registerState: StateFlow<Resource<Boolean>> = _registerState

    private val _loginState = MutableStateFlow<Resource<Boolean>>(Resource.Idle())
    val loginState: StateFlow<Resource<Boolean>> = _loginState

    private val _userInfoState = MutableStateFlow<Resource<UserInfoResponse>>(Resource.Idle())
    val userInfoState: StateFlow<Resource<UserInfoResponse>> = _userInfoState

    fun register(username: String, password: String, email: String, firstName: String, lastName: String) {
        viewModelScope.launch {
            authRepository.register(username, password, email, firstName, lastName).onEach { result ->
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
            resetRegisterState()
            resetLoginState()
            resetUserInfoState()
        }
    }

    fun isLoggedIn(): Boolean {
        return authRepository.isLoggedIn()
    }

    fun resetRegisterState() {
        _registerState.value = Resource.Idle()
    }

    fun resetLoginState() {
        _loginState.value = Resource.Idle()
    }

    fun resetUserInfoState() {
        _userInfoState.value = Resource.Idle()
    }
}