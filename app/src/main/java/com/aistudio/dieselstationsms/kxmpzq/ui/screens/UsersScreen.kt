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
import com.aistudio.dieselstationsms.kxmpzq.data.model.Role
import com.aistudio.dieselstationsms.kxmpzq.data.model.User
import com.aistudio.dieselstationsms.kxmpzq.ui.components.AppTopBar
import com.aistudio.dieselstationsms.kxmpzq.ui.components.EmptyState
import com.aistudio.dieselstationsms.kxmpzq.ui.components.ErrorMessage
import com.aistudio.dieselstationsms.kxmpzq.ui.components.LoadingIndicator
import com.aistudio.dieselstationsms.kxmpzq.ui.state.ActionState
import com.aistudio.dieselstationsms.kxmpzq.ui.state.UiState
import com.aistudio.dieselstationsms.kxmpzq.ui.viewmodel.UsersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersScreen(
    viewModel: UsersViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val usersState by viewModel.usersState.collectAsState()
    val rolesState by viewModel.rolesState.collectAsState()
    val actionState by viewModel.actionState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = { AppTopBar(title = "المستخدمين والأدوار", onBackClick = onNavigateBack) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("المستخدمين") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("الأدوار") })
            }

            when (selectedTab) {
                0 -> UsersTab(usersState, actionState, onDelete = { viewModel.deleteUser(it) })
                1 -> RolesTab(rolesState, actionState, onDelete = { viewModel.deleteRole(it) })
            }
        }
    }
}

@Composable
private fun UsersTab(
    usersState: UiState<List<User>>,
    actionState: ActionState,
    onDelete: (Int) -> Unit
) {
    when (val state = usersState) {
        is UiState.Loading -> LoadingIndicator()
        is UiState.Error -> ErrorMessage(message = state.message, onRetry = { })
        is UiState.Success -> {
            if (state.data.isEmpty()) {
                EmptyState(message = "لا يوجد مستخدمين")
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.data) { user ->
                        UserCard(user = user, onDelete = { onDelete(user.id) })
                    }
                }
            }
        }
        else -> { }
    }
}

@Composable
private fun UserCard(user: User, onDelete: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = user.username, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = user.fullName ?: "—", style = MaterialTheme.typography.bodyMedium)
                Text(text = "📞 ${user.phone ?: "—"} | 📧 ${user.email ?: "—"}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error) }
        }
    }
}

@Composable
private fun RolesTab(
    rolesState: UiState<List<Role>>,
    actionState: ActionState,
    onDelete: (Int) -> Unit
) {
    when (val state = rolesState) {
        is UiState.Loading -> LoadingIndicator()
        is UiState.Error -> ErrorMessage(message = state.message, onRetry = { })
        is UiState.Success -> {
            if (state.data.isEmpty()) {
                EmptyState(message = "لا توجد أدوار")
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.data) { role ->
                        RoleCard(role = role, onDelete = { onDelete(role.id) })
                    }
                }
            }
        }
        else -> { }
    }
}

@Composable
private fun RoleCard(role: Role, onDelete: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = role.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                if (!role.description.isNullOrBlank()) {
                    Text(text = role.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error) }
        }
    }
}