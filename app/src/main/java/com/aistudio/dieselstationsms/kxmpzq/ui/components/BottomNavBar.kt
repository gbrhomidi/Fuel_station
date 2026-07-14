package com.aistudio.dieselstationsms.kxmpzq.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow
import com.aistudio.dieselstationsms.kxmpzq.ui.navigation.Screen

@Composable
fun BottomNavBar(
    currentRoute: String,
    onNavigate: (Screen) -> Unit
) {
    NavigationBar {
        Screen.bottomNavItems.forEach { screen ->
            val selected = currentRoute == screen.route
            NavigationBarItem(
                icon = {
                    Text(
                        text = when (screen.icon) {
                            "dashboard" -> "📊"
                            "shopping_cart" -> "🛒"
                            "people" -> "👥"
                            "assessment" -> "📈"
                            "settings" -> "⚙️"
                            else -> "•"
                        }
                    )
                },
                label = {
                    Text(
                        text = screen.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                selected = selected,
                onClick = { if (!selected) onNavigate(screen) }
            )
        }
    }
}