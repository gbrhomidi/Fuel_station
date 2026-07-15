package com.aistudio.dieselstationsms.kxmpzq.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aistudio.dieselstationsms.kxmpzq.data.model.Notification
import com.aistudio.dieselstationsms.kxmpzq.ui.components.AppTopBar
import com.aistudio.dieselstationsms.kxmpzq.ui.components.EmptyState
import com.aistudio.dieselstationsms.kxmpzq.ui.components.ErrorMessage
import com.aistudio.dieselstationsms.kxmpzq.ui.components.LoadingIndicator
import com.aistudio.dieselstationsms.kxmpzq.ui.state.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    viewModel: com.aistudio.dieselstationsms.kxmpzq.ui.viewmodel.DashboardViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // استخدام DashboardViewModel مؤقتاً — يمكن إنشاء NotificationsViewModel لاحقاً
    Scaffold(
        topBar = { AppTopBar(title = "الإشعارات", onBackClick = onNavigateBack) }
    ) { padding ->
        EmptyState(
            message = "سيتم ربط الإشعارات لاحقاً",
            modifier = Modifier.padding(padding)
        )
    }
}