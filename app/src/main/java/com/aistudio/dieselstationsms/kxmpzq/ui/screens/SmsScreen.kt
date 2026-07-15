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
import com.aistudio.dieselstationsms.kxmpzq.data.model.SmsMessage
import com.aistudio.dieselstationsms.kxmpzq.data.model.SmsWhitelistEntry
import com.aistudio.dieselstationsms.kxmpzq.ui.components.AppTopBar
import com.aistudio.dieselstationsms.kxmpzq.ui.components.EmptyState
import com.aistudio.dieselstationsms.kxmpzq.ui.components.ErrorMessage
import com.aistudio.dieselstationsms.kxmpzq.ui.components.LoadingIndicator
import com.aistudio.dieselstationsms.kxmpzq.ui.state.ActionState
import com.aistudio.dieselstationsms.kxmpzq.ui.state.UiState
import com.aistudio.dieselstationsms.kxmpzq.ui.viewmodel.SmsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmsScreen(
    viewModel: SmsViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val smsLogsState by viewModel.smsLogsState.collectAsState()
    val whitelistState by viewModel.whitelistState.collectAsState()
    val actionState by viewModel.actionState.collectAsState()
    var showAddWhitelistDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(actionState) {
        if (actionState is ActionState.Success) {
            showAddWhitelistDialog = false
            viewModel.resetActionState()
        }
    }

    Scaffold(
        topBar = { AppTopBar(title = "الرسائل", onBackClick = onNavigateBack) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("السجلات") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("القائمة البيضاء") })
            }

            when (selectedTab) {
                0 -> SmsLogsTab(smsLogsState)
                1 -> WhitelistTab(
                    whitelistState = whitelistState,
                    onAdd = { showAddWhitelistDialog = true },
                    onRemove = { phone -> viewModel.removeWhitelist(phone) }
                )
            }
        }
    }

    if (showAddWhitelistDialog) {
        WhitelistDialog(
            onDismiss = { showAddWhitelistDialog = false },
            onConfirm = { entry -> viewModel.addWhitelist(entry) },
            actionState = actionState
        )
    }
}

@Composable
private fun SmsLogsTab(smsLogsState: UiState<List<SmsMessage>>) {
    when (val state = smsLogsState) {
        is UiState.Loading -> LoadingIndicator()
        is UiState.Error -> ErrorMessage(message = state.message, onRetry = { })
        is UiState.Success -> {
            if (state.data.isEmpty()) {
                EmptyState(message = "لا توجد رسائل")
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.data) { sms ->
                        SmsCard(sms = sms)
                    }
                }
            }
        }
        else -> { }
    }
}

@Composable
private fun SmsCard(sms: SmsMessage, modifier: Modifier = Modifier) {
    val isIncoming = sms.messageType == "incoming"
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = if (isIncoming) CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)) else CardDefaults.cardColors()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${if (isIncoming) "📥" else "📤"} ${sms.phoneNumber}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = sms.status,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = sms.messageBody, style = MaterialTheme.typography.bodyMedium)
            Text(text = sms.createdAt ?: "", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun WhitelistTab(
    whitelistState: UiState<List<SmsWhitelistEntry>>,
    onAdd: () -> Unit,
    onRemove: (String) -> Unit
) {
    Column {
        Button(
            onClick = onAdd,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) { Text("➕ إضافة رقم للقائمة البيضاء") }

        when (val state = whitelistState) {
            is UiState.Loading -> LoadingIndicator()
            is UiState.Error -> ErrorMessage(message = state.message, onRetry = { })
            is UiState.Success -> {
                if (state.data.isEmpty()) {
                    EmptyState(message = "القائمة البيضاء فارغة")
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.data) { entry ->
                            WhitelistCard(entry = entry, onRemove = { onRemove(entry.phone) })
                        }
                    }
                }
            }
            else -> { }
        }
    }
}

@Composable
private fun WhitelistCard(
    entry: SmsWhitelistEntry,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = entry.name ?: "—", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(text = entry.phone, style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = onRemove) { Icon(Icons.Default.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error) }
        }
    }
}

@Composable
private fun WhitelistDialog(
    onDismiss: () -> Unit,
    onConfirm: (SmsWhitelistEntry) -> Unit,
    actionState: ActionState
) {
    var phone by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة للقائمة البيضاء") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("الرقم *") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("الاسم") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                if (actionState is ActionState.Error) {
                    Text(text = actionState.message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(SmsWhitelistEntry(phone = phone, name = name.takeIf { it.isNotBlank() })) },
                enabled = actionState !is ActionState.Loading && phone.isNotBlank()
            ) {
                if (actionState is ActionState.Loading) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                else Text("إضافة")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء") } }
    )
}