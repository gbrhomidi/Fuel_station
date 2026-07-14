package com.aistudio.dieselstationsms.kxmpzq.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aistudio.dieselstationsms.kxmpzq.data.model.Employee
import com.aistudio.dieselstationsms.kxmpzq.data.repository.StationRepository
import com.aistudio.dieselstationsms.kxmpzq.ui.state.ActionState
import com.aistudio.dieselstationsms.kxmpzq.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EmployeesViewModel(
    private val repository: StationRepository
) : ViewModel() {

    private val _employeesState = MutableStateFlow<UiState<List<Employee>>>(UiState.Loading)
    val employeesState: StateFlow<UiState<List<Employee>>> = _employeesState.asStateFlow()

    private val _actionState = MutableStateFlow<ActionState>(ActionState.Idle)
    val actionState: StateFlow<ActionState> = _actionState.asStateFlow()

    init { loadEmployees() }

    fun loadEmployees() {
        viewModelScope.launch {
            _employeesState.value = UiState.Loading
            repository.getEmployees().fold(
                onSuccess = { _employeesState.value = UiState.Success(it) },
                onFailure = { _employeesState.value = UiState.Error(it.message ?: "فشل تحميل الموظفين") }
            )
        }
    }

    fun addEmployee(employee: Employee) {
        viewModelScope.launch {
            _actionState.value = ActionState.Loading
            repository.addEmployee(employee).fold(
                onSuccess = {
                    _actionState.value = ActionState.Success("تم إضافة الموظف")
                    loadEmployees()
                },
                onFailure = { _actionState.value = ActionState.Error(it.message ?: "فشل الإضافة") }
            )
        }
    }

    fun updateEmployee(employee: Employee) {
        viewModelScope.launch {
            _actionState.value = ActionState.Loading
            repository.updateEmployee(employee).fold(
                onSuccess = {
                    _actionState.value = ActionState.Success("تم تحديث الموظف")
                    loadEmployees()
                },
                onFailure = { _actionState.value = ActionState.Error(it.message ?: "فشل التحديث") }
            )
        }
    }

    fun deleteEmployee(employeeId: Int) {
        viewModelScope.launch {
            _actionState.value = ActionState.Loading
            repository.deleteEmployee(employeeId).fold(
                onSuccess = {
                    _actionState.value = ActionState.Success("تم حذف الموظف")
                    loadEmployees()
                },
                onFailure = { _actionState.value = ActionState.Error(it.message ?: "فشل الحذف") }
            )
        }
    }

    fun resetActionState() { _actionState.value = ActionState.Idle }
}