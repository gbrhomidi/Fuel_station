package com.aistudio.dieselstationsms.kxmpzq.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aistudio.dieselstationsms.kxmpzq.data.model.Party
import com.aistudio.dieselstationsms.kxmpzq.ui.components.AppTopBar
import com.aistudio.dieselstationsms.kxmpzq.ui.components.EmptyState
import com.aistudio.dieselstationsms.kxmpzq.ui.components.ErrorMessage
import com.aistudio.dieselstationsms.kxmpzq.ui.components.LoadingIndicator
import com.aistudio.dieselstationsms.kxmpzq.ui.state.ActionState
import com.aistudio.dieselstationsms.kxmpzq.ui.state.UiState
import com.aistudio.dieselstationsms.kxmpzq.ui.viewmodel.CustomersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomersScreen(
    viewModel: CustomersViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val customersState by viewModel.customersState.collectAsState()
    val actionState by viewModel.actionState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingCustomer by remember { mutableStateOf<Party?>(null) }

    LaunchedEffect(actionState) {
        if (actionState is ActionState.Success) {
            showAddDialog = false
            editingCustomer = null
            viewModel.resetActionState()
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "العملاء",
                onBackClick = onNavigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "إضافة عميل")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                label = { Text("بحث") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            when (val state = customersState) {
                is UiState.Loading -> LoadingIndicator()
                is UiState.Error -> ErrorMessage(
                    message = state.message,
                    onRetry = { viewModel.loadCustomers() }
                )
                is UiState.Success -> {
                    if (state.data.isEmpty()) {
                        EmptyState(message = "لا يوجد عملاء")
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.data) { customer ->
                                CustomerCard(
                                    customer = customer,
                                    onEdit = { editingCustomer = customer },
                                    onDelete = { viewModel.deleteCustomer(customer.id) }
                                )
                            }
                        }
                    }
                }
                else -> { }
            }
        }
    }

    // Add/Edit Dialog
    if (showAddDialog || editingCustomer != null) {
        CustomerDialog(
            customer = editingCustomer,
            onDismiss = {
                showAddDialog = false
                editingCustomer = null
            },
            onConfirm = { customer ->
                if (editingCustomer != null) {
                    viewModel.updateCustomer(party = party)
                } else {
                    viewModel.addCustomer(party = party)
                }
            },
            actionState = actionState
        )
    }
}

@Composable
private fun CustomerCard(
    customer: Party,
    onEdit: () -> Unit,
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
                    text = customer.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (!customer.commercialName.isNullOrBlank()) {
                    Text(
                        text = customer.commercialName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "📞 ${customer.phone ?: "—"} | 💰 ${"%.2f".format(customer.balance)}",
                    style = MaterialTheme.typography.bodySmall
                )
                if (customer.vipLevel > 0) {
                    Text(
                        text = "⭐ VIP ${customer.vipLevel} | 🚗 ${customer.fleetSize} مركبة",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "تعديل")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
private fun CustomerDialog(
    customer: Party?,
    onDismiss: () -> Unit,
    onConfirm: (Party) -> Unit,
    actionState: ActionState
) {
    var name by remember { mutableStateOf(customer?.name ?: "") }
    var commercialName by remember { mutableStateOf(customer?.commercialName ?: "") }
    var phone by remember { mutableStateOf(customer?.phone ?: "") }
    var address by remember { mutableStateOf(customer?.address ?: "") }
    var balance by remember { mutableStateOf(customer?.balance?.toString() ?: "0") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (customer != null) "تعديل عميل" else "عميل جديد") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("الاسم *") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = commercialName, onValueChange = { commercialName = it }, label = { Text("الاسم التجاري") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("الهاتف") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("العنوان") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = balance, onValueChange = { balance = it }, label = { Text("الرصيد") }, singleLine = true, modifier = Modifier.fillMaxWidth())

                if (actionState is ActionState.Error) {
                    Text(text = actionState.message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        Party(
                            id = customer?.id ?: 0,
                            name = name,
                            commercialName = commercialName.takeIf { it.isNotBlank() },
                            phone = phone.takeIf { it.isNotBlank() },
                            address = address.takeIf { it.isNotBlank() },
                            balance = balance.toDoubleOrNull() ?: 0.0
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
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}