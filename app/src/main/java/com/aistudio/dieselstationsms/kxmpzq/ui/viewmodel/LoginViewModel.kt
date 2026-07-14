package com.aistudio.dieselstationsms.kxmpzq.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aistudio.dieselstationsms.kxmpzq.data.model.AuthResult
import com.aistudio.dieselstationsms.kxmpzq.data.repository.StationRepository
import com.aistudio.dieselstationsms.kxmpzq.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ═══════════════════════════════════════════════════════════════
 * LoginViewModel — المصادقة وتسجيل الدخول
 * ═══════════════════════════════════════════════════════════════
 */
class LoginViewModel(
    private val repository: StationRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<UiState<AuthResult>>(UiState.Idle)
    val authState: StateFlow<UiState<AuthResult>> = _authState.asStateFlow()

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    fun onUsernameChange(value: String) { _username.value = value }
    fun onPasswordChange(value: String) { _password.value = value }

    fun login() {
        viewModelScope.launch {
            _authState.value = UiState.Loading
            val result = repository.login(_username.value.trim(), _password.value.trim())
            if (result.success) {
                _authState.value = UiState.Success(result)
            } else {
                _authState.value = UiState.Error(result.error ?: "فشل تسجيل الدخول")
            }
        }
    }

    fun resetState() { _authState.value = UiState.Idle }
}