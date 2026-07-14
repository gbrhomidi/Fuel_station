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
import com.aistudio.dieselstationsms.kxmpzq.ui.components.AppTopBar
import com.aistudio.dieselstationsms.kxmpzq.ui.components.EmptyState
import com.aistudio.dieselstationsms.kxmpzq.ui.components.ErrorMessage
import com.aistudio.dieselstationsms.kxmpzq.ui.components.LoadingIndicator
import com.aistudio.dieselstationsms.kxmpzq.ui.state.UiState
import com.aistudio.dieselstationsms.kxmpzq.ui.viewmodel.ReportsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    viewModel: ReportsViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val eodState by viewModel.eodState.collectAsState()
    val monthlySalesState by viewModel.monthlySalesState.collectAsState()
    val profitState by viewModel.profitState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "التقارير",
                onBackClick = onNavigateBack,
                actions = {
                    IconButton(onClick = {
                        when (selectedTab) {
                            0 -> viewModel.loadEodReport()
                            1 -> viewModel.loadMonthlySales()
                            2 -> viewModel.loadProfitReport("2024-01-01", "2024-12-31")
                        }
                    }) { Icon(Icons.Default.Refresh, contentDescription = "تحديث") }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0; viewModel.loadEodReport() }, text = { Text("EOD") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1; viewModel.loadMonthlySales() }, text = { Text("شهري") })
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("أرباح") })
            }

            when (selectedTab) {
                0 -> EodTab(eodState)
                1 -> MonthlyTab(monthlySalesState)
                2 -> ProfitTab(profitState, isLoading, onLoad = { from, to -> viewModel.loadProfitReport(from, to) })
            }
        }
    }
}

@Composable
private fun EodTab(eodState: UiState<com.aistudio.dieselstationsms.kxmpzq.data.model.EodReport>) {
    when (val state = eodState) {
        is UiState.Loading -> LoadingIndicator()
        is UiState.Error -> ErrorMessage(message = state.message, onRetry = { })
        is UiState.Success -> {
            val report = state.data
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                item {
                    ReportCard(title = "تاريخ التقرير", value = report.date, icon = "📅")
                    ReportCard(title = "إجمالي المبيعات", value = "%.2f".format(report.totalSales), icon = "💰")
                    ReportCard(title = "إجمالي المدفوعات", value = "%.2f".format(report.totalPayments), icon = "💳")
                    ReportCard(title = "إجمالي الإيداعات", value = "%.2f".format(report.totalDeposits), icon = "🏦")
                    ReportCard(title = "الرصيد في الصندوق", value = "%.2f".format(report.cashInHand), icon = "💵")
                    ReportCard(title = "عدد الورديات", value = report.shiftCount.toString(), icon = "📊")
                    ReportCard(title = "عدد العمليات", value = report.transactionCount.toString(), icon = "📋")
                }
            }
        }
        else -> { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("اضغط تحديث لعرض التقرير") } }
    }
}

@Composable
private fun MonthlyTab(monthlySalesState: UiState<List<com.aistudio.dieselstationsms.kxmpzq.data.model.MonthlySales>>) {
    when (val state = monthlySalesState) {
        is UiState.Loading -> LoadingIndicator()
        is UiState.Error -> ErrorMessage(message = state.message, onRetry = { })
        is UiState.Success -> {
            if (state.data.isEmpty()) {
                EmptyState(message = "لا توجد بيانات شهرية")
            } else {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.data) { month ->
                        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                            Row(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = month.month, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(text = "%.2f".format(month.totalAmount), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                                    Text(text = "${month.transactionCount} عملية", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }
        else -> { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("اضغط تحديث لعرض البيانات") } }
    }
}

@Composable
private fun ProfitTab(
    profitState: UiState<com.aistudio.dieselstationsms.kxmpzq.data.model.ProfitReport>,
    isLoading: Boolean,
    onLoad: (String, String) -> Unit
) {
    var fromDate by remember { mutableStateOf("2024-01-01") }
    var toDate by remember { mutableStateOf("2024-12-31") }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(value = fromDate, onValueChange = { fromDate = it }, label = { Text("من") }, modifier = Modifier.weight(1f), singleLine = true)
            OutlinedTextField(value = toDate, onValueChange = { toDate = it }, label = { Text("إلى") }, modifier = Modifier.weight(1f), singleLine = true)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { onLoad(fromDate, toDate) },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            else Text("عرض التقرير")
        }
        Spacer(modifier = Modifier.height(16.dp))

        when (val state = profitState) {
            is UiState.Loading -> LoadingIndicator()
            is UiState.Error -> ErrorMessage(message = state.message)
            is UiState.Success -> {
                val report = state.data
                ReportCard(title = "إجمالي الإيرادات", value = "%.2f".format(report.totalRevenue), icon = "💰")
                ReportCard(title = "إجمالي التكلفة", value = "%.2f".format(report.totalCost), icon = "📉")
                ReportCard(title = "الربح الإجمالي", value = "%.2f".format(report.grossProfit), icon = "📈")
                ReportCard(title = "هامش الربح", value = "%.1f%%".format(report.profitMargin), icon = "📊")
            }
            else -> { }
        }
    }
}

@Composable
private fun ReportCard(title: String, value: String, icon: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth().padding(vertical = 4.dp), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "$icon $title", style = MaterialTheme.typography.bodyLarge)
            Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}