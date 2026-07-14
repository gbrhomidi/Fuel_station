package com.aistudio.dieselstationsms.kxmpzq.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aistudio.dieselstationsms.kxmpzq.data.model.SmsMessage
import com.aistudio.dieselstationsms.kxmpzq.data.model.SmsWhitelistEntry
import com.aistudio.dieselstationsms.kxmpzq.data.repository.StationRepository
import com.aistudio.dieselstationsms.kxmpzq.ui.state.ActionState
import com.aistudio.dieselstationsms.kxmpzq.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SmsViewModel(
    private val repository: StationRepository
) : ViewModel() {

    private val _smsLogsState = MutableStateFlow<UiState<List<SmsMessage>>>(UiState.Loading)
    val smsLogsState: StateFlow<UiState<List<SmsMessage>>> = _smsLogsState.asStateFlow()

    private val _whitelistState = MutableStateFlow<UiState<List<SmsWhitelistEntry>>>(UiState.Loading)
    val whitelistState: StateFlow<UiState<List<SmsWhitelistEntry>>> = _whitelistState.asStateFlow()

    private val _actionState = MutableStateFlow<ActionState>(ActionState.Idle)
    val actionState: StateFlow<ActionState> = _actionState.asStateFlow()

    init {
        loadSmsLogs()
        loadWhitelist()
    }

    fun loadSmsLogs() {
        viewModelScope.launch {
            _smsLogsState.value = UiState.Loading
            repository.getSmsLogs().fold(
                onSuccess = { _smsLogsState.value = UiState.Success(it) },
                onFailure = { _smsLogsState.value = UiState.Error(it.message ?: "فشل تحميل سجلات SMS") }
            )
        }
    }

    fun loadWhitelist() {
        viewModelScope.launch {
            _whitelistState.value = UiState.Loading
            repository.getWhitelist().fold(
                onSuccess = { _whitelistState.value = UiState.Success(it) },
                onFailure = { _whitelistState.value = UiState.Error(it.message ?: "فشل تحميل القائمة البيضاء") }
            )
        }
    }

    fun addWhitelist(entry: SmsWhitelistEntry) {
        viewModelScope.launch {
            _actionState.value = ActionState.Loading
            repository.addWhitelist(entry).fold(
                onSuccess = {
                    _actionState.value = ActionState.Success("تمت الإضافة للقائمة البيضاء")
                    loadWhitelist()
                },
                onFailure = { _actionState.value = ActionState.Error(it.message ?: "فشل الإضافة") }
            )
        }
    }

    fun removeWhitelist(phone: String) {
        viewModelScope.launch {
            _actionState.value = ActionState.Loading
            repository.removeWhitelist(phone).fold(
                onSuccess = {
                    _actionState.value = ActionState.Success("تمت الإزالة")
                    loadWhitelist()
                },
                onFailure = { _actionState.value = ActionState.Error(it.message ?: "فشل الإزالة") }
            )
        }
    }

    fun resetActionState() { _actionState.value = ActionState.Idle }
}