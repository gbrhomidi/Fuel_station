package com.aistudio.dieselstationsms.kxmpzq.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aistudio.dieselstationsms.kxmpzq.data.model.MaintenanceRequest
import com.aistudio.dieselstationsms.kxmpzq.data.repository.StationRepository
import com.aistudio.dieselstationsms.kxmpzq.ui.state.ActionState
import com.aistudio.dieselstationsms.kxmpzq.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MaintenanceViewModel(
    private val repository: StationRepository
) : ViewModel() {

    private val _requestsState = MutableStateFlow<UiState<List<MaintenanceRequest>>>(UiState.Loading)
    val requestsState: StateFlow<UiState<List<MaintenanceRequest>>> = _requestsState.asStateFlow()

    private val _actionState = MutableStateFlow<ActionState>(ActionState.Idle)
    val actionState: StateFlow<ActionState> = _actionState.asStateFlow()

    init { loadRequests() }

    fun loadRequests() {
        viewModelScope.launch {
            _requestsState.value = UiState.Loading
            repository.getMaintenanceRequests().fold(
                onSuccess = { _requestsState.value = UiState.Success(it) },
                onFailure = { _requestsState.value = UiState.Error(it.message ?: "فشل تحميل طلبات الصيانة") }
            )
        }
    }

    fun addRequest(request: MaintenanceRequest) {
        viewModelScope.launch {
            _actionState.value = ActionState.Loading
            repository.addMaintenanceRequest(request).fold(
                onSuccess = {
                    _actionState.value = ActionState.Success("تم إضافة طلب الصيانة")
                    loadRequests()
                },
                onFailure = { _actionState.value = ActionState.Error(it.message ?: "فشل الإضافة") }
            )
        }
    }

    fun updateStatus(requestId: Int, status: String) {
        viewModelScope.launch {
            _actionState.value = ActionState.Loading
            repository.updateMaintenanceStatus(requestId, status).fold(
                onSuccess = {
                    _actionState.value = ActionState.Success("تم تحديث الحالة")
                    loadRequests()
                },
                onFailure = { _actionState.value = ActionState.Error(it.message ?: "فشل التحديث") }
            )
        }
    }

    fun deleteRequest(requestId: Int) {
        viewModelScope.launch {
            _actionState.value = ActionState.Loading
            repository.deleteMaintenance(requestId).fold(
                onSuccess = {
                    _actionState.value = ActionState.Success("تم حذف الطلب")
                    loadRequests()
                },
                onFailure = { _actionState.value = ActionState.Error(it.message ?: "فشل الحذف") }
            )
        }
    }

    fun resetActionState() { _actionState.value = ActionState.Idle }
}