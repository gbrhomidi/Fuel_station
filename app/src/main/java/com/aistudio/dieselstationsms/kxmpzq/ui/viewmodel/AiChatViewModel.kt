package com.aistudio.dieselstationsms.kxmpzq.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aistudio.dieselstationsms.kxmpzq.data.model.AiChatResponse
import com.aistudio.dieselstationsms.kxmpzq.data.model.AiInsight
import com.aistudio.dieselstationsms.kxmpzq.data.repository.StationRepository
import com.aistudio.dieselstationsms.kxmpzq.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AiChatViewModel(
    private val repository: StationRepository
) : ViewModel() {

    private val _chatHistory = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val chatHistory: StateFlow<List<Pair<String, String>>> = _chatHistory.asStateFlow()

    private val _currentResponse = MutableStateFlow<UiState<AiChatResponse>>(UiState.Idle)
    val currentResponse: StateFlow<UiState<AiChatResponse>> = _currentResponse.asStateFlow()

    private val _insightState = MutableStateFlow<UiState<AiInsight>>(UiState.Idle)
    val insightState: StateFlow<UiState<AiInsight>> = _insightState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun sendMessage(message: String) {
        if (message.isBlank()) return
        viewModelScope.launch {
            _isLoading.value = true
            _currentResponse.value = UiState.Loading
            val currentHistory = _chatHistory.value.toMutableList()
            currentHistory.add("user" to message)
            _chatHistory.value = currentHistory

            repository.aiChat(message, sessionId = "compose_${System.currentTimeMillis()}").fold(
                onSuccess = {
                    currentHistory.add("ai" to it.response)
                    _chatHistory.value = currentHistory
                    _currentResponse.value = UiState.Success(it)
                },
                onFailure = {
                    _currentResponse.value = UiState.Error(it.message ?: "فشل الاتصال بالذكاء الاصطناعي")
                }
            )
            _isLoading.value = false
        }
    }

    fun loadAiInsight() {
        viewModelScope.launch {
            _insightState.value = UiState.Loading
            repository.getAiInsight().fold(
                onSuccess = { _insightState.value = UiState.Success(it) },
                onFailure = { _insightState.value = UiState.Error(it.message ?: "فشل تحميل التحليل") }
            )
        }
    }

    fun clearChat() {
        _chatHistory.value = emptyList()
        _currentResponse.value = UiState.Idle
    }
}