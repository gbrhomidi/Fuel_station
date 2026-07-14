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
import com.aistudio.dieselstationsms.kxmpzq.data.model.Employee
import com.aistudio.dieselstationsms.kxmpzq.data.model.Shift
import com.aistudio.dieselstationsms.kxmpzq.ui.components.AppTopBar
import com.aistudio.dieselstationsms.kxmpzq.ui.components.EmptyState
import com.aistudio.dieselstationsms.kxmpzq.ui.components.ErrorMessage
import com.aistudio.dieselstationsms.kxmpzq.ui.components.LoadingIndicator
import com.aistudio.dieselstationsms.kxmpzq.ui.state.ActionState
import com.aistudio.dieselstationsms.kxmpzq.ui.state.UiState
import com.aistudio.dieselstationsms.kxmpzq.ui.viewmodel.ShiftsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShiftsScreen(
    viewModel: ShiftsViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shiftsState by viewModel.shiftsState.collectAsState()
    val actionState by viewModel.actionState.collectAsState()
    val cashiers by viewModel.cashiers.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(actionState) {
        if (actionState is ActionState.Success) {
            showAddDialog = false
            viewModel.resetActionState()
        }
    }

    Scaffold(
        topBar = { AppTopBar(title = "الورديات", onBackClick = onNavigateBack) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "إضافة وردية")
            }
        }
    ) { padding ->
        when (val state = shiftsState) {
            is UiState.Loading -> LoadingIndicator(modifier = Modifier.padding(padding))
            is UiState.Error -> ErrorMessage(message = state.message, onRetry = { viewModel.loadShifts() }, modifier = Modifier.padding(padding))
            is UiState.Success -> {
                if (state.data.isEmpty()) {
                    EmptyState(message = "لا توجد ورديات مسجلة", modifier = Modifier.padding(padding))
                } else {
                    LazyColumn(
                        modifier = Modifier.padding(padding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.data) { shift ->
                            ShiftCard(
                                shift = shift,
                                cashiers = cashiers,
                                onDelete = { viewModel.deleteShift(shift.id) }
                            )
                        }
                    }
                }
            }
            else -> { }
        }
    }

    if (showAddDialog) {
        ShiftDialog(
            cashiers = cashiers,
            onDismiss = { showAddDialog = false },
            onConfirm = { shift -> viewModel.addShift(shift) },
            actionState = actionState
        )
    }
}

@Composable
private fun ShiftCard(
    shift: Shift,
    cashiers: List<Employee>,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cashierName = cashiers.find { it.id == shift.cashierId }?.fullName ?: "—"
    val isOpen = shift.status == "open"
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = if (isOpen) CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)) else CardDefaults.cardColors()
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "وردية #${shift.id}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = "الكاشير: $cashierName", style = MaterialTheme.typography.bodyMedium)
                Text(text = "الحالة: ${if (isOpen) "🟢 مفتوحة" else "🔴 مغلقة"} | المبيعات: %.2f".format(shift.totalSales), style = MaterialTheme.typography.bodySmall)
                Text(text = "${shift.startTime ?: ""} → ${shift.endTime ?: ""}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error) }
        }
    }
}

@Composable
private fun ShiftDialog(
    cashiers: List<Employee>,
    onDismiss: () -> Unit,
    onConfirm: (Shift) -> Unit,
    actionState: ActionState
) {
    var selectedCashierId by remember { mutableStateOf<Int?>(null) }
    var startingCash by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("وردية جديدة") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                    OutlinedTextField(
                        value = cashiers.find { it.id == selectedCashierId }?.fullName ?: "اختر الكاشير",
                        onValueChange = {}, readOnly = true, label = { Text("الكاشير *") },
                        modifier = Modifier.menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        cashiers.forEach { cashier ->
                            DropdownMenuItem(text = { Text(cashier.fullName) }, onClick = { selectedCashierId = cashier.id; expanded = false })
                        }
                    }
                }
                OutlinedTextField(value = startingCash, onValueChange = { startingCash = it }, label = { Text("الرصيد الافتتاحي") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true, modifier = Modifier.fillMaxWidth())
                if (actionState is ActionState.Error) {
                    Text(text = actionState.message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(Shift(
                        cashierId = selectedCashierId,
                        startingCash = startingCash.toDoubleOrNull() ?: 0.0,
                        status = "open",
                        startTime = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
                    ))
                },
                enabled = actionState !is ActionState.Loading && selectedCashierId != null
            ) {
                if (actionState is ActionState.Loading) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                else Text("بدء الوردية")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء") } }
    )
}