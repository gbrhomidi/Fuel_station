package com.aistudio.dieselstationsms.kxmpzq.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aistudio.dieselstationsms.kxmpzq.data.model.Party
import com.aistudio.dieselstationsms.kxmpzq.data.repository.StationRepository
import com.aistudio.dieselstationsms.kxmpzq.ui.state.ActionState
import com.aistudio.dieselstationsms.kxmpzq.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ═══════════════════════════════════════════════════════════════
 * CustomersViewModel — إدارة العملاء (الأطراف)
 * ═══════════════════════════════════════════════════════════════
 */
class CustomersViewModel(
    private val repository: StationRepository
) : ViewModel() {

    private val _customersState = MutableStateFlow<UiState<List<Party>>>(UiState.Loading)
    val customersState: StateFlow<UiState<List<Party>>> = _customersState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _actionState = MutableStateFlow<ActionState>(ActionState.Idle)
    val actionState: StateFlow<ActionState> = _actionState.asStateFlow()

    init { loadCustomers() }

    fun loadCustomers() {
        viewModelScope.launch {
            _customersState.value = UiState.Loading
            repository.getCustomers().fold(
                onSuccess = { _customersState.value = UiState.Success(it) },
                onFailure = { _customersState.value = UiState.Error(it.message ?: "فشل تحميل العملاء") }
            )
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        val current = _customersState.value
        if (current is UiState.Success) {
            val filtered = current.data.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.phone?.contains(query) == true ||
                it.commercialName?.contains(query, ignoreCase = true) == true
            }
            _customersState.value = UiState.Success(filtered)
        }
    }

    fun addCustomer(party: Party) {
        viewModelScope.launch {
            _actionState.value = ActionState.Loading
            repository.addCustomer(party).fold(
                onSuccess = {
                    _actionState.value = ActionState.Success("تم إضافة العميل بنجاح")
                    loadCustomers()
                },
                onFailure = { _actionState.value = ActionState.Error(it.message ?: "فشل الإضافة") }
            )
        }
    }

    fun updateCustomer(party: Party) {
        viewModelScope.launch {
            _actionState.value = ActionState.Loading
            repository.updateCustomer(party).fold(
                onSuccess = {
                    _actionState.value = ActionState.Success("تم تحديث العميل")
                    loadCustomers()
                },
                onFailure = { _actionState.value = ActionState.Error(it.message ?: "فشل التحديث") }
            )
        }
    }

    fun deleteCustomer(partyId: Int) {
        viewModelScope.launch {
            _actionState.value = ActionState.Loading
            repository.deleteCustomer(partyId).fold(
                onSuccess = {
                    _actionState.value = ActionState.Success("تم حذف العميل")
                    loadCustomers()
                },
                onFailure = { _actionState.value = ActionState.Error(it.message ?: "فشل الحذف") }
            )
        }
    }

    fun resetActionState() { _actionState.value = ActionState.Idle }
}