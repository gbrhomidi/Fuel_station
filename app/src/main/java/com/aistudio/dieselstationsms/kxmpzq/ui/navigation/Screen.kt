package com.aistudio.dieselstationsms.kxmpzq.ui.navigation

/**
 * ═══════════════════════════════════════════════════════════════
 * Screen Routes — تعريف جميع شاشات التطبيق
 * ═══════════════════════════════════════════════════════════════
 *
 * يتوافق مع جميع الصفحات التي كانت في web_interface.html:
 * login, dashboard, sales, customers, products, employees,
 * tanks, payments, shifts, maintenance, sms, reports,
 * settings, ai_chat, users, notifications
 */
sealed class Screen(val route: String, val title: String, val icon: String? = null) {

    data object Login : Screen("login", "تسجيل الدخول")
    data object Dashboard : Screen("dashboard", "لوحة التحكم", "dashboard")
    data object Sales : Screen("sales", "المبيعات", "shopping_cart")
    data object Customers : Screen("customers", "العملاء", "people")
    data object Products : Screen("products", "المنتجات", "inventory_2")
    data object Employees : Screen("employees", "الموظفين", "badge")
    data object Tanks : Screen("tanks", "الخزانات", "local_gas_station")
    data object Payments : Screen("payments", "المدفوعات", "payments")
    data object Shifts : Screen("shifts", "الورديات", "schedule")
    data object Maintenance : Screen("maintenance", "الصيانة", "build")
    data object Sms : Screen("sms", "الرسائل", "sms")
    data object Reports : Screen("reports", "التقارير", "assessment")
    data object Settings : Screen("settings", "الإعدادات", "settings")
    data object AiChat : Screen("ai_chat", "AI مساعد", "psychology")
    data object Users : Screen("users", "المستخدمين", "manage_accounts")
    data object Notifications : Screen("notifications", "الإشعارات", "notifications")

    companion object {
        val bottomNavItems = listOf(
            Dashboard, Sales, Customers, Reports, Settings
        )

        val drawerItems = listOf(
            Dashboard,
            Sales,
            Customers,
            Products,
            Employees,
            Tanks,
            Payments,
            Shifts,
            Maintenance,
            Sms,
            Reports,
            Users,
            AiChat,
            Settings
        )
    }
}