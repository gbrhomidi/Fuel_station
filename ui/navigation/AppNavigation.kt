package com.aistudio.dieselstationsms.kxmpzq.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.aistudio.dieselstationsms.kxmpzq.di.AppModule
import com.aistudio.dieselstationsms.kxmpzq.ui.screens.*
import com.aistudio.dieselstationsms.kxmpzq.ui.viewmodel.StationViewModelFactory

/**
 * ═══════════════════════════════════════════════════════════════
 * AppNavigation — NavHost رئيسي للتطبيق
 * ═══════════════════════════════════════════════════════════════
 *
 * يحل محل: WebView + HTML + JavaScript + NanoHTTPD REST API
 * كل شاشة تعتمد على ViewModel + Repository مباشرة
 */
@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onLogout: () -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val repository = remember { AppModule.provideStationRepository(context) }
    val factory = remember { StationViewModelFactory(repository) }

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route,
        modifier = modifier
    ) {
        // ─── Login ───
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = viewModel(factory = factory),
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // ─── Dashboard ───
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                viewModel = viewModel(factory = factory),
                onNavigate = { screen -> navController.navigate(screen.route) }
            )
        }

        // ─── Sales ───
        composable(Screen.Sales.route) {
            SalesScreen(
                viewModel = viewModel(factory = factory),
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ─── Customers ───
        composable(Screen.Customers.route) {
            CustomersScreen(
                viewModel = viewModel(factory = factory),
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ─── Products ───
        composable(Screen.Products.route) {
            ProductsScreen(
                viewModel = viewModel(factory = factory),
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ─── Employees ───
        composable(Screen.Employees.route) {
            EmployeesScreen(
                viewModel = viewModel(factory = factory),
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ─── Tanks ───
        composable(Screen.Tanks.route) {
            TanksScreen(
                viewModel = viewModel(factory = factory),
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ─── Payments ───
        composable(Screen.Payments.route) {
            PaymentsScreen(
                viewModel = viewModel(factory = factory),
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ─── Shifts ───
        composable(Screen.Shifts.route) {
            ShiftsScreen(
                viewModel = viewModel(factory = factory),
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ─── Maintenance ───
        composable(Screen.Maintenance.route) {
            MaintenanceScreen(
                viewModel = viewModel(factory = factory),
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ─── SMS ───
        composable(Screen.Sms.route) {
            SmsScreen(
                viewModel = viewModel(factory = factory),
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ─── Reports ───
        composable(Screen.Reports.route) {
            ReportsScreen(
                viewModel = viewModel(factory = factory),
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ─── Settings ───
        composable(Screen.Settings.route) {
            SettingsScreen(
                viewModel = viewModel(factory = factory),
                onNavigateBack = { navController.popBackStack() },
                onLogout = {
                    onLogout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ─── AI Chat ───
        composable(Screen.AiChat.route) {
            AiChatScreen(
                viewModel = viewModel(factory = factory),
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ─── Users ───
        composable(Screen.Users.route) {
            UsersScreen(
                viewModel = viewModel(factory = factory),
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ─── Notifications ───
        composable(Screen.Notifications.route) {
            NotificationsScreen(
                viewModel = viewModel(factory = factory),
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}