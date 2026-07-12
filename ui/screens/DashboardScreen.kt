package com.aistudio.dieselstationsms.kxmpzq.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aistudio.dieselstationsms.kxmpzq.data.model.DashboardData
import com.aistudio.dieselstationsms.kxmpzq.ui.components.AppTopBar
import com.aistudio.dieselstationsms.kxmpzq.ui.components.EmptyState
import com.aistudio.dieselstationsms.kxmpzq.ui.components.ErrorMessage
import com.aistudio.dieselstationsms.kxmpzq.ui.components.LoadingIndicator
import com.aistudio.dieselstationsms.kxmpzq.ui.navigation.Screen
import com.aistudio.dieselstationsms.kxmpzq.ui.state.UiState
import com.aistudio.dieselstationsms.kxmpzq.ui.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val dashboardState by viewModel.dashboardState.collectAsState()
    val dailySalesState by viewModel.dailySalesState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "لوحة التحكم",
                onMenuClick = { /* Drawer handled by parent */ },
                onNotificationsClick = { onNavigate(Screen.Notifications) },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }, enabled = !isRefreshing) {
                        if (isRefreshing) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = "تحديث")
                        }
                    }
                }
            )
        }
    ) { padding ->
        when (val state = dashboardState) {
            is UiState.Loading -> {
                LoadingIndicator(modifier = Modifier.padding(padding))
            }
            is UiState.Error -> {
                ErrorMessage(
                    message = state.message,
                    onRetry = { viewModel.loadDashboard() },
                    modifier = Modifier.padding(padding)
                )
            }
            is UiState.Success -> {
                DashboardContent(
                    data = state.data,
                    dailySalesState = dailySalesState,
                    onNavigate = onNavigate,
                    modifier = Modifier.padding(padding)
                )
            }
            else -> { /* Idle */ }
        }
    }
}

@Composable
private fun DashboardContent(
    data: DashboardData,
    dailySalesState: UiState<List<com.aistudio.dieselstationsms.kxmpzq.data.model.DailySales>>,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Stats Grid
        item {
            StatsGrid(data = data, onNavigate = onNavigate)
        }

        // Quick Actions
        item {
            QuickActions(onNavigate = onNavigate)
        }

        // Daily Sales Chart placeholder
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "المبيعات اليومية (آخر 30 يوم)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    when (dailySalesState) {
                        is UiState.Success -> {
                            if (dailySalesState.data.isEmpty()) {
                                EmptyState(message = "لا توجد مبيعات مسجلة")
                            } else {
                                DailySalesBars(dailySalesState.data.take(7))
                            }
                        }
                        is UiState.Loading -> {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                        }
                        else -> { /* */ }
                    }
                }
            }
        }

        // Alerts
        if (data.lowStockItems > 0 || data.pendingMaintenance > 0) {
            item {
                AlertsCard(data = data)
            }
        }
    }
}

@Composable
private fun StatsGrid(
    data: DashboardData,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard(
                title = "إيرادات اليوم",
                value = "%.2f".format(data.todayRevenue),
                icon = "💰",
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "العمليات",
                value = data.totalTransactions.toString(),
                icon = "📋",
                modifier = Modifier.weight(1f)
            )
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard(
                title = "العملاء",
                value = data.totalCustomers.toString(),
                icon = "👥",
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "الوقود",
                value = "%.0f".format(data.totalFuelDispensed) + " لتر",
                icon = "⛽",
                modifier = Modifier.weight(1f)
            )
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard(
                title = "الورديات النشطة",
                value = data.activeShifts.toString(),
                icon = "📅",
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "إيرادات الشهر",
                value = "%.2f".format(data.monthlyRevenue),
                icon = "📈",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = icon, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun QuickActions(
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "وصول سريع",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickActionButton("🛒", "بيع جديد") { onNavigate(Screen.Sales) }
                QuickActionButton("👤", "عميل جديد") { onNavigate(Screen.Customers) }
                QuickActionButton("💳", "دفع") { onNavigate(Screen.Payments) }
                QuickActionButton("📅", "وردية") { onNavigate(Screen.Shifts) }
            }
        }
    }
}

@Composable
private fun QuickActionButton(icon: String, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FilledIconButton(onClick = onClick) {
            Text(icon, style = MaterialTheme.typography.titleLarge)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun DailySalesBars(
    data: List<com.aistudio.dieselstationsms.kxmpzq.data.model.DailySales>,
    modifier: Modifier = Modifier
) {
    val maxValue = data.maxOfOrNull { it.totalAmount }?.takeIf { it > 0 } ?: 1.0
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        data.reversed().forEach { day ->
            val heightFraction = (day.totalAmount / maxValue).toFloat().coerceIn(0.05f, 1f)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "%.0f".format(day.totalAmount),
                    style = MaterialTheme.typography.labelSmall
                )
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .fillMaxHeight(heightFraction)
                        .padding(horizontal = 2.dp)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.fillMaxSize()
                    ) { }
                }
                Text(
                    text = day.date.takeLast(2),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
private fun AlertsCard(data: DashboardData, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "⚠️ تنبيهات",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (data.lowStockItems > 0) {
                Text(
                    text = "• $data.lowStockItems منتجات بمخزون منخفض",
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
            if (data.pendingMaintenance > 0) {
                Text(
                    text = "• $data.pendingMaintenance طلبات صيانة معلقة",
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}