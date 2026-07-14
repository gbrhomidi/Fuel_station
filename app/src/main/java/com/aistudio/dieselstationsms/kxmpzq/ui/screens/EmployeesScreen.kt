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
import com.aistudio.dieselstationsms.kxmpzq.data.model.Employee
import com.aistudio.dieselstationsms.kxmpzq.ui.components.AppTopBar
import com.aistudio.dieselstationsms.kxmpzq.ui.components.EmptyState
import com.aistudio.dieselstationsms.kxmpzq.ui.components.ErrorMessage
import com.aistudio.dieselstationsms.kxmpzq.ui.components.LoadingIndicator
import com.aistudio.dieselstationsms.kxmpzq.ui.state.ActionState
import com.aistudio.dieselstationsms.kxmpzq.ui.state.UiState
import com.aistudio.dieselstationsms.kxmpzq.ui.viewmodel.EmployeesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeesScreen(
    viewModel: EmployeesViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val employeesState by viewModel.employeesState.collectAsState()
    val actionState by viewModel.actionState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingEmployee by remember { mutableStateOf<Employee?>(null) }

    LaunchedEffect(actionState) {
        if (actionState is ActionState.Success) {
            showAddDialog = false
            editingEmployee = null
            viewModel.resetActionState()
        }
    }

    Scaffold(
        topBar = { AppTopBar(title = "الموظفين", onBackClick = onNavigateBack) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "إضافة موظف")
            }
        }
    ) { padding ->
        when (val state = employeesState) {
            is UiState.Loading -> LoadingIndicator(modifier = Modifier.padding(padding))
            is UiState.Error -> ErrorMessage(message = state.message, onRetry = { viewModel.loadEmployees() }, modifier = Modifier.padding(padding))
            is UiState.Success -> {
                if (state.data.isEmpty()) {
                    EmptyState(message = "لا يوجد موظفين", modifier = Modifier.padding(padding))
                } else {
                    LazyColumn(
                        modifier = Modifier.padding(padding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.data) { employee ->
                            EmployeeCard(
                                employee = employee,
                                onEdit = { editingEmployee = employee },
                                onDelete = { viewModel.deleteEmployee(employee.id) }
                            )
                        }
                    }
                }
            }
            else -> { }
        }
    }

    if (showAddDialog || editingEmployee != null) {
        EmployeeDialog(
            employee = editingEmployee,
            onDismiss = { showAddDialog = false; editingEmployee = null },
            onConfirm = { employee ->
                if (editingEmployee != null) viewModel.updateEmployee(employee)
                else viewModel.addEmployee(employee)
            },
            actionState = actionState
        )
    }
}

@Composable
private fun EmployeeCard(
    employee: Employee,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = employee.fullName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = "${employee.position ?: "—"} | 📞 ${employee.phone ?: "—"}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "الراتب: %.2f | 📅 ${employee.hireDate ?: "—"}".format(employee.salary), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Row {
                IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, contentDescription = "تعديل") }
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error) }
            }
        }
    }
}

@Composable
private fun EmployeeDialog(
    employee: Employee?,
    onDismiss: () -> Unit,
    onConfirm: (Employee) -> Unit,
    actionState: ActionState
) {
    var fullName by remember { mutableStateOf(employee?.fullName ?: "") }
    var phone by remember { mutableStateOf(employee?.phone ?: "") }
    var position by remember { mutableStateOf(employee?.position ?: "") }
    var salary by remember { mutableStateOf(employee?.salary?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (employee != null) "تعديل موظف" else "موظف جديد") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = fullName, onValueChange = { fullName = it }, label = { Text("الاسم الكامل *") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("الهاتف") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = position, onValueChange = { position = it }, label = { Text("المنصب") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = salary, onValueChange = { salary = it }, label = { Text("الراتب") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true, modifier = Modifier.fillMaxWidth())
                if (actionState is ActionState.Error) {
                    Text(text = actionState.message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(Employee(
                        id = employee?.id ?: 0,
                        fullName = fullName,
                        phone = phone.takeIf { it.isNotBlank() },
                        position = position.takeIf { it.isNotBlank() },
                        salary = salary.toDoubleOrNull() ?: 0.0
                    ))
                },
                enabled = actionState !is ActionState.Loading && fullName.isNotBlank()
            ) {
                if (actionState is ActionState.Loading) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                else Text("حفظ")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء") } }
    )
}