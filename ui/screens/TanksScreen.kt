package com.aistudio.dieselstationsms.kxmpzq.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.aistudio.dieselstationsms.kxmpzq.data.model.FuelType
import com.aistudio.dieselstationsms.kxmpzq.data.model.Pump
import com.aistudio.dieselstationsms.kxmpzq.data.model.Tank
import com.aistudio.dieselstationsms.kxmpzq.ui.components.AppTopBar
import com.aistudio.dieselstationsms.kxmpzq.ui.components.EmptyState
import com.aistudio.dieselstationsms.kxmpzq.ui.components.ErrorMessage
import com.aistudio.dieselstationsms.kxmpzq.ui.components.LoadingIndicator
import com.aistudio.dieselstationsms.kxmpzq.ui.state.ActionState
import com.aistudio.dieselstationsms.kxmpzq.ui.state.UiState
import com.aistudio.dieselstationsms.kxmpzq.ui.viewmodel.TanksViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TanksScreen(
    viewModel: TanksViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tanksState by viewModel.tanksState.collectAsState()
    val pumpsState by viewModel.pumpsState.collectAsState()
    val actionState by viewModel.actionState.collectAsState()
    val fuelTypes by viewModel.fuelTypes.collectAsState()
    var editingTank by remember { mutableStateOf<Tank?>(null) }

    LaunchedEffect(actionState) {
        if (actionState is ActionState.Success) {
            editingTank = null
            viewModel.resetActionState()
        }
    }

    Scaffold(
        topBar = { AppTopBar(title = "الخزانات والمضخات", onBackClick = onNavigateBack) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tanks Section
            item {
                Text("الخزانات", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }
            when (val state = tanksState) {
                is UiState.Loading -> item { LoadingIndicator() }
                is UiState.Error -> item { ErrorMessage(message = state.message, onRetry = { viewModel.loadTanks() }) }
                is UiState.Success -> {
                    if (state.data.isEmpty()) {
                        item { EmptyState(message = "لا توجد خزانات") }
                    } else {
                        items(state.data) { tank ->
                            TankCard(
                                tank = tank,
                                fuelTypes = fuelTypes,
                                onEdit = { editingTank = tank }
                            )
                        }
                    }
                }
                else -> { }
            }

            // Pumps Section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text("المضخات", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }
            when (val state = pumpsState) {
                is UiState.Loading -> item { LoadingIndicator() }
                is UiState.Error -> item { ErrorMessage(message = state.message, onRetry = { viewModel.loadPumps() }) }
                is UiState.Success -> {
                    if (state.data.isEmpty()) {
                        item { EmptyState(message = "لا توجد مضخات") }
                    } else {
                        items(state.data) { pump ->
                            PumpCard(pump = pump, fuelTypes = fuelTypes)
                        }
                    }
                }
                else -> { }
            }
        }
    }

    if (editingTank != null) {
        TankEditDialog(
            tank = editingTank!!,
            onDismiss = { editingTank = null },
            onConfirm = { tankId, quantity ->
                viewModel.updateTankQuantity(tankId, quantity)
            },
            actionState = actionState
        )
    }
}

@Composable
private fun TankCard(
    tank: Tank,
    fuelTypes: List<FuelType>,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fuelName = fuelTypes.find { it.id == tank.fuelTypeId }?.name ?: "—"
    val fillPercent = if (tank.capacity > 0) (tank.currentQuantity / tank.capacity * 100).toFloat() else 0f
    val isLow = fillPercent < 20f

    Card(modifier = modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = tank.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = "الوقود: $fuelName", style = MaterialTheme.typography.bodyMedium)
                }
                IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, contentDescription = "تعديل") }
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { fillPercent / 100f },
                modifier = Modifier.fillMaxWidth().height(12.dp),
                color = if (isLow) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "%.1f / %.1f لتر (%.0f%%)".format(tank.currentQuantity, tank.capacity, fillPercent),
                style = MaterialTheme.typography.bodySmall,
                color = if (isLow) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PumpCard(
    pump: Pump,
    fuelTypes: List<FuelType>,
    modifier: Modifier = Modifier
) {
    val fuelName = fuelTypes.find { it.id == pump.fuelTypeId }?.name ?: "—"
    Card(modifier = modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = pump.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(text = "الوقود: $fuelName | الحالة: ${pump.status}", style = MaterialTheme.typography.bodySmall)
            }
            Text(
                text = "%.0f لتر".format(pump.totalDispensed),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun TankEditDialog(
    tank: Tank,
    onDismiss: () -> Unit,
    onConfirm: (Int, Double) -> Unit,
    actionState: ActionState
) {
    var quantity by remember { mutableStateOf(tank.currentQuantity.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تحديث كمية ${tank.name}") },
        text = {
            Column {
                Text("السعة: %.1f لتر".format(tank.capacity), style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("الكمية الحالية") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (actionState is ActionState.Error) {
                    Text(text = actionState.message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(tank.id, quantity.toDoubleOrNull() ?: tank.currentQuantity) },
                enabled = actionState !is ActionState.Loading
            ) {
                if (actionState is ActionState.Loading) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                else Text("تحديث")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء") } }
    )
}