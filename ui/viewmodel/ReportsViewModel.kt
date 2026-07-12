package com.aistudio.dieselstationsms.kxmpzq.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aistudio.dieselstationsms.kxmpzq.data.model.EodReport
import com.aistudio.dieselstationsms.kxmpzq.data.model.MonthlySales
import com.aistudio.dieselstationsms.kxmpzq.data.model.ProfitReport
import com.aistudio.dieselstationsms.kxmpzq.data.repository.StationRepository
import com.aistudio.dieselstationsms.kxmpzq.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReportsViewModel(
    private val repository: StationRepository
) : ViewModel() {

    private val _eodState = MutableStateFlow<UiState<EodReport>>(UiState.Idle)
    val eodState: StateFlow<UiState<EodReport>> = _eodState.asStateFlow()

    private val _monthlySalesState = MutableStateFlow<UiState<List<MonthlySales>>>(UiState.Idle)
    val monthlySalesState: StateFlow<UiState<List<MonthlySales>>> = _monthlySalesState.asStateFlow()

    private val _profitState = MutableStateFlow<UiState<ProfitReport>>(UiState.Idle)
    val profitState: StateFlow<UiState<ProfitReport>> = _profitState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadEodReport() {
        viewModelScope.launch {
            _eodState.value = UiState.Loading
            repository.getEodReport().fold(
                onSuccess = { _eodState.value = UiState.Success(it) },
                onFailure = { _eodState.value = UiState.Error(it.message ?: "فشل تحميل تقرير EOD") }
            )
        }
    }

    fun loadMonthlySales() {
        viewModelScope.launch {
            _monthlySalesState.value = UiState.Loading
            repository.getMonthlySales().fold(
                onSuccess = { _monthlySalesState.value = UiState.Success(it) },
                onFailure = { _monthlySalesState.value = UiState.Error(it.message ?: "فشل تحميل المبيعات الشهرية") }
            )
        }
    }

    fun loadProfitReport(fromDate: String, toDate: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _profitState.value = UiState.Loading
            repository.getProfitReport(fromDate, toDate).fold(
                onSuccess = { _profitState.value = UiState.Success(it) },
                onFailure = { _profitState.value = UiState.Error(it.message ?: "فشل تحميل تقرير الأرباح") }
            )
            _isLoading.value = false
        }
    }

    fun exportData() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.exportData().fold(
                onSuccess = {
                    // سيتم معالجة تصدير البيانات لاحقاً (مشاركة ملف، إلخ)
                    _isLoading.value = false
                },
                onFailure = { _isLoading.value = false }
            )
        }
    }
}