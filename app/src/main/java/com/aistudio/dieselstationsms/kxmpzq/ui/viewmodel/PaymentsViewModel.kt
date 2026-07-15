package com.aistudio.dieselstationsms.kxmpzq.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aistudio.dieselstationsms.kxmpzq.data.model.Deposit
import com.aistudio.dieselstationsms.kxmpzq.data.model.Party
import com.aistudio.dieselstationsms.kxmpzq.data.model.Payment
import com.aistudio.dieselstationsms.kxmpzq.data.repository.StationRepository
import com.aistudio.dieselstationsms.kxmpzq.ui.state.ActionState
import com.aistudio.dieselstationsms.kxmpzq.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PaymentsViewModel(
    private val repository: StationRepository
) : ViewModel() {

    private val _paymentsState = MutableStateFlow<UiState<List<Payment>>>(UiState.Loading)
    val paymentsState: StateFlow<UiState<List<Payment>>> = _paymentsState.asStateFlow()

    private val _customers = MutableStateFlow<List<Party>>(emptyList())
    val customers: StateFlow<List<Party>> = _customers.asStateFlow()

    private val _actionState = MutableStateFlow<ActionState>(ActionState.Idle)
    val actionState: StateFlow<ActionState> = _actionState.asStateFlow()

    init {
        loadPayments()
        loadCustomers()
    }

    fun loadPayments() {
        viewModelScope.launch {
            _paymentsState.value = UiState.Loading
            repository.getPayments().fold(
                onSuccess = { _paymentsState.value = UiState.Success(it) },
                onFailure = { _paymentsState.value = UiState.Error(it.message ?: "فشل تحميل المدفوعات") }
            )
        }
    }

    fun loadCustomers() {
        viewModelScope.launch {
            repository.getCustomers().fold(
                onSuccess = { _customers.value = it },
                onFailure = {}
            )
        }
    }

    fun makePayment(payment: Payment) {
        viewModelScope.launch {
            _actionState.value = ActionState.Loading
            repository.makePayment(payment).fold(
                onSuccess = {
                    _actionState.value = ActionState.Success("تم تسجيل الدفع")
                    loadPayments()
                },
                onFailure = { _actionState.value = ActionState.Error(it.message ?: "فشل تسجيل الدفع") }
            )
        }
    }

    fun addDeposit(deposit: Deposit) {
        viewModelScope.launch {
            _actionState.value = ActionState.Loading
            repository.addDeposit(deposit).fold(
                onSuccess = {
                    _actionState.value = ActionState.Success("تم تسجيل الإيداع")
                    loadPayments()
                },
                onFailure = { _actionState.value = ActionState.Error(it.message ?: "فشل تسجيل الإيداع") }
            )
        }
    }

    fun deletePayment(paymentId: Int) {
        viewModelScope.launch {
            _actionState.value = ActionState.Loading
            repository.deletePayment(paymentId).fold(
                onSuccess = {
                    _actionState.value = ActionState.Success("تم حذف الدفع")
                    loadPayments()
                },
                onFailure = { _actionState.value = ActionState.Error(it.message ?: "فشل الحذف") }
            )
        }
    }

    fun resetActionState() { _actionState.value = ActionState.Idle }
}