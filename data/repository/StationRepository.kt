package com.aistudio.dieselstationsms.kxmpzq.data.repository

import com.aistudio.dieselstationsms.kxmpzq.data.model.*

/**
 * ═══════════════════════════════════════════════════════════════
 * StationRepository Interface
 * يعرف جميع عمليات البيانات التي كانت تُقدمها NanoHTTPD REST API
 * ═══════════════════════════════════════════════════════════════
 *
 * الهدف: عزل DatabaseHelper خلف abstraction layer نظيفة
 *        تُمكّن ViewModels من الوصول المباشر للبيانات
 *        بدون المرور بـ localhost:8080
 */
interface StationRepository {

    // ================================================================
    // AUTHENTICATION
    // ================================================================
    suspend fun login(username: String, password: String): AuthResult
    suspend fun getUsers(): Result<List<User>>
    suspend fun getRoles(): Result<List<Role>>
    suspend fun deleteUser(userId: Int): Result<Boolean>
    suspend fun deleteRole(roleId: Int): Result<Boolean>

    // ================================================================
    // PARTIES (CUSTOMERS)
    // ================================================================
    suspend fun getCustomers(): Result<List<Party>>
    suspend fun getCustomerById(id: Int): Result<Party?>
    suspend fun addCustomer(party: Party): Result<Int>
    suspend fun updateCustomer(party: Party): Result<Boolean>
    suspend fun deleteCustomer(partyId: Int): Result<Boolean>

    // ================================================================
    // PRODUCTS & INVENTORY
    // ================================================================
    suspend fun getProducts(): Result<List<Product>>
    suspend fun getCategories(): Result<List<Category>>
    suspend fun getFuelTypes(): Result<List<FuelType>>
    suspend fun addProduct(product: Product): Result<Int>
    suspend fun updateProduct(product: Product): Result<Boolean>
    suspend fun deleteProduct(productId: Int): Result<Boolean>

    // ================================================================
    // TANKS & PUMPS
    // ================================================================
    suspend fun getTanks(): Result<List<Tank>>
    suspend fun getPumps(): Result<List<Pump>>
    suspend fun updateTankQuantity(tankId: Int, quantity: Double): Result<Boolean>

    // ================================================================
    // SALES
    // ================================================================
    suspend fun getSales(): Result<List<Sale>>
    suspend fun executeSale(sale: Sale): Result<Int>
    suspend fun deleteSale(saleId: Int): Result<Boolean>
    suspend fun getFuelSales(): Result<List<FuelSale>>
    suspend fun getDeliveries(): Result<List<Delivery>>

    // ================================================================
    // FINANCE
    // ================================================================
    suspend fun getPayments(): Result<List<Payment>>
    suspend fun makePayment(payment: Payment): Result<Int>
    suspend fun addDeposit(deposit: Deposit): Result<Int>
    suspend fun deletePayment(paymentId: Int): Result<Boolean>

    // ================================================================
    // HR & SHIFTS
    // ================================================================
    suspend fun getEmployees(): Result<List<Employee>>
    suspend fun addEmployee(employee: Employee): Result<Int>
    suspend fun updateEmployee(employee: Employee): Result<Boolean>
    suspend fun deleteEmployee(employeeId: Int): Result<Boolean>

    suspend fun getShifts(): Result<List<Shift>>
    suspend fun getCashiers(): Result<List<Employee>>
    suspend fun addShift(shift: Shift): Result<Int>
    suspend fun deleteShift(shiftId: Int): Result<Boolean>

    // ================================================================
    // MAINTENANCE
    // ================================================================
    suspend fun getMaintenanceRequests(): Result<List<MaintenanceRequest>>
    suspend fun addMaintenanceRequest(request: MaintenanceRequest): Result<Int>
    suspend fun updateMaintenanceStatus(requestId: Int, status: String): Result<Boolean>
    suspend fun deleteMaintenance(requestId: Int): Result<Boolean>

    // ================================================================
    // SMS & NOTIFICATIONS
    // ================================================================
    suspend fun getSmsLogs(): Result<List<SmsMessage>>
    suspend fun getWhitelist(): Result<List<SmsWhitelistEntry>>
    suspend fun addWhitelist(entry: SmsWhitelistEntry): Result<Int>
    suspend fun removeWhitelist(phone: String): Result<Boolean>
    suspend fun getNotifications(): Result<List<Notification>>

    // ================================================================
    // SETTINGS
    // ================================================================
    suspend fun getSetting(key: String): Result<String?>
    suspend fun setSetting(key: String, value: String): Result<Boolean>
    suspend fun getAllSettings(): Result<Map<String, String>>

    // ================================================================
    // REPORTS & DASHBOARD
    // ================================================================
    suspend fun getDashboard(): Result<DashboardData>
    suspend fun getDailySales(): Result<List<DailySales>>
    suspend fun getAiInsight(): Result<AiInsight>
    suspend fun getEodReport(): Result<EodReport>
    suspend fun getMonthlySales(): Result<List<MonthlySales>>
    suspend fun getProfitReport(fromDate: String, toDate: String): Result<ProfitReport>
    suspend fun exportData(): Result<ExportData>

    // ================================================================
    // AI
    // ================================================================
    suspend fun aiChat(message: String, sessionId: String = "default"): Result<AiChatResponse>

    // ================================================================
    // STOCK MOVEMENTS
    // ================================================================
    suspend fun getStockMovements(): Result<List<StockMovement>>
    suspend fun addStockMovement(movement: StockMovement): Result<Int>
}