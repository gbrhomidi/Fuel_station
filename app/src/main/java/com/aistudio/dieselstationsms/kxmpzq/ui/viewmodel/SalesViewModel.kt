package com.aistudio.dieselstationsms.kxmpzq.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aistudio.dieselstationsms.kxmpzq.data.model.FuelType
import com.aistudio.dieselstationsms.kxmpzq.data.model.Party
import com.aistudio.dieselstationsms.kxmpzq.data.model.Pump
import com.aistudio.dieselstationsms.kxmpzq.data.model.Sale
import com.aistudio.dieselstationsms.kxmpzq.data.repository.StationRepository
import com.aistudio.dieselstationsms.kxmpzq.ui.state.ActionState
import com.aistudio.dieselstationsms.kxmpzq.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ═══════════════════════════════════════════════════════════════
 * SalesViewModel — إدارة المبيعات وشاشة البيع
 * ═══════════════════════════════════════════════════════════════
 */
class SalesViewModel(
    private val repository: StationRepository
) : ViewModel() {

    // ─── قائمة المبيعات ───
    private val _salesState = MutableStateFlow<UiState<List<Sale>>>(UiState.Loading)
    val salesState: StateFlow<UiState<List<Sale>>> = _salesState.asStateFlow()

    // ─── Dropdowns ───
    private val _customers = MutableStateFlow<List<Party>>(emptyList())
    val customers: StateFlow<List<Party>> = _customers.asStateFlow()

    private val _fuelTypes = MutableStateFlow<List<FuelType>>(emptyList())
    val fuelTypes: StateFlow<List<FuelType>> = _fuelTypes.asStateFlow()

    private val _pumps = MutableStateFlow<List<Pump>>(emptyList())
    val pumps: StateFlow<List<Pump>> = _pumps.asStateFlow()

    // ─── نموذج البيع الجديد ───
    private val _saleForm = MutableStateFlow(Sale())
    val saleForm: StateFlow<Sale> = _saleForm.asStateFlow()

    // ─── حالة العملية ───
    private val _actionState = MutableStateFlow<ActionState>(ActionState.Idle)
    val actionState: StateFlow<ActionState> = _actionState.asStateFlow()

    init {
        loadSales()
        loadDropdowns()
    }

    fun loadSales() {
        viewModelScope.launch {
            _salesState.value = UiState.Loading
            repository.getSales().fold(
                onSuccess = { _salesState.value = UiState.Success(it) },
                onFailure = { _salesState.value = UiState.Error(it.message ?: "فشل تحميل المبيعات") }
            )
        }
    }

    fun loadDropdowns() {
        viewModelScope.launch {
            repository.getCustomers().fold(onSuccess = { _customers.value = it }, onFailure = {})
            repository.getFuelTypes().fold(onSuccess = { _fuelTypes.value = it }, onFailure = {})
            repository.getPumps().fold(onSuccess = { _pumps.value = it }, onFailure = {})
        }
    }

    fun onFormChange(update: (Sale) -> Sale) {
        _saleForm.value = update(_saleForm.value)
    }

    fun executeSale() {
        viewModelScope.launch {
            _actionState.value = ActionState.Loading
            repository.executeSale(_saleForm.value).fold(
                onSuccess = {
                    _actionState.value = ActionState.Success("تمت عملية البيع بنجاح")
                    _saleForm.value = Sale()
                    loadSales()
                },
                onFailure = { _actionState.value = ActionState.Error(it.message ?: "فشل تنفيذ البيع") }
            )
        }
    }

    fun deleteSale(saleId: Int) {
        viewModelScope.launch {
            _actionState.value = ActionState.Loading
            repository.deleteSale(saleId).fold(
                onSuccess = {
                    _actionState.value = ActionState.Success("تم حذف البيع")
                    loadSales()
                },
                onFailure = { _actionState.value = ActionState.Error(it.message ?: "فشل الحذف") }
            )
        }
    }

    fun resetActionState() { _actionState.value = ActionState.Idle }
}