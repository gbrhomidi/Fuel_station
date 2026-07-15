package com.aistudio.dieselstationsms.kxmpzq.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.aistudio.dieselstationsms.kxmpzq.data.model.Product
import com.aistudio.dieselstationsms.kxmpzq.ui.components.AppTopBar
import com.aistudio.dieselstationsms.kxmpzq.ui.components.EmptyState
import com.aistudio.dieselstationsms.kxmpzq.ui.components.ErrorMessage
import com.aistudio.dieselstationsms.kxmpzq.ui.components.LoadingIndicator
import com.aistudio.dieselstationsms.kxmpzq.ui.state.ActionState
import com.aistudio.dieselstationsms.kxmpzq.ui.state.UiState
import com.aistudio.dieselstationsms.kxmpzq.ui.viewmodel.ProductsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    viewModel: ProductsViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val productsState by viewModel.productsState.collectAsState()
    val actionState by viewModel.actionState.collectAsState()
    val categories by viewModel.categories.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<Product?>(null) }

    LaunchedEffect(actionState) {
        if (actionState is ActionState.Success) {
            showAddDialog = false
            editingProduct = null
            viewModel.resetActionState()
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(title = "المنتجات", onBackClick = onNavigateBack)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "إضافة منتج")
            }
        }
    ) { padding ->
        when (val state = productsState) {
            is UiState.Loading -> LoadingIndicator(modifier = Modifier.padding(padding))
            is UiState.Error -> ErrorMessage(
                message = state.message,
                onRetry = { viewModel.loadProducts() },
                modifier = Modifier.padding(padding)
            )
            is UiState.Success -> {
                if (state.data.isEmpty()) {
                    EmptyState(message = "لا توجد منتجات", modifier = Modifier.padding(padding))
                } else {
                    LazyColumn(
                        modifier = Modifier.padding(padding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.data) { product ->
                            ProductCard(
                                product = product,
                                onEdit = { editingProduct = product },
                                onDelete = { viewModel.deleteProduct(product.id) }
                            )
                        }
                    }
                }
            }
            else -> { }
        }
    }

    if (showAddDialog || editingProduct != null) {
        ProductDialog(
            product = editingProduct,
            categories = categories,
            onDismiss = {
                showAddDialog = false
                editingProduct = null
            },
            onConfirm = { product ->
                if (editingProduct != null) {
                    viewModel.updateProduct(product)
                } else {
                    viewModel.addProduct(product)
                }
            },
            actionState = actionState
        )
    }
}

@Composable
private fun ProductCard(
    product: Product,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLowStock = product.quantity <= product.minStock
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = if (isLowStock) {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f))
        } else CardDefaults.cardColors()
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "السعر: %.2f | التكلفة: %.2f".format(product.unitPrice, product.costPrice),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "المخزون: %.2f ${if (isLowStock) "⚠️ منخفض" else ""}".format(product.quantity),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isLowStock) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row {
                IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, contentDescription = "تعديل") }
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error) }
            }
        }
    }
}

@Composable
private fun ProductDialog(
    product: Product?,
    categories: List<com.aistudio.dieselstationsms.kxmpzq.data.model.Category>,
    onDismiss: () -> Unit,
    onConfirm: (Product) -> Unit,
    actionState: ActionState
) {
    var name by remember { mutableStateOf(product?.name ?: "") }
    var unitPrice by remember { mutableStateOf(product?.unitPrice?.toString() ?: "") }
    var costPrice by remember { mutableStateOf(product?.costPrice?.toString() ?: "") }
    var quantity by remember { mutableStateOf(product?.quantity?.toString() ?: "0") }
    var minStock by remember { mutableStateOf(product?.minStock?.toString() ?: "0") }
    var selectedCategoryId by remember { mutableStateOf(product?.categoryId) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (product != null) "تعديل منتج" else "منتج جديد") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("الاسم *") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = unitPrice, onValueChange = { unitPrice = it }, label = { Text("سعر البيع") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = costPrice, onValueChange = { costPrice = it }, label = { Text("سعر التكلفة") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = quantity, onValueChange = { quantity = it }, label = { Text("الكمية") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = minStock, onValueChange = { minStock = it }, label = { Text("الحد الأدنى") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true, modifier = Modifier.fillMaxWidth())

                if (actionState is ActionState.Error) {
                    Text(text = actionState.message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        Product(
                            id = product?.id ?: 0,
                            name = name,
                            unitPrice = unitPrice.toDoubleOrNull() ?: 0.0,
                            costPrice = costPrice.toDoubleOrNull() ?: 0.0,
                            quantity = quantity.toDoubleOrNull() ?: 0.0,
                            minStock = minStock.toDoubleOrNull() ?: 0.0,
                            categoryId = selectedCategoryId
                        )
                    )
                },
                enabled = actionState !is ActionState.Loading && name.isNotBlank()
            ) {
                if (actionState is ActionState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("حفظ")
                }
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء") } }
    )
}