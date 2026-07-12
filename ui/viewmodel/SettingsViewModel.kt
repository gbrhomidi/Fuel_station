package com.aistudio.dieselstationsms.kxmpzq.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aistudio.dieselstationsms.kxmpzq.data.repository.StationRepository
import com.aistudio.dieselstationsms.kxmpzq.ui.state.ActionState
import com.aistudio.dieselstationsms.kxmpzq.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: StationRepository
) : ViewModel() {

    private val _settingsState = MutableStateFlow<UiState<Map<String, String>>>(UiState.Loading)
    val settingsState: StateFlow<UiState<Map<String, String>>> = _settingsState.asStateFlow()

    private val _actionState = MutableStateFlow<ActionState>(ActionState.Idle)
    val actionState: StateFlow<ActionState> = _actionState.asStateFlow()

    private val _geminiKey = MutableStateFlow("")
    val geminiKey: StateFlow<String> = _geminiKey.asStateFlow()

    private val _deepseekKey = MutableStateFlow("")
    val deepseekKey: StateFlow<String> = _deepseekKey.asStateFlow()

    private val _chatgptKey = MutableStateFlow("")
    val chatgptKey: StateFlow<String> = _chatgptKey.asStateFlow()

    init { loadSettings() }

    fun loadSettings() {
        viewModelScope.launch {
            _settingsState.value = UiState.Loading
            repository.getAllSettings().fold(
                onSuccess = {
                    _settingsState.value = UiState.Success(it)
                    _geminiKey.value = it["gemini_api_key"] ?: ""
                    _deepseekKey.value = it["deepseek_api_key"] ?: ""
                    _chatgptKey.value = it["chatgpt_api_key"] ?: ""
                },
                onFailure = { _settingsState.value = UiState.Error(it.message ?: "فشل تحميل الإعدادات") }
            )
        }
    }

    fun updateSetting(key: String, value: String) {
        viewModelScope.launch {
            _actionState.value = ActionState.Loading
            repository.setSetting(key, value).fold(
                onSuccess = {
                    _actionState.value = ActionState.Success("تم حفظ الإعداد")
                    loadSettings()
                },
                onFailure = { _actionState.value = ActionState.Error(it.message ?: "فشل الحفظ") }
            )
        }
    }

    fun saveApiKeys(gemini: String, deepseek: String, chatgpt: String) {
        viewModelScope.launch {
            _actionState.value = ActionState.Loading
            var success = true
            if (gemini.isNotBlank()) {
                repository.setSetting("gemini_api_key", gemini).onFailure { success = false }
            }
            if (deepseek.isNotBlank()) {
                repository.setSetting("deepseek_api_key", deepseek).onFailure { success = false }
            }
            if (chatgpt.isNotBlank()) {
                repository.setSetting("chatgpt_api_key", chatgpt).onFailure { success = false }
            }
            if (success) {
                _actionState.value = ActionState.Success("تم حفظ مفاتيح API")
                loadSettings()
            } else {
                _actionState.value = ActionState.Error("فشل حفظ بعض المفاتيح")
            }
        }
    }

    fun resetActionState() { _actionState.value = ActionState.Idle }
}