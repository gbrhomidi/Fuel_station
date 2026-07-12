package com.aistudio.dieselstationsms.kxmpzq.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aistudio.dieselstationsms.kxmpzq.data.model.Employee
import com.aistudio.dieselstationsms.kxmpzq.data.model.Shift
import com.aistudio.dieselstationsms.kxmpzq.data.repository.StationRepository
import com.aistudio.dieselstationsms.kxmpzq.ui.state.ActionState
import com.aistudio.dieselstationsms.kxmpzq.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ShiftsViewModel(
    private val repository: StationRepository
) : ViewModel() {

    private val _shiftsState = MutableStateFlow<UiState<List<Shift>>>(UiState.Loading)
    val shiftsState: StateFlow<UiState<List<Shift>>> = _shiftsState.asStateFlow()

    private val _cashiers = MutableStateFlow<List<Employee>>(emptyList())
    val cashiers: StateFlow<List<Employee>> = _cashiers.asStateFlow()

    private val _actionState = MutableStateFlow<ActionState>(ActionState.Idle)
    val actionState: StateFlow<ActionState> = _actionState.asStateFlow()

    init {
        loadShifts()
        loadCashiers()
    }

    fun loadShifts() {
        viewModelScope.launch {
            _shiftsState.value = UiState.Loading
            repository.getShifts().fold(
                onSuccess = { _shiftsState.value = UiState.Success(it) },
                onFailure = { _shiftsState.value = UiState.Error(it.message ?: "فشل تحميل الورديات") }
            )
        }
    }

    fun loadCashiers() {
        viewModelScope.launch {
            repository.getCashiers().fold(
                onSuccess = { _cashiers.value = it },
                onFailure = {}
            )
        }
    }

    fun addShift(shift: Shift) {
        viewModelScope.launch {
            _actionState.value = ActionState.Loading
            repository.addShift(shift).fold(
                onSuccess = {
                    _actionState.value = ActionState.Success("تم إضافة الوردية")
                    loadShifts()
                },
                onFailure = { _actionState.value = ActionState.Error(it.message ?: "فشل الإضافة") }
            )
        }
    }

    fun deleteShift(shiftId: Int) {
        viewModelScope.launch {
            _actionState.value = ActionState.Loading
            repository.deleteShift(shiftId).fold(
                onSuccess = {
                    _actionState.value = ActionState.Success("تم حذف الوردية")
                    loadShifts()
                },
                onFailure = { _actionState.value = ActionState.Error(it.message ?: "فشل الحذف") }
            )
        }
    }

    fun resetActionState() { _actionState.value = ActionState.Idle }
}