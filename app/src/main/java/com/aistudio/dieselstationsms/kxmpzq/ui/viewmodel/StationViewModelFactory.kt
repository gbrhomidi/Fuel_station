package com.aistudio.dieselstationsms.kxmpzq.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aistudio.dieselstationsms.kxmpzq.data.repository.StationRepository

/**
 * ═══════════════════════════════════════════════════════════════
 * StationViewModelFactory — Factory لإنشاء ViewModels
 * ═══════════════════════════════════════════════════════════════
 *
 * يُستخدم حتى يتم إدخال Hilt في مرحلة لاحقة.
 * يوفر Dependency Injection يدوي للـ Repository في كل ViewModel.
 */
class StationViewModelFactory(
    private val repository: StationRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            LoginViewModel::class.java -> LoginViewModel(repository) as T
            DashboardViewModel::class.java -> DashboardViewModel(repository) as T
            SalesViewModel::class.java -> SalesViewModel(repository) as T
            CustomersViewModel::class.java -> CustomersViewModel(repository) as T
            ProductsViewModel::class.java -> ProductsViewModel(repository) as T
            EmployeesViewModel::class.java -> EmployeesViewModel(repository) as T
            TanksViewModel::class.java -> TanksViewModel(repository) as T
            PaymentsViewModel::class.java -> PaymentsViewModel(repository) as T
            ShiftsViewModel::class.java -> ShiftsViewModel(repository) as T
            MaintenanceViewModel::class.java -> MaintenanceViewModel(repository) as T
            SmsViewModel::class.java -> SmsViewModel(repository) as T
            ReportsViewModel::class.java -> ReportsViewModel(repository) as T
            SettingsViewModel::class.java -> SettingsViewModel(repository) as T
            AiChatViewModel::class.java -> AiChatViewModel(repository) as T
            UsersViewModel::class.java -> UsersViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}