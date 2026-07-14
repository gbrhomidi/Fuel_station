package com.aistudio.dieselstationsms.kxmpzq.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aistudio.dieselstationsms.kxmpzq.ui.navigation.Screen

@Composable
fun AppDrawer(
    currentRoute: String,
    onNavigate: (Screen) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(modifier = modifier) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "⛽ محطة أبو أحمد",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "النظام المتكامل V6",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        HorizontalDivider()

        // Navigation Items
        Screen.drawerItems.forEach { screen ->
            val selected = currentRoute == screen.route
            NavigationDrawerItem(
                label = { Text(screen.title) },
                selected = selected,
                onClick = {
                    onNavigate(screen)
                    onClose()
                },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                icon = {
                    // استخدام Material Icons بناءً على اسم الأيقونة
                    val iconName = screen.icon ?: "dashboard"
                    Text(
                        text = when (iconName) {
                            "dashboard" -> "📊"
                            "shopping_cart" -> "🛒"
                            "people" -> "👥"
                            "inventory_2" -> "📦"
                            "badge" -> "🪪"
                            "local_gas_station" -> "⛽"
                            "payments" -> "💳"
                            "schedule" -> "📅"
                            "build" -> "🔧"
                            "sms" -> "💬"
                            "assessment" -> "📈"
                            "settings" -> "⚙️"
                            "psychology" -> "🤖"
                            "manage_accounts" -> "👤"
                            else -> "•"
                        },
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    }
}