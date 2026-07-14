package com.aistudio.dieselstationsms.kxmpzq.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aistudio.dieselstationsms.kxmpzq.data.model.Role
import com.aistudio.dieselstationsms.kxmpzq.data.model.User
import com.aistudio.dieselstationsms.kxmpzq.data.repository.StationRepository
import com.aistudio.dieselstationsms.kxmpzq.ui.state.ActionState
import com.aistudio.dieselstationsms.kxmpzq.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UsersViewModel(
    private val repository: StationRepository
) : ViewModel() {

    private val _usersState = MutableStateFlow<UiState<List<User>>>(UiState.Loading)
    val usersState: StateFlow<UiState<List<User>>> = _usersState.asStateFlow()

    private val _rolesState = MutableStateFlow<UiState<List<Role>>>(UiState.Loading)
    val rolesState: StateFlow<UiState<List<Role>>> = _rolesState.asStateFlow()

    private val _actionState = MutableStateFlow<ActionState>(ActionState.Idle)
    val actionState: StateFlow<ActionState> = _actionState.asStateFlow()

    init {
        loadUsers()
        loadRoles()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _usersState.value = UiState.Loading
            repository.getUsers().fold(
                onSuccess = { _usersState.value = UiState.Success(it) },
                onFailure = { _usersState.value = UiState.Error(it.message ?: "فشل تحميل المستخدمين") }
            )
        }
    }

    fun loadRoles() {
        viewModelScope.launch {
            _rolesState.value = UiState.Loading
            repository.getRoles().fold(
                onSuccess = { _rolesState.value = UiState.Success(it) },
                onFailure = { _rolesState.value = UiState.Error(it.message ?: "فشل تحميل الأدوار") }
            )
        }
    }

    fun deleteUser(userId: Int) {
        viewModelScope.launch {
            _actionState.value = ActionState.Loading
            repository.deleteUser(userId).fold(
                onSuccess = {
                    _actionState.value = ActionState.Success("تم حذف المستخدم")
                    loadUsers()
                },
                onFailure = { _actionState.value = ActionState.Error(it.message ?: "فشل الحذف") }
            )
        }
    }

    fun deleteRole(roleId: Int) {
        viewModelScope.launch {
            _actionState.value = ActionState.Loading
            repository.deleteRole(roleId).fold(
                onSuccess = {
                    _actionState.value = ActionState.Success("تم حذف الدور")
                    loadRoles()
                },
                onFailure = { _actionState.value = ActionState.Error(it.message ?: "فشل الحذف") }
            )
        }
    }

    fun resetActionState() { _actionState.value = ActionState.Idle }
}