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
import com.aistudio.dieselstationsms.kxmpzq.data.model.Deposit
import com.aistudio.dieselstationsms.kxmpzq.data.model.Party
import com.aistudio.dieselstationsms.kxmpzq.data.model.Payment
import com.aistudio.dieselstationsms.kxmpzq.ui.components.AppTopBar
import com.aistudio.dieselstationsms.kxmpzq.ui.components.EmptyState
import com.aistudio.dieselstationsms.kxmpzq.ui.components.ErrorMessage
import com.aistudio.dieselstationsms.kxmpzq.ui.components.LoadingIndicator
import com.aistudio.dieselstationsms.kxmpzq.ui.state.ActionState
import com.aistudio.dieselstationsms.kxmpzq.ui.state.UiState
import com.aistudio.dieselstationsms.kxmpzq.ui.viewmodel.PaymentsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentsScreen(
    viewModel: PaymentsViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val paymentsState by viewModel.paymentsState.collectAsState()
    val actionState by viewModel.actionState.collectAsState()
    val customers by viewModel.customers.collectAsState()
    var showPaymentDialog by remember { mutableStateOf(false) }
    var showDepositDialog by remember { mutableStateOf(false) }

    LaunchedEffect(actionState) {
        if (actionState is ActionState.Success) {
            showPaymentDialog = false
            showDepositDialog = false
            viewModel.resetActionState()
        }
    }

    Scaffold(
        topBar = { AppTopBar(title = "المدفوعات", onBackClick = onNavigateBack) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { showPaymentDialog = true },
                    modifier = Modifier.weight(1f)
                ) { Text("💳 دفع جديد") }
                Button(
                    onClick = { showDepositDialog = true },
                    modifier = Modifier.weight(1f)
                ) { Text("💰 إيداع جديد") }
            }

            when (val state = paymentsState) {
                is UiState.Loading -> LoadingIndicator()
                is UiState.Error -> ErrorMessage(message = state.message, onRetry = { viewModel.loadPayments() })
                is UiState.Success -> {
                    if (state.data.isEmpty()) {
                        EmptyState(message = "لا توجد مدفوعات مسجلة")
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.data) { payment ->
                                PaymentCard(
                                    payment = payment,
                                    customers = customers,
                                    onDelete = { viewModel.deletePayment(payment.id) }
                                )
                            }
                        }
                    }
                }
                else -> { }
            }
        }
    }

    if (showPaymentDialog) {
        PaymentDialog(
            customers = customers,
            onDismiss = { showPaymentDialog = false },
            onConfirm = { payment -> viewModel.makePayment(payment) },
            actionState = actionState
        )
    }

    if (showDepositDialog) {
        DepositDialog(
            customers = customers,
            onDismiss = { showDepositDialog = false },
            onConfirm = { deposit -> viewModel.addDeposit(deposit) },
            actionState = actionState
        )
    }
}

@Composable
private fun PaymentCard(
    payment: Payment,
    customers: List<Party>,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val customerName = customers.find { it.id == payment.partyId }?.name ?: "—"
    Card(modifier = modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "💳 ${"%.2f".format(payment.amount)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = "العميل: $customerName", style = MaterialTheme.typography.bodyMedium)
                Text(text = "${payment.paymentMethod} | ${payment.createdAt ?: ""}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error) }
        }
    }
}

@Composable
private fun PaymentDialog(
    customers: List<Party>,
    onDismiss: () -> Unit,
    onConfirm: (Payment) -> Unit,
    actionState: ActionState
) {
    var selectedCustomerId by remember { mutableStateOf<Int?>(null) }
    var amount by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("cash") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("دفع جديد") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                    OutlinedTextField(
                        value = customers.find { it.id == selectedCustomerId }?.name ?: "اختر العميل",
                        onValueChange = {}, readOnly = true, label = { Text("العميل") },
                        modifier = Modifier.menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        customers.forEach { customer ->
                            DropdownMenuItem(text = { Text(customer.name) }, onClick = { selectedCustomerId = customer.id; expanded = false })
                        }
                    }
                }
                OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("المبلغ") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true, modifier = Modifier.fillMaxWidth())
                if (actionState is ActionState.Error) {
                    Text(text = actionState.message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(Payment(partyId = selectedCustomerId, amount = amount.toDoubleOrNull() ?: 0.0, paymentMethod = paymentMethod))
                },
                enabled = actionState !is ActionState.Loading && selectedCustomerId != null && amount.isNotBlank()
            ) {
                if (actionState is ActionState.Loading) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                else Text("تسجيل الدفع")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء") } }
    )
}

@Composable
private fun DepositDialog(
    customers: List<Party>,
    onDismiss: () -> Unit,
    onConfirm: (Deposit) -> Unit,
    actionState: ActionState
) {
    var selectedCustomerId by remember { mutableStateOf<Int?>(null) }
    var amount by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إيداع جديد") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                    OutlinedTextField(
                        value = customers.find { it.id == selectedCustomerId }?.name ?: "اختر العميل",
                        onValueChange = {}, readOnly = true, label = { Text("العميل") },
                        modifier = Modifier.menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        customers.forEach { customer ->
                            DropdownMenuItem(text = { Text(customer.name) }, onClick = { selectedCustomerId = customer.id; expanded = false })
                        }
                    }
                }
                OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("المبلغ") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true, modifier = Modifier.fillMaxWidth())
                if (actionState is ActionState.Error) {
                    Text(text = actionState.message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(Deposit(partyId = selectedCustomerId, amount = amount.toDoubleOrNull() ?: 0.0)) },
                enabled = actionState !is ActionState.Loading && selectedCustomerId != null && amount.isNotBlank()
            ) {
                if (actionState is ActionState.Loading) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                else Text("تسجيل الإيداع")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء") } }
    )
}