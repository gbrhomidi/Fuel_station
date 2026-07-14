package com.aistudio.dieselstationsms.kxmpzq.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aistudio.dieselstationsms.kxmpzq.data.model.DashboardData
import com.aistudio.dieselstationsms.kxmpzq.data.model.DailySales
import com.aistudio.dieselstationsms.kxmpzq.data.repository.StationRepository
import com.aistudio.dieselstationsms.kxmpzq.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ═══════════════════════════════════════════════════════════════
 * DashboardViewModel — لوحة التحكم الرئيسية
 * ═══════════════════════════════════════════════════════════════
 *
 * يوفر:
 * - إحصائيات Dashboard (إيرادات، عمليات، مخزون، صيانة)
 * - مخطط المبيعات اليومية (آخر 30 يوم)
 * - إشعارات التنبيهات (مخزون منخفض، صيانة معلقة)
 */
class DashboardViewModel(
    private val repository: StationRepository
) : ViewModel() {

    private val _dashboardState = MutableStateFlow<UiState<DashboardData>>(UiState.Loading)
    val dashboardState: StateFlow<UiState<DashboardData>> = _dashboardState.asStateFlow()

    private val _dailySalesState = MutableStateFlow<UiState<List<DailySales>>>(UiState.Idle)
    val dailySalesState: StateFlow<UiState<List<DailySales>>> = _dailySalesState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        loadDashboard()
        loadDailySales()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _dashboardState.value = UiState.Loading
            repository.getDashboard().fold(
                onSuccess = { _dashboardState.value = UiState.Success(it) },
                onFailure = { _dashboardState.value = UiState.Error(it.message ?: "فشل تحميل لوحة التحكم") }
            )
        }
    }

    fun loadDailySales() {
        viewModelScope.launch {
            _dailySalesState.value = UiState.Loading
            repository.getDailySales().fold(
                onSuccess = { _dailySalesState.value = UiState.Success(it) },
                onFailure = { _dailySalesState.value = UiState.Error(it.message ?: "فشل تحميل المبيعات اليومية") }
            )
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            loadDashboard()
            loadDailySales()
            _isRefreshing.value = false
        }
    }
}