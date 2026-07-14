package com.aistudio.dieselstationsms.kxmpzq.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.aistudio.dieselstationsms.kxmpzq.data.model.Sale
import com.aistudio.dieselstationsms.kxmpzq.ui.components.AppTopBar
import com.aistudio.dieselstationsms.kxmpzq.ui.components.EmptyState
import com.aistudio.dieselstationsms.kxmpzq.ui.components.ErrorMessage
import com.aistudio.dieselstationsms.kxmpzq.ui.components.LoadingIndicator
import com.aistudio.dieselstationsms.kxmpzq.ui.state.ActionState
import com.aistudio.dieselstationsms.kxmpzq.ui.state.UiState
import com.aistudio.dieselstationsms.kxmpzq.ui.viewmodel.SalesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesScreen(
    viewModel: SalesViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val salesState by viewModel.salesState.collectAsState()
    val actionState by viewModel.actionState.collectAsState()
    val customers by viewModel.customers.collectAsState()
    val fuelTypes by viewModel.fuelTypes.collectAsState()
    val pumps by viewModel.pumps.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    // Handle action states
    LaunchedEffect(actionState) {
        if (actionState is ActionState.Success) {
            showAddDialog = false
            viewModel.resetActionState()
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "المبيعات",
                onBackClick = onNavigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "إضافة بيع")
            }
        }
    ) { padding ->
        when (val state = salesState) {
            is UiState.Loading -> LoadingIndicator(modifier = Modifier.padding(padding))
            is UiState.Error -> ErrorMessage(
                message = state.message,
                onRetry = { viewModel.loadSales() },
                modifier = Modifier.padding(padding)
            )
            is UiState.Success -> {
                if (state.data.isEmpty()) {
                    EmptyState(message = "لا توجد مبيعات مسجلة", modifier = Modifier.padding(padding))
                } else {
                    LazyColumn(
                        modifier = Modifier.padding(padding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.data) { sale ->
                            SaleCard(
                                sale = sale,
                                onDelete = { viewModel.deleteSale(sale.id) }
                            )
                        }
                    }
                }
            }
            else -> { }
        }
    }

    // Add Sale Dialog
    if (showAddDialog) {
        AddSaleDialog(
            customers = customers,
            fuelTypes = fuelTypes,
            pumps = pumps,
            onDismiss = { showAddDialog = false },
            onConfirm = { sale ->
                viewModel.onFormChange { sale }
                viewModel.executeSale()
            },
            actionState = actionState
        )
    }
}

@Composable
private fun SaleCard(
    sale: Sale,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "#${sale.id} — ${sale.paymentMethod}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "الكمية: %.2f لتر × %.2f = %.2f".format(
                        sale.quantity, sale.pricePerLiter, sale.totalAmount
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = sale.createdAt ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun AddSaleDialog(
    customers: List<com.aistudio.dieselstationsms.kxmpzq.data.model.Party>,
    fuelTypes: List<com.aistudio.dieselstationsms.kxmpzq.data.model.FuelType>,
    pumps: List<com.aistudio.dieselstationsms.kxmpzq.data.model.Pump>,
    onDismiss: () -> Unit,
    onConfirm: (Sale) -> Unit,
    actionState: ActionState
) {
    var quantity by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var selectedCustomerId by remember { mutableStateOf<Int?>(null) }
    var selectedFuelTypeId by remember { mutableStateOf<Int?>(null) }
    var selectedPumpId by remember { mutableStateOf<Int?>(null) }
    var paymentMethod by remember { mutableStateOf("cash") }

    val total = try {
        val q = quantity.toDoubleOrNull() ?: 0.0
        val p = price.toDoubleOrNull() ?: 0.0
        q * p
    } catch (_: Exception) { 0.0 }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("عملية بيع جديدة") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Customer Dropdown
                var customerExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = customerExpanded,
                    onExpandedChange = { customerExpanded = it }
                ) {
                    OutlinedTextField(
                        value = customers.find { it.id == selectedCustomerId }?.name ?: "اختر العميل",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("العميل") },
                        modifier = Modifier.menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = customerExpanded) }
                    )
                    ExposedDropdownMenu(
                        expanded = customerExpanded,
                        onDismissRequest = { customerExpanded = false }
                    ) {
                        customers.forEach { customer ->
                            DropdownMenuItem(
                                text = { Text(customer.name) },
                                onClick = {
                                    selectedCustomerId = customer.id
                                    customerExpanded = false
                                }
                            )
                        }
                    }
                }

                // Fuel Type Dropdown
                var fuelExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = fuelExpanded,
                    onExpandedChange = { fuelExpanded = it }
                ) {
                    OutlinedTextField(
                        value = fuelTypes.find { it.id == selectedFuelTypeId }?.name ?: "اختر الوقود",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("نوع الوقود") },
                        modifier = Modifier.menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = fuelExpanded) }
                    )
                    ExposedDropdownMenu(
                        expanded = fuelExpanded,
                        onDismissRequest = { fuelExpanded = false }
                    ) {
                        fuelTypes.forEach { fuel ->
                            DropdownMenuItem(
                                text = { Text("${fuel.name} — ${fuel.pricePerLiter}") },
                                onClick = {
                                    selectedFuelTypeId = fuel.id
                                    price = fuel.pricePerLiter.toString()
                                    fuelExpanded = false
                                }
                            )
                        }
                    }
                }

                // Pump Dropdown
                var pumpExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = pumpExpanded,
                    onExpandedChange = { pumpExpanded = it }
                ) {
                    OutlinedTextField(
                        value = pumps.find { it.id == selectedPumpId }?.name ?: "اختر المضخة",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("المضخة") },
                        modifier = Modifier.menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = pumpExpanded) }
                    )
                    ExposedDropdownMenu(
                        expanded = pumpExpanded,
                        onDismissRequest = { pumpExpanded = false }
                    ) {
                        pumps.forEach { pump ->
                            DropdownMenuItem(
                                text = { Text(pump.name) },
                                onClick = {
                                    selectedPumpId = pump.id
                                    pumpExpanded = false
                                }
                            )
                        }
                    }
                }

                // Quantity
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("الكمية (لتر)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Price
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("السعر للتر") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Payment Method
                var paymentExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = paymentExpanded,
                    onExpandedChange = { paymentExpanded = it }
                ) {
                    OutlinedTextField(
                        value = when (paymentMethod) {
                            "cash" -> "نقدي"
                            "credit" -> "آجل"
                            "card" -> "بطاقة"
                            else -> paymentMethod
                        },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("طريقة الدفع") },
                        modifier = Modifier.menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = paymentExpanded) }
                    )
                    ExposedDropdownMenu(
                        expanded = paymentExpanded,
                        onDismissRequest = { paymentExpanded = false }
                    ) {
                        listOf("cash" to "نقدي", "credit" to "آجل", "card" to "بطاقة").forEach { (value, label) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    paymentMethod = value
                                    paymentExpanded = false
                                }
                            )
                        }
                    }
                }

                // Total
                Text(
                    text = "الإجمالي: %.2f".format(total),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                if (actionState is ActionState.Error) {
                    Text(
                        text = actionState.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        Sale(
                            partyId = selectedCustomerId,
                            fuelTypeId = selectedFuelTypeId,
                            pumpId = selectedPumpId,
                            quantity = quantity.toDoubleOrNull() ?: 0.0,
                            pricePerLiter = price.toDoubleOrNull() ?: 0.0,
                            totalAmount = total,
                            paymentMethod = paymentMethod
                        )
                    )
                },
                enabled = actionState !is ActionState.Loading &&
                         selectedFuelTypeId != null &&
                         quantity.isNotBlank() &&
                         price.isNotBlank()
            ) {
                if (actionState is ActionState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("تنفيذ البيع")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}