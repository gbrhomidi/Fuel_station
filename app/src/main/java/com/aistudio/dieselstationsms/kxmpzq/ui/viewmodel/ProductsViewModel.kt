package com.aistudio.dieselstationsms.kxmpzq.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aistudio.dieselstationsms.kxmpzq.data.model.Category
import com.aistudio.dieselstationsms.kxmpzq.data.model.FuelType
import com.aistudio.dieselstationsms.kxmpzq.data.model.Product
import com.aistudio.dieselstationsms.kxmpzq.data.repository.StationRepository
import com.aistudio.dieselstationsms.kxmpzq.ui.state.ActionState
import com.aistudio.dieselstationsms.kxmpzq.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductsViewModel(
    private val repository: StationRepository
) : ViewModel() {

    private val _productsState = MutableStateFlow<UiState<List<Product>>>(UiState.Loading)
    val productsState: StateFlow<UiState<List<Product>>> = _productsState.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _fuelTypes = MutableStateFlow<List<FuelType>>(emptyList())
    val fuelTypes: StateFlow<List<FuelType>> = _fuelTypes.asStateFlow()

    private val _actionState = MutableStateFlow<ActionState>(ActionState.Idle)
    val actionState: StateFlow<ActionState> = _actionState.asStateFlow()

    init {
        loadProducts()
        loadCategories()
        loadFuelTypes()
    }

    fun loadProducts() {
        viewModelScope.launch {
            _productsState.value = UiState.Loading
            repository.getProducts().fold(
                onSuccess = { _productsState.value = UiState.Success(it) },
                onFailure = { _productsState.value = UiState.Error(it.message ?: "فشل تحميل المنتجات") }
            )
        }
    }

    fun loadCategories() {
        viewModelScope.launch {
            repository.getCategories().fold(
                onSuccess = { _categories.value = it },
                onFailure = {}
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

    fun addProduct(product: Product) {
        viewModelScope.launch {
            _actionState.value = ActionState.Loading
            repository.addProduct(product).fold(
                onSuccess = {
                    _actionState.value = ActionState.Success("تم إضافة المنتج")
                    loadProducts()
                },
                onFailure = { _actionState.value = ActionState.Error(it.message ?: "فشل الإضافة") }
            )
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            _actionState.value = ActionState.Loading
            repository.updateProduct(product).fold(
                onSuccess = {
                    _actionState.value = ActionState.Success("تم تحديث المنتج")
                    loadProducts()
                },
                onFailure = { _actionState.value = ActionState.Error(it.message ?: "فشل التحديث") }
            )
        }
    }

    fun deleteProduct(productId: Int) {
        viewModelScope.launch {
            _actionState.value = ActionState.Loading
            repository.deleteProduct(productId).fold(
                onSuccess = {
                    _actionState.value = ActionState.Success("تم حذف المنتج")
                    loadProducts()
                },
                onFailure = { _actionState.value = ActionState.Error(it.message ?: "فشل الحذف") }
            )
        }
    }

    fun resetActionState() { _actionState.value = ActionState.Idle }
}