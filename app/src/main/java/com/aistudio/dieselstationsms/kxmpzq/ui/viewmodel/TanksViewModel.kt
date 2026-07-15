package com.aistudio.dieselstationsms.kxmpzq.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aistudio.dieselstationsms.kxmpzq.data.model.FuelType
import com.aistudio.dieselstationsms.kxmpzq.data.model.Pump
import com.aistudio.dieselstationsms.kxmpzq.data.model.Tank
import com.aistudio.dieselstationsms.kxmpzq.data.repository.StationRepository
import com.aistudio.dieselstationsms.kxmpzq.ui.state.ActionState
import com.aistudio.dieselstationsms.kxmpzq.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TanksViewModel(
    private val repository: StationRepository
) : ViewModel() {

    private val _tanksState = MutableStateFlow<UiState<List<Tank>>>(UiState.Loading)
    val tanksState: StateFlow<UiState<List<Tank>>> = _tanksState.asStateFlow()

    private val _pumpsState = MutableStateFlow<UiState<List<Pump>>>(UiState.Loading)
    val pumpsState: StateFlow<UiState<List<Pump>>> = _pumpsState.asStateFlow()

    private val _fuelTypes = MutableStateFlow<List<FuelType>>(emptyList())
    val fuelTypes: StateFlow<List<FuelType>> = _fuelTypes.asStateFlow()

    private val _actionState = MutableStateFlow<ActionState>(ActionState.Idle)
    val actionState: StateFlow<ActionState> = _actionState.asStateFlow()

    init {
        loadTanks()
        loadPumps()
        loadFuelTypes()
    }

    fun loadTanks() {
        viewModelScope.launch {
            _tanksState.value = UiState.Loading
            repository.getTanks().fold(
                onSuccess = { _tanksState.value = UiState.Success(it) },
                onFailure = { _tanksState.value = UiState.Error(it.message ?: "فشل تحميل الخزانات") }
            )
        }
    }

    fun loadPumps() {
        viewModelScope.launch {
            _pumpsState.value = UiState.Loading
            repository.getPumps().fold(
                onSuccess = { _pumpsState.value = UiState.Success(it) },
                onFailure = { _pumpsState.value = UiState.Error(it.message ?: "فشل تحميل المضخات") }
            )
        }
    }

    fun loadFuelTypes() {
        viewModelScope.launch {
            repository.getFuelTypes().fold(
                onSuccess = { _fuelTypes.value = it },
                onFailure = {}
            )
        }
    }

    fun updateTankQuantity(tankId: Int, quantity: Double) {
        viewModelScope.launch {
            _actionState.value = ActionState.Loading
            repository.updateTankQuantity(tankId, quantity).fold(
                onSuccess = {
                    _actionState.value = ActionState.Success("تم تحديث الكمية")
                    loadTanks()
                },
                onFailure = { _actionState.value = ActionState.Error(it.message ?: "فشل التحديث") }
            )
        }
    }

    fun resetActionState() { _actionState.value = ActionState.Idle }
}