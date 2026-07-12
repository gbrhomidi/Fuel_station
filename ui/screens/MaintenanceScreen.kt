package com.aistudio.dieselstationsms.kxmpzq.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aistudio.dieselstationsms.kxmpzq.data.model.MaintenanceRequest
import com.aistudio.dieselstationsms.kxmpzq.ui.components.AppTopBar
import com.aistudio.dieselstationsms.kxmpzq.ui.components.EmptyState
import com.aistudio.dieselstationsms.kxmpzq.ui.components.ErrorMessage
import com.aistudio.dieselstationsms.kxmpzq.ui.components.LoadingIndicator
import com.aistudio.dieselstationsms.kxmpzq.ui.state.ActionState
import com.aistudio.dieselstationsms.kxmpzq.ui.state.UiState
import com.aistudio.dieselstationsms.kxmpzq.ui.viewmodel.MaintenanceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaintenanceScreen(
    viewModel: MaintenanceViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val requestsState by viewModel.requestsState.collectAsState()
    val actionState by viewModel.actionState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(actionState) {
        if (actionState is ActionState.Success) {
            showAddDialog = false
            viewModel.resetActionState()
        }
    }

    Scaffold(
        topBar = { AppTopBar(title = "طلبات الصيانة", onBackClick = onNavigateBack) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "إضافة طلب")
            }
        }
    ) { padding ->
        when (val state = requestsState) {
            is UiState.Loading -> LoadingIndicator(modifier = Modifier.padding(padding))
            is UiState.Error -> ErrorMessage(message = state.message, onRetry = { viewModel.loadRequests() }, modifier = Modifier.padding(padding))
            is UiState.Success -> {
                if (state.data.isEmpty()) {
                    EmptyState(message = "لا توجد طلبات صيانة", modifier = Modifier.padding(padding))
                } else {
                    LazyColumn(
                        modifier = Modifier.padding(padding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.data) { request ->
                            MaintenanceCard(
                                request = request,
                                onStatusChange = { status -> viewModel.updateStatus(request.id, status) },
                                onDelete = { viewModel.deleteRequest(request.id) }
                            )
                        }
                    }
                }
            }
            else -> { }
        }
    }

    if (showAddDialog) {
        MaintenanceDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { request -> viewModel.addRequest(request) },
            actionState = actionState
        )
    }
}

@Composable
private fun MaintenanceCard(
    request: MaintenanceRequest,
    onStatusChange: (String) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusColor = when (request.status) {
        "completed" -> MaterialTheme.colorScheme.primary
        "in_progress" -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.error
    }
    val statusText = when (request.status) {
        "completed" -> "✅ مكتمل"
        "in_progress" -> "🔄 قيد التنفيذ"
        else -> "⏳ معلق"
    }

    Card(modifier = modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = request.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                AssistChip(
                    onClick = { },
                    label = { Text(statusText, style = MaterialTheme.typography.labelSmall) },
                    colors = AssistChipDefaults.assistChipColors(containerColor = statusColor.copy(alpha = 0.15f), labelColor = statusColor)
                )
            }
            if (!request.description.isNullOrBlank()) {
                Text(text = request.description, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 4.dp))
            }
            Text(text = "الأولوية: ${request.priority} | ${request.createdAt ?: ""}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (request.status != "completed") {
                    TextButton(onClick = { onStatusChange("in_progress") }) { Text("بدء التنفيذ") }
                    TextButton(onClick = { onStatusChange("completed") }) { Text("إكمال") }
                }
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error) }
            }
        }
    }
}

@Composable
private fun MaintenanceDialog(
    onDismiss: () -> Unit,
    onConfirm: (MaintenanceRequest) -> Unit,
    actionState: ActionState
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("medium") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("طلب صيانة جديد") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("العنوان *") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("الوصف") }, modifier = Modifier.fillMaxWidth())
                var priorityExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = priorityExpanded, onExpandedChange = { priorityExpanded = it }) {
                    OutlinedTextField(
                        value = when (priority) { "high" -> "🔴 عالية"; "medium" -> "🟡 متوسطة"; else -> "🟢 منخفضة" },
                        onValueChange = {}, readOnly = true, label = { Text("الأولوية") },
                        modifier = Modifier.menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = priorityExpanded) }
                    )
                    ExposedDropdownMenu(expanded = priorityExpanded, onDismissRequest = { priorityExpanded = false }) {
                        listOf("high" to "🔴 عالية", "medium" to "🟡 متوسطة", "low" to "🟢 منخفضة").forEach { (value, label) ->
                            DropdownMenuItem(text = { Text(label) }, onClick = { priority = value; priorityExpanded = false })
                        }
                    }
                }
                if (actionState is ActionState.Error) {
                    Text(text = actionState.message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(MaintenanceRequest(title = title, description = description.takeIf { it.isNotBlank() }, priority = priority)) },
                enabled = actionState !is ActionState.Loading && title.isNotBlank()
            ) {
                if (actionState is ActionState.Loading) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                else Text("إرسال")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء") } }
    )
}