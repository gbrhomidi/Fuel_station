package com.aistudio.dieselstationsms.kxmpzq.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aistudio.dieselstationsms.kxmpzq.ui.components.AppTopBar
import com.aistudio.dieselstationsms.kxmpzq.ui.components.ErrorMessage
import com.aistudio.dieselstationsms.kxmpzq.ui.components.LoadingIndicator
import com.aistudio.dieselstationsms.kxmpzq.ui.state.ActionState
import com.aistudio.dieselstationsms.kxmpzq.ui.state.UiState
import com.aistudio.dieselstationsms.kxmpzq.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val settingsState by viewModel.settingsState.collectAsState()
    val actionState by viewModel.actionState.collectAsState()
    val geminiKey by viewModel.geminiKey.collectAsState()
    val deepseekKey by viewModel.deepseekKey.collectAsState()
    val chatgptKey by viewModel.chatgptKey.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadSettings() }

    Scaffold(
        topBar = { AppTopBar(title = "الإعدادات", onBackClick = onNavigateBack) }
    ) { padding ->
        when (val state = settingsState) {
            is UiState.Loading -> LoadingIndicator(modifier = Modifier.padding(padding))
            is UiState.Error -> ErrorMessage(message = state.message, onRetry = { viewModel.loadSettings() }, modifier = Modifier.padding(padding))
            is UiState.Success -> {
                SettingsContent(
                    geminiKey = geminiKey,
                    deepseekKey = deepseekKey,
                    chatgptKey = chatgptKey,
                    onSaveApiKeys = { g, d, c -> viewModel.saveApiKeys(g, d, c) },
                    onLogout = onLogout,
                    actionState = actionState,
                    modifier = Modifier.padding(padding)
                )
            }
            else -> { }
        }
    }
}

@Composable
private fun SettingsContent(
    geminiKey: String,
    deepseekKey: String,
    chatgptKey: String,
    onSaveApiKeys: (String, String, String) -> Unit,
    onLogout: () -> Unit,
    actionState: ActionState,
    modifier: Modifier = Modifier
) {
    var gemini by remember { mutableStateOf(geminiKey) }
    var deepseek by remember { mutableStateOf(deepseekKey) }
    var chatgpt by remember { mutableStateOf(chatgptKey) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // API Keys Section
        Text("🔑 مفاتيح API", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = gemini,
                    onValueChange = { gemini = it },
                    label = { Text("Gemini API Key") },
                    leadingIcon = { Icon(Icons.Default.Key, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = deepseek,
                    onValueChange = { deepseek = it },
                    label = { Text("DeepSeek API Key") },
                    leadingIcon = { Icon(Icons.Default.Cloud, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = chatgpt,
                    onValueChange = { chatgpt = it },
                    label = { Text("ChatGPT API Key") },
                    leadingIcon = { Icon(Icons.Default.Cloud, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = { onSaveApiKeys(gemini, deepseek, chatgpt) },
                    enabled = actionState !is ActionState.Loading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (actionState is ActionState.Loading) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    else Text("حفظ المفاتيح")
                }
                if (actionState is ActionState.Success) {
                    Text(text = actionState.message, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodyMedium)
                }
                if (actionState is ActionState.Error) {
                    Text(text = actionState.message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        // Appearance
        Text("🎨 المظهر", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                ListItem(
                    headlineContent = { Text("الوضع الداكن") },
                    leadingContent = { Icon(Icons.Default.Palette, contentDescription = null) },
                    trailingContent = {
                        var darkMode by remember { mutableStateOf(false) }
                        Switch(checked = darkMode, onCheckedChange = { darkMode = it })
                    }
                )
            }
        }

        // Security
        Text("🔒 الأمان", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                ListItem(
                    headlineContent = { Text("المصادقة البيومترية") },
                    leadingContent = { Icon(Icons.Default.Security, contentDescription = null) },
                    trailingContent = {
                        var biometric by remember { mutableStateOf(false) }
                        Switch(checked = biometric, onCheckedChange = { biometric = it })
                    }
                )
            }
        }

        // Logout
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("تسجيل الخروج")
        }
    }
}