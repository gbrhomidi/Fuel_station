package com.aistudio.dieselstationsms.kxmpzq.data.repository

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.Log
import com.aistudio.dieselstationsms.kxmpzq.DatabaseHelper
import com.aistudio.dieselstationsms.kxmpzq.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class StationRepositoryImpl(
    private val context: Context
) : StationRepository {

    companion object {
        private const val TAG = "StationRepository"
    }

    private val dbHelper: DatabaseHelper by lazy { DatabaseHelper(context) }
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    private suspend fun <T> safeDbCall(block: () -> T): Result<T> = withContext(Dispatchers.IO) {
        try {
            Result.success(block())
        } catch (e: Exception) {
            Log.e(TAG, "Database error: ${e.message}", e)
            Result.failure(e)
        }
    }

    private fun getCurrentDateTime(): String = dateFormat.format(Date())

    // ─── Authentication ──────────────────────────────────────────

    override suspend fun login(username: String, password: String): AuthResult = withContext(Dispatchers.IO) {
        try {
            val cursor = dbHelper.authenticateUser(username, password)
            if (cursor != null && cursor.moveToFirst()) {
                val user = cursor.toUser()
                cursor.close()
                AuthResult(
                    success = true,
                    user = user,
                    token = UUID.randomUUID().toString()
                )
            } else {
                cursor?.close()
                AuthResult(success = false, error = "اسم المستخدم أو كلمة المرور غير صحيحة")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Login error: ${e.message}", e)
            AuthResult(success = false, error = e.message)
        }
    }

    override suspend fun getUsers(): Result<List<User>> = safeDbCall {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<User>()
        db.rawQuery("SELECT * FROM users WHERE is_deleted = 0 OR is_deleted IS NULL", null).use { cursor ->
            while (cursor.moveToNext()) {
                list.add(cursor.toUser())
            }
        }
        list
    }

    override suspend fun getRoles(): Result<List<Role>> = safeDbCall {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<Role>()
        db.rawQuery("SELECT * FROM roles", null).use { cursor ->
            while (cursor.moveToNext()) {
                list.add(Role(
                    id = cursor.getInt("id"),
                    name = cursor.getString("name"),
                    permissions = cursor.getStringOrNull("permissions"),
                    description = cursor.getStringOrNull("description")
                ))
            }
        }
        list
    }

    override suspend fun deleteUser(userId: Int): Result<Boolean> = safeDbCall {
        dbHelper.execSQL("UPDATE users SET is_deleted = 1 WHERE id = ?", arrayOf(userId.toString()))
        true
    }

    override suspend fun deleteRole(roleId: Int): Result<Boolean> = safeDbCall {
        dbHelper.execSQL("DELETE FROM roles WHERE id = ?", arrayOf(roleId.toString()))
        true
    }

    // ─── Parties (Customers) ─────────────────────────────────────

    override suspend fun getCustomers(): Result<List<Party>> = safeDbCall {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<Party>()
        db.rawQuery("SELECT * FROM parties WHERE is_deleted = 0 OR is_deleted IS NULL", null).use { cursor ->
            while (cursor.moveToNext()) {
                list.add(cursor.toParty())
            }
        }
        list
    }

    override suspend fun getCustomerById(id: Int): Result<Party?> = safeDbCall {
        val db = dbHelper.readableDatabase
        db.rawQuery(
            "SELECT * FROM parties WHERE id = ? AND (is_deleted = 0 OR is_deleted IS NULL)",
            arrayOf(id.toString())
        ).use { cursor ->
            if (cursor.moveToFirst()) cursor.toParty() else null
        }
    }

    override suspend fun addCustomer(party: Party): Result<Int> = safeDbCall {
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
            put("uuid", party.uuid)
            put("name", party.name)
            put("commercial_name", party.commercialName)
            put("phone", party.phone)
            put("email", party.email)
            put("address", party.address)
            put("balance", party.balance)
            put("credit_limit", party.creditLimit)
            put("points", party.points)
            put("vip_level", party.vipLevel)
            put("vehicle_type", party.vehicleType)
            put("fleet_size", party.fleetSize)
            put("is_active", if (party.isActive) 1 else 0)
            put("created_at", getCurrentDateTime())
            put("updated_at", getCurrentDateTime())
        }
        db.insert("parties", null, cv).toInt()
    }

    override suspend fun updateCustomer(party: Party): Result<Boolean> = safeDbCall {
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
            put("name", party.name)
            put("commercial_name", party.commercialName)
            put("phone", party.phone)
            put("email", party.email)
            put("address", party.address)
            put("balance", party.balance)
            put("credit_limit", party.creditLimit)
            put("points", party.points)
            put("vip_level", party.vipLevel)
            put("vehicle_type", party.vehicleType)
            put("fleet_size", party.fleetSize)
            put("is_active", if (party.isActive) 1 else 0)
            put("updated_at", getCurrentDateTime())
        }
        db.update("parties", cv, "id = ?", arrayOf(party.id.toString())) > 0
    }

    override suspend fun deleteCustomer(partyId: Int): Result<Boolean> = safeDbCall {
        dbHelper.execSQL("UPDATE parties SET is_deleted = 1 WHERE id = ?", arrayOf(partyId.toString()))
        true
    }

    // ─── Products & Inventory ────────────────────────────────────

    override suspend fun getProducts(): Result<List<Product>> = safeDbCall {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<Product>()
        db.rawQuery("SELECT * FROM products WHERE is_deleted = 0 OR is_deleted IS NULL", null).use { cursor ->
            while (cursor.moveToNext()) {
                list.add(cursor.toProduct())
            }
        }
        list
    }

    override suspend fun getCategories(): Result<List<Category>> = safeDbCall {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<Category>()
        db.rawQuery("SELECT * FROM categories", null).use { cursor ->
            while (cursor.moveToNext()) {
                list.add(Category(
                    id = cursor.getInt("id"),
                    name = cursor.getString("name"),
                    description = cursor.getStringOrNull("description"),
                    parentId = cursor.getIntOrNull("parent_id")
                ))
            }
        }
        list
    }

    override suspend fun getFuelTypes(): Result<List<FuelType>> = safeDbCall {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<FuelType>()
        db.rawQuery("SELECT * FROM fuel_types WHERE is_deleted = 0 OR is_deleted IS NULL", null).use { cursor ->
            while (cursor.moveToNext()) {
                list.add(cursor.toFuelType())
            }
        }
        list
    }

    override suspend fun addProduct(product: Product): Result<Int> = safeDbCall {
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
            put("uuid", product.uuid)
            put("name", product.name)
            put("category_id", product.categoryId)
            put("sku", product.sku)
            put("barcode", product.barcode)
            put("description", product.description)
            put("unit_price", product.unitPrice)
            put("cost_price", product.costPrice)
            put("quantity", product.quantity)
            put("min_stock", product.minStock)
            put("is_active", if (product.isActive) 1 else 0)
            put("created_at", getCurrentDateTime())
        }
        db.insert("products", null, cv).toInt()
    }

    override suspend fun updateProduct(product: Product): Result<Boolean> = safeDbCall {
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
            put("name", product.name)
            put("category_id", product.categoryId)
            put("sku", product.sku)
            put("barcode", product.barcode)
            put("description", product.description)
            put("unit_price", product.unitPrice)
            put("cost_price", product.costPrice)
            put("quantity", product.quantity)
            put("min_stock", product.minStock)
            put("is_active", if (product.isActive) 1 else 0)
        }
        db.update("products", cv, "id = ?", arrayOf(product.id.toString())) > 0
    }

    override suspend fun deleteProduct(productId: Int): Result<Boolean> = safeDbCall {
        dbHelper.execSQL("UPDATE products SET is_deleted = 1 WHERE id = ?", arrayOf(productId.toString()))
        true
    }

    // ─── Tanks ────────────────────────────────────────────────────

    override suspend fun getTanks(): Result<List<Tank>> = safeDbCall {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<Tank>()
        db.rawQuery("SELECT * FROM tanks WHERE is_deleted = 0 OR is_deleted IS NULL", null).use { cursor ->
            while (cursor.moveToNext()) {
                list.add(cursor.toTank())
            }
        }
        list
    }

    override suspend fun getPumps(): Result<List<Pump>> = safeDbCall {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<Pump>()
        db.rawQuery("SELECT * FROM pumps WHERE is_deleted = 0 OR is_deleted IS NULL", null).use { cursor ->
            while (cursor.moveToNext()) {
                list.add(Pump(
                    id = cursor.getInt("id"),
                    uuid = cursor.getString("uuid"),
                    name = cursor.getString("name"),
                    tankId = cursor.getIntOrNull("tank_id"),
                    fuelTypeId = cursor.getIntOrNull("fuel_type_id"),
                    status = cursor.getStringOrNull("status") ?: "active",
                    totalDispensed = cursor.getDoubleOrNull("total_dispensed") ?: 0.0,
                    lastMaintenance = cursor.getStringOrNull("last_maintenance")
                ))
            }
        }
        list
    }

    override suspend fun updateTankQuantity(tankId: Int, quantity: Double): Result<Boolean> = safeDbCall {
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
            put("current_quantity", quantity)
        }
        db.update("tanks", cv, "id = ?", arrayOf(tankId.toString())) > 0
    }

    // ─── Sales ────────────────────────────────────────────────────

    override suspend fun getSales(): Result<List<Sale>> = safeDbCall {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<Sale>()
        db.rawQuery(
            "SELECT * FROM sales_transactions WHERE is_deleted = 0 OR is_deleted IS NULL ORDER BY created_at DESC",
            null
        ).use { cursor ->
            while (cursor.moveToNext()) {
                list.add(cursor.toSale())
            }
        }
        list
    }

    override suspend fun executeSale(sale: Sale): Result<Int> = safeDbCall {
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
            put("uuid", sale.uuid)
            put("party_id", sale.partyId)
            put("product_id", sale.productId)
            put("fuel_type_id", sale.fuelTypeId)
            put("pump_id", sale.pumpId)
            put("quantity", sale.quantity)
            put("price_per_liter", sale.pricePerLiter)
            put("total_amount", sale.totalAmount)
            put("payment_method", sale.paymentMethod)
            put("status", sale.status)
            put("delivery_location", sale.deliveryLocation)
            put("delivery_time", sale.deliveryTime)
            put("driver_id", sale.driverId)
            put("vehicle_id", sale.vehicleId)
            put("order_type", sale.orderType)
            put("shift_id", sale.shiftId)
            put("notes", sale.notes)
            put("created_at", getCurrentDateTime())
            put("updated_at", getCurrentDateTime())
        }
        db.insert("sales_transactions", null, cv).toInt()
    }

    override suspend fun deleteSale(saleId: Int): Result<Boolean> = safeDbCall {
        dbHelper.execSQL("UPDATE sales_transactions SET is_deleted = 1 WHERE id = ?", arrayOf(saleId.toString()))
        true
    }

    override suspend fun getFuelSales(): Result<List<FuelSale>> = safeDbCall {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<FuelSale>()
        db.rawQuery("SELECT * FROM fuel_sales WHERE is_deleted = 0 OR is_deleted IS NULL", null).use { cursor ->
            while (cursor.moveToNext()) {
                list.add(cursor.toFuelSale())
            }
        }
        list
    }

    override suspend fun getDeliveries(): Result<List<Delivery>> = safeDbCall {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<Delivery>()
        db.rawQuery("SELECT * FROM deliveries WHERE is_deleted = 0 OR is_deleted IS NULL", null).use { cursor ->
            while (cursor.moveToNext()) {
                list.add(cursor.toDelivery())
            }
        }
        list
    }

    // ─── Finance ──────────────────────────────────────────────────

    override suspend fun getPayments(): Result<List<Payment>> = safeDbCall {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<Payment>()
        db.rawQuery(
            "SELECT * FROM payments WHERE is_deleted = 0 OR is_deleted IS NULL ORDER BY created_at DESC",
            null
        ).use { cursor ->
            while (cursor.moveToNext()) {
                list.add(cursor.toPayment())
            }
        }
        list
    }

    override suspend fun makePayment(payment: Payment): Result<Int> = safeDbCall {
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
            put("uuid", payment.uuid)
            put("party_id", payment.partyId)
            put("amount", payment.amount)
            put("payment_method", payment.paymentMethod)
            put("reference_number", payment.referenceNumber)
            put("notes", payment.notes)
            put("created_at", getCurrentDateTime())
        }
        db.insert("payments", null, cv).toInt()
    }

    override suspend fun addDeposit(deposit: Deposit): Result<Int> = safeDbCall {
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
            put("uuid", deposit.uuid)
            put("party_id", deposit.partyId)
            put("amount", deposit.amount)
            put("deposit_method", deposit.depositMethod)
            put("reference_number", deposit.referenceNumber)
            put("notes", deposit.notes)
            put("created_at", getCurrentDateTime())
        }
        db.insert("deposits", null, cv).toInt()
    }

    override suspend fun deletePayment(paymentId: Int): Result<Boolean> = safeDbCall {
        dbHelper.execSQL("UPDATE payments SET is_deleted = 1 WHERE id = ?", arrayOf(paymentId.toString()))
        true
    }

    // ─── HR & Shifts ──────────────────────────────────────────────

    override suspend fun getEmployees(): Result<List<Employee>> = safeDbCall {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<Employee>()
        db.rawQuery("SELECT * FROM employees WHERE is_deleted = 0 OR is_deleted IS NULL", null).use { cursor ->
            while (cursor.moveToNext()) {
                list.add(cursor.toEmployee())
            }
        }
        list
    }

    override suspend fun addEmployee(employee: Employee): Result<Int> = safeDbCall {
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
            put("uuid", employee.uuid)
            put("full_name", employee.fullName)
            put("phone", employee.phone)
            put("email", employee.email)
            put("position", employee.position)
            put("salary", employee.salary)
            put("hire_date", employee.hireDate)
            put("is_active", if (employee.isActive) 1 else 0)
            put("created_at", getCurrentDateTime())
        }
        db.insert("employees", null, cv).toInt()
    }

    override suspend fun updateEmployee(employee: Employee): Result<Boolean> = safeDbCall {
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
            put("full_name", employee.fullName)
            put("phone", employee.phone)
            put("email", employee.email)
            put("position", employee.position)
            put("salary", employee.salary)
            put("hire_date", employee.hireDate)
            put("is_active", if (employee.isActive) 1 else 0)
        }
        db.update("employees", cv, "id = ?", arrayOf(employee.id.toString())) > 0
    }

    override suspend fun deleteEmployee(employeeId: Int): Result<Boolean> = safeDbCall {
        dbHelper.execSQL("UPDATE employees SET is_deleted = 1 WHERE id = ?", arrayOf(employeeId.toString()))
        true
    }

    override suspend fun getShifts(): Result<List<Shift>> = safeDbCall {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<Shift>()
        db.rawQuery(
            "SELECT * FROM shifts WHERE is_deleted = 0 OR is_deleted IS NULL ORDER BY created_at DESC",
            null
        ).use { cursor ->
            while (cursor.moveToNext()) {
                list.add(cursor.toShift())
            }
        }
        list
    }

    override suspend fun getCashiers(): Result<List<Employee>> = safeDbCall {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<Employee>()
        db.rawQuery(
            "SELECT * FROM employees WHERE (is_deleted = 0 OR is_deleted IS NULL) AND position LIKE ?",
            arrayOf("%cashier%")
        ).use { cursor ->
            while (cursor.moveToNext()) {
                list.add(cursor.toEmployee())
            }
        }
        list
    }

    override suspend fun addShift(shift: Shift): Result<Int> = safeDbCall {
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
            put("uuid", shift.uuid)
            put("cashier_id", shift.cashierId)
            put("start_time", shift.startTime)
            put("end_time", shift.endTime)
            put("starting_cash", shift.startingCash)
            put("ending_cash", shift.endingCash)
            put("total_sales", shift.totalSales)
            put("status", shift.status)
            put("notes", shift.notes)
            put("created_at", getCurrentDateTime())
        }
        db.insert("shifts", null, cv).toInt()
    }

    override suspend fun deleteShift(shiftId: Int): Result<Boolean> = safeDbCall {
        dbHelper.execSQL("UPDATE shifts SET is_deleted = 1 WHERE id = ?", arrayOf(shiftId.toString()))
        true
    }

    // ─── Maintenance ──────────────────────────────────────────────

    override suspend fun getMaintenanceRequests(): Result<List<MaintenanceRequest>> = safeDbCall {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<MaintenanceRequest>()
        db.rawQuery(
            "SELECT * FROM maintenance_requests WHERE is_deleted = 0 OR is_deleted IS NULL ORDER BY created_at DESC",
            null
        ).use { cursor ->
            while (cursor.moveToNext()) {
                list.add(cursor.toMaintenanceRequest())
            }
        }
        list
    }

    override suspend fun addMaintenanceRequest(request: MaintenanceRequest): Result<Int> = safeDbCall {
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
            put("uuid", request.uuid)
            put("title", request.title)
            put("description", request.description)
            put("equipment_type", request.equipmentType)
            put("equipment_id", request.equipmentId)
            put("priority", request.priority)
            put("status", request.status)
            put("requested_by", request.requestedBy)
            put("assigned_to", request.assignedTo)
            put("created_at", getCurrentDateTime())
        }
        db.insert("maintenance_requests", null, cv).toInt()
    }

    override suspend fun updateMaintenanceStatus(requestId: Int, status: String): Result<Boolean> = safeDbCall {
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
            put("status", status)
            if (status == "completed") {
                put("completed_at", getCurrentDateTime())
            }
        }
        db.update("maintenance_requests", cv, "id = ?", arrayOf(requestId.toString())) > 0
    }

    override suspend fun deleteMaintenance(requestId: Int): Result<Boolean> = safeDbCall {
        dbHelper.execSQL("UPDATE maintenance_requests SET is_deleted = 1 WHERE id = ?", arrayOf(requestId.toString()))
        true
    }

    // ─── SMS & Notifications ──────────────────────────────────────

    override suspend fun getSmsLogs(): Result<List<SmsMessage>> = safeDbCall {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<SmsMessage>()
        db.rawQuery("SELECT * FROM sms_messages ORDER BY created_at DESC", null).use { cursor ->
            while (cursor.moveToNext()) {
                list.add(cursor.toSmsMessage())
            }
        }
        list
    }

    override suspend fun getWhitelist(): Result<List<SmsWhitelistEntry>> = safeDbCall {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<SmsWhitelistEntry>()
        db.rawQuery("SELECT * FROM sms_whitelist WHERE is_active = 1", null).use { cursor ->
            while (cursor.moveToNext()) {
                list.add(cursor.toSmsWhitelistEntry())
            }
        }
        list
    }

    override suspend fun addWhitelist(entry: SmsWhitelistEntry): Result<Int> = safeDbCall {
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
            put("phone", entry.phone)
            put("name", entry.name)
            put("is_active", 1)
            put("created_at", getCurrentDateTime())
        }
        db.insert("sms_whitelist", null, cv).toInt()
    }

    override suspend fun removeWhitelist(phone: String): Result<Boolean> = safeDbCall {
        dbHelper.delete("sms_whitelist", "phone = ?", arrayOf(phone))
        true
    }

    override suspend fun getNotifications(): Result<List<Notification>> = safeDbCall {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<Notification>()
        db.rawQuery("SELECT * FROM notifications ORDER BY created_at DESC LIMIT 100", null).use { cursor ->
            while (cursor.moveToNext()) {
                list.add(cursor.toNotification())
            }
        }
        list
    }

    // ─── Settings ──────────────────────────────────────────────────

    override suspend fun getSetting(key: String): Result<String?> = safeDbCall {
        val db = dbHelper.readableDatabase
        db.rawQuery("SELECT value FROM settings WHERE key = ?", arrayOf(key)).use { cursor ->
            if (cursor.moveToFirst()) cursor.getString("value") else null
        }
    }

    override suspend fun setSetting(key: String, value: String): Result<Boolean> = safeDbCall {
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
            put("key", key)
            put("value", value)
            put("updated_at", getCurrentDateTime())
        }
        db.insertWithOnConflict("settings", null, cv, android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE)
        true
    }

    override suspend fun getAllSettings(): Result<Map<String, String>> = safeDbCall {
        val db = dbHelper.readableDatabase
        val map = mutableMapOf<String, String>()
        db.rawQuery("SELECT key, value FROM settings", null).use { cursor ->
            while (cursor.moveToNext()) {
                map[cursor.getString("key")] = cursor.getString("value")
            }
        }
        map
    }

    // ─── Reports & Dashboard ──────────────────────────────────────

    override suspend fun getDashboard(): Result<DashboardData> = safeDbCall {
        val db = dbHelper.readableDatabase
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val weekAgo = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -7) }.time
        val monthAgo = Calendar.getInstance().apply { add(Calendar.MONTH, -1) }.time
        val weekStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(weekAgo)
        val monthStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(monthAgo)

        DashboardData(
            totalSales = db.rawQuery(
                "SELECT COALESCE(SUM(total_amount), 0) as total FROM sales_transactions WHERE is_deleted = 0 OR is_deleted IS NULL",
                null
            ).use { if (it.moveToFirst()) it.getDouble(0) else 0.0 },
            totalTransactions = db.rawQuery(
                "SELECT COUNT(*) FROM sales_transactions WHERE is_deleted = 0 OR is_deleted IS NULL",
                null
            ).use { if (it.moveToFirst()) it.getInt(0) else 0 },
            totalCustomers = db.rawQuery(
                "SELECT COUNT(*) FROM parties WHERE is_deleted = 0 OR is_deleted IS NULL",
                null
            ).use { if (it.moveToFirst()) it.getInt(0) else 0 },
            totalFuelDispensed = db.rawQuery(
                "SELECT COALESCE(SUM(quantity), 0) FROM fuel_sales WHERE is_deleted = 0 OR is_deleted IS NULL",
                null
            ).use { if (it.moveToFirst()) it.getDouble(0) else 0.0 },
            activeShifts = db.rawQuery(
                "SELECT COUNT(*) FROM shifts WHERE status = 'open' AND (is_deleted = 0 OR is_deleted IS NULL)",
                null
            ).use { if (it.moveToFirst()) it.getInt(0) else 0 },
            lowStockItems = db.rawQuery(
                "SELECT COUNT(*) FROM products WHERE quantity <= min_stock AND (is_deleted = 0 OR is_deleted IS NULL)",
                null
            ).use { if (it.moveToFirst()) it.getInt(0) else 0 },
            pendingMaintenance = db.rawQuery(
                "SELECT COUNT(*) FROM maintenance_requests WHERE status = 'pending' AND (is_deleted = 0 OR is_deleted IS NULL)",
                null
            ).use { if (it.moveToFirst()) it.getInt(0) else 0 },
            todayRevenue = db.rawQuery(
                "SELECT COALESCE(SUM(total_amount), 0) FROM sales_transactions WHERE DATE(created_at) = ? AND (is_deleted = 0 OR is_deleted IS NULL)",
                arrayOf(today)
            ).use { if (it.moveToFirst()) it.getDouble(0) else 0.0 },
            weeklyRevenue = db.rawQuery(
                "SELECT COALESCE(SUM(total_amount), 0) FROM sales_transactions WHERE DATE(created_at) >= ? AND (is_deleted = 0 OR is_deleted IS NULL)",
                arrayOf(weekStr)
            ).use { if (it.moveToFirst()) it.getDouble(0) else 0.0 },
            monthlyRevenue = db.rawQuery(
                "SELECT COALESCE(SUM(total_amount), 0) FROM sales_transactions WHERE DATE(created_at) >= ? AND (is_deleted = 0 OR is_deleted IS NULL)",
                arrayOf(monthStr)
            ).use { if (it.moveToFirst()) it.getDouble(0) else 0.0 }
        )
    }

    override suspend fun getDailySales(): Result<List<DailySales>> = safeDbCall {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<DailySales>()
        db.rawQuery(
            """
            SELECT DATE(created_at) as sale_date, 
                   COALESCE(SUM(total_amount), 0) as total, 
                   COUNT(*) as count,
                   COALESCE(SUM(quantity), 0) as fuel_qty
            FROM sales_transactions 
            WHERE (is_deleted = 0 OR is_deleted IS NULL) 
            AND created_at >= date('now', '-30 days')
            GROUP BY DATE(created_at)
            ORDER BY sale_date DESC
            """.trimIndent(),
            null
        ).use { cursor ->
            while (cursor.moveToNext()) {
                list.add(DailySales(
                    date = cursor.getString("sale_date"),
                    totalAmount = cursor.getDouble("total"),
                    transactionCount = cursor.getInt("count"),
                    fuelDispensed = cursor.getDouble("fuel_qty")
                ))
            }
        }
        list
    }

    override suspend fun getAiInsight(): Result<AiInsight> = safeDbCall {
        // Placeholder: سيتم استبدالها بـ Gemini API الفعلي
        AiInsight(
            insight = "تحليل ذكي للمحطة: الأداء الحالي جيد، ولكن هناك فرص للتحسين.",
            recommendations = listOf(
                "زيادة المخزون من الأصناف الأكثر مبيعاً",
                "تحسين تجربة العملاء من خلال برنامج ولاء",
                "استخدام Gemini API للحصول على تحليلات أعمق"
            )
        )
    }

    override suspend fun getEodReport(): Result<EodReport> = safeDbCall {
        val db = dbHelper.readableDatabase
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        EodReport(
            date = today,
            totalSales = db.rawQuery(
                "SELECT COALESCE(SUM(total_amount), 0) FROM sales_transactions WHERE DATE(created_at) = ? AND (is_deleted = 0 OR is_deleted IS NULL)",
                arrayOf(today)
            ).use { if (it.moveToFirst()) it.getDouble(0) else 0.0 },
            totalPayments = db.rawQuery(
                "SELECT COALESCE(SUM(amount), 0) FROM payments WHERE DATE(created_at) = ? AND (is_deleted = 0 OR is_deleted IS NULL)",
                arrayOf(today)
            ).use { if (it.moveToFirst()) it.getDouble(0) else 0.0 },
            totalDeposits = db.rawQuery(
                "SELECT COALESCE(SUM(amount), 0) FROM deposits WHERE DATE(created_at) = ?",
                arrayOf(today)
            ).use { if (it.moveToFirst()) it.getDouble(0) else 0.0 },
            cashInHand = 0.0,
            shiftCount = db.rawQuery(
                "SELECT COUNT(*) FROM shifts WHERE DATE(created_at) = ? AND (is_deleted = 0 OR is_deleted IS NULL)",
                arrayOf(today)
            ).use { if (it.moveToFirst()) it.getInt(0) else 0 },
            transactionCount = db.rawQuery(
                "SELECT COUNT(*) FROM sales_transactions WHERE DATE(created_at) = ? AND (is_deleted = 0 OR is_deleted IS NULL)",
                arrayOf(today)
            ).use { if (it.moveToFirst()) it.getInt(0) else 0 }
        )
    }

    override suspend fun getMonthlySales(): Result<List<MonthlySales>> = safeDbCall {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<MonthlySales>()
        db.rawQuery(
            """
            SELECT strftime('%Y-%m', created_at) as month, 
                   COALESCE(SUM(total_amount), 0) as total, 
                   COUNT(*) as count
            FROM sales_transactions 
            WHERE (is_deleted = 0 OR is_deleted IS NULL) 
            AND created_at >= date('now', '-12 months')
            GROUP BY strftime('%Y-%m', created_at)
            ORDER BY month DESC
            """.trimIndent(),
            null
        ).use { cursor ->
            while (cursor.moveToNext()) {
                list.add(MonthlySales(
                    month = cursor.getString("month"),
                    totalAmount = cursor.getDouble("total"),
                    transactionCount = cursor.getInt("count")
                ))
            }
        }
        list
    }

    override suspend fun getProfitReport(fromDate: String, toDate: String): Result<ProfitReport> = safeDbCall {
        val db = dbHelper.readableDatabase
        val revenue = db.rawQuery(
            "SELECT COALESCE(SUM(total_amount), 0) FROM sales_transactions WHERE DATE(created_at) BETWEEN ? AND ? AND (is_deleted = 0 OR is_deleted IS NULL)",
            arrayOf(fromDate, toDate)
        ).use { if (it.moveToFirst()) it.getDouble(0) else 0.0 }

        val cost = db.rawQuery(
            """
            SELECT COALESCE(SUM(st.quantity * p.cost_price), 0) 
            FROM sales_transactions st 
            LEFT JOIN products p ON st.product_id = p.id 
            WHERE DATE(st.created_at) BETWEEN ? AND ? 
            AND (st.is_deleted = 0 OR st.is_deleted IS NULL)
            """.trimIndent(),
            arrayOf(fromDate, toDate)
        ).use { if (it.moveToFirst()) it.getDouble(0) else 0.0 }

        val profit = revenue - cost
        ProfitReport(
            fromDate = fromDate,
            toDate = toDate,
            totalRevenue = revenue,
            totalCost = cost,
            grossProfit = profit,
            profitMargin = if (revenue > 0) (profit / revenue) * 100 else 0.0
        )
    }

    override suspend fun exportData(): Result<ExportData> = safeDbCall {
        ExportData(
            parties = getCustomers().getOrNull() ?: emptyList(),
            products = getProducts().getOrNull() ?: emptyList(),
            sales = getSales().getOrNull() ?: emptyList(),
            payments = getPayments().getOrNull() ?: emptyList(),
            employees = getEmployees().getOrNull() ?: emptyList(),
            generatedAt = getCurrentDateTime()
        )
    }

    // ─── AI ────────────────────────────────────────────────────────

    override suspend fun aiChat(message: String, sessionId: String): Result<AiChatResponse> = safeDbCall {
        // Placeholder: سيتم استبدالها بـ Gemini API الفعلي
        AiChatResponse(
            response = "مرحباً! لقد استقبلت رسالتك: '$message'. أنا مساعد Gemini الذكي. كيف يمكنني مساعدتك؟",
            sessionId = sessionId
        )
    }

    // ─── Stock Movements ──────────────────────────────────────────

    override suspend fun getStockMovements(): Result<List<StockMovement>> = safeDbCall {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<StockMovement>()
        db.rawQuery(
            "SELECT * FROM stock_movements WHERE is_deleted = 0 OR is_deleted IS NULL ORDER BY created_at DESC",
            null
        ).use { cursor ->
            while (cursor.moveToNext()) {
                list.add(cursor.toStockMovement())
            }
        }
        list
    }

    override suspend fun addStockMovement(movement: StockMovement): Result<Int> = safeDbCall {
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
            put("uuid", movement.uuid)
            put("product_id", movement.productId)
            put("movement_type", movement.movementType)
            put("quantity", movement.quantity)
            put("unit_cost", movement.unitCost)
            put("total_cost", movement.totalCost)
            put("reference_type", movement.referenceType)
            put("reference_id", movement.referenceId)
            put("movement_date", movement.movementDate)
            put("notes", movement.notes)
            put("created_by", movement.createdBy)
            put("created_at", getCurrentDateTime())
        }
        db.insert("stock_movements", null, cv).toInt()
    }

    // ─── Cursor Extension Functions ──────────────────────────────

    private fun Cursor.getString(columnName: String): String = getString(getColumnIndexOrThrow(columnName))
    private fun Cursor.getStringOrNull(columnName: String): String? = getColumnIndex(columnName).takeIf { it >= 0 }?.let { getString(it) }
    private fun Cursor.getInt(columnName: String): Int = getInt(getColumnIndexOrThrow(columnName))
    private fun Cursor.getIntOrNull(columnName: String): Int? = getColumnIndex(columnName).takeIf { it >= 0 }?.let { getInt(it) }
    private fun Cursor.getDouble(columnName: String): Double = getDouble(getColumnIndexOrThrow(columnName))
    private fun Cursor.getDoubleOrNull(columnName: String): Double? = getColumnIndex(columnName).takeIf { it >= 0 }?.let { getDouble(it) }

    private fun Cursor.toUser() = User(
        id = getInt("id"),
        username = getString("username"),
        fullName = getStringOrNull("full_name"),
        email = getStringOrNull("email"),
        phone = getStringOrNull("phone"),
        roleId = getIntOrNull("role_id"),
        isActive = getIntOrNull("is_active") != 0,
        createdAt = getStringOrNull("created_at"),
        updatedAt = getStringOrNull("updated_at")
    )

    private fun Cursor.toParty() = Party(
        id = getInt("id"),
        uuid = getString("uuid"),
        name = getString("name"),
        commercialName = getStringOrNull("commercial_name"),
        phone = getStringOrNull("phone"),
        email = getStringOrNull("email"),
        address = getStringOrNull("address"),
        balance = getDoubleOrNull("balance") ?: 0.0,
        creditLimit = getDoubleOrNull("credit_limit") ?: 0.0,
        points = getIntOrNull("points") ?: 0,
        vipLevel = getIntOrNull("vip_level") ?: 0,
        vehicleType = getStringOrNull("vehicle_type"),
        fleetSize = getIntOrNull("fleet_size") ?: 0,
        isActive = getIntOrNull("is_active") != 0,
        createdAt = getStringOrNull("created_at"),
        updatedAt = getStringOrNull("updated_at")
    )

    private fun Cursor.toProduct() = Product(
        id = getInt("id"),
        uuid = getString("uuid"),
        name = getString("name"),
        categoryId = getIntOrNull("category_id"),
        sku = getStringOrNull("sku"),
        barcode = getStringOrNull("barcode"),
        description = getStringOrNull("description"),
        unitPrice = getDoubleOrNull("unit_price") ?: 0.0,
        costPrice = getDoubleOrNull("cost_price") ?: 0.0,
        quantity = getDoubleOrNull("quantity") ?: 0.0,
        minStock = getDoubleOrNull("min_stock") ?: 0.0,
        isActive = getIntOrNull("is_active") != 0,
        createdAt = getStringOrNull("created_at")
    )

    private fun Cursor.toFuelType() = FuelType(
        id = getInt("id"),
        uuid = getString("uuid"),
        name = getString("name"),
        pricePerLiter = getDoubleOrNull("price_per_liter") ?: 0.0,
        description = getStringOrNull("description"),
        isActive = getIntOrNull("is_active") != 0
    )

    private fun Cursor.toTank() = Tank(
        id = getInt("id"),
        uuid = getString("uuid"),
        name = getString("name"),
        fuelTypeId = getIntOrNull("fuel_type_id"),
        capacity = getDoubleOrNull("capacity") ?: 0.0,
        currentQuantity = getDoubleOrNull("current_quantity") ?: 0.0,
        minLevel = getDoubleOrNull("min_level") ?: 0.0,
        status = getStringOrNull("status") ?: "active",
        lastRefillDate = getStringOrNull("last_refill_date"),
        notes = getStringOrNull("notes")
    )

    private fun Cursor.toSale() = Sale(
        id = getInt("id"),
        uuid = getString("uuid"),
        partyId = getIntOrNull("party_id"),
        productId = getIntOrNull("product_id"),
        fuelTypeId = getIntOrNull("fuel_type_id"),
        pumpId = getIntOrNull("pump_id"),
        quantity = getDoubleOrNull("quantity") ?: 0.0,
        pricePerLiter = getDoubleOrNull("price_per_liter") ?: 0.0,
        totalAmount = getDoubleOrNull("total_amount") ?: 0.0,
        paymentMethod = getStringOrNull("payment_method") ?: "cash",
        status = getStringOrNull("status") ?: "completed",
        deliveryLocation = getStringOrNull("delivery_location"),
        deliveryTime = getStringOrNull("delivery_time"),
        driverId = getIntOrNull("driver_id"),
        vehicleId = getIntOrNull("vehicle_id"),
        orderType = getStringOrNull("order_type") ?: "sale",
        shiftId = getIntOrNull("shift_id"),
        notes = getStringOrNull("notes"),
        createdAt = getStringOrNull("created_at"),
        updatedAt = getStringOrNull("updated_at")
    )

    private fun Cursor.toFuelSale() = FuelSale(
        id = getInt("id"),
        uuid = getString("uuid"),
        saleId = getIntOrNull("sale_id"),
        shiftId = getIntOrNull("shift_id"),
        pumpId = getIntOrNull("pump_id"),
        fuelTypeId = getIntOrNull("fuel_type_id"),
        quantity = getDoubleOrNull("quantity") ?: 0.0,
        pricePerLiter = getDoubleOrNull("price_per_liter") ?: 0.0,
        totalAmount = getDoubleOrNull("total_amount") ?: 0.0,
        paymentMethod = getStringOrNull("payment_method") ?: "cash",
        customerId = getIntOrNull("customer_id"),
        vehiclePlate = getStringOrNull("vehicle_plate"),
        saleDate = getStringOrNull("sale_date"),
        saleTime = getStringOrNull("sale_time"),
        notes = getStringOrNull("notes")
    )

    private fun Cursor.toDelivery() = Delivery(
        id = getInt("id"),
        uuid = getString("uuid"),
        saleId = getIntOrNull("sale_id"),
        partyId = getIntOrNull("party_id"),
        vehicleId = getIntOrNull("vehicle_id"),
        driverId = getIntOrNull("driver_id"),
        deliveryDate = getString("delivery_date"),
        quantity = getDoubleOrNull("quantity") ?: 0.0,
        fuelType = getStringOrNull("fuel_type") ?: "diesel",
        pricePerLiter = getDoubleOrNull("price_per_liter") ?: 0.0,
        totalAmount = getDoubleOrNull("total_amount") ?: 0.0,
        status = getStringOrNull("status") ?: "delivered",
        location = getStringOrNull("location"),
        notes = getStringOrNull("notes")
    )

    private fun Cursor.toPayment() = Payment(
        id = getInt("id"),
        uuid = getString("uuid"),
        partyId = getIntOrNull("party_id"),
        amount = getDoubleOrNull("amount") ?: 0.0,
        paymentMethod = getStringOrNull("payment_method") ?: "cash",
        referenceNumber = getStringOrNull("reference_number"),
        notes = getStringOrNull("notes"),
        createdAt = getStringOrNull("created_at")
    )

    private fun Cursor.toEmployee() = Employee(
        id = getInt("id"),
        uuid = getString("uuid"),
        fullName = getString("full_name"),
        phone = getStringOrNull("phone"),
        email = getStringOrNull("email"),
        position = getStringOrNull("position"),
        salary = getDoubleOrNull("salary") ?: 0.0,
        hireDate = getStringOrNull("hire_date"),
        isActive = getIntOrNull("is_active") != 0,
        createdAt = getStringOrNull("created_at")
    )

    private fun Cursor.toShift() = Shift(
        id = getInt("id"),
        uuid = getString("uuid"),
        cashierId = getIntOrNull("cashier_id"),
        startTime = getStringOrNull("start_time"),
        endTime = getStringOrNull("end_time"),
        startingCash = getDoubleOrNull("starting_cash") ?: 0.0,
        endingCash = getDoubleOrNull("ending_cash") ?: 0.0,
        totalSales = getDoubleOrNull("total_sales") ?: 0.0,
        status = getStringOrNull("status") ?: "open",
        notes = getStringOrNull("notes"),
        createdAt = getStringOrNull("created_at")
    )

    private fun Cursor.toMaintenanceRequest() = MaintenanceRequest(
        id = getInt("id"),
        uuid = getString("uuid"),
        title = getString("title"),
        description = getStringOrNull("description"),
        equipmentType = getStringOrNull("equipment_type"),
        equipmentId = getIntOrNull("equipment_id"),
        priority = getStringOrNull("priority") ?: "medium",
        status = getStringOrNull("status") ?: "pending",
        requestedBy = getIntOrNull("requested_by"),
        assignedTo = getIntOrNull("assigned_to"),
        completedAt = getStringOrNull("completed_at"),
        createdAt = getStringOrNull("created_at")
    )

    private fun Cursor.toSmsMessage() = SmsMessage(
        id = getInt("id"),
        uuid = getString("uuid"),
        phoneNumber = getString("phone_number"),
        messageBody = getString("message_body"),
        messageType = getStringOrNull("message_type") ?: "incoming",
        status = getStringOrNull("status") ?: "pending",
        partyId = getIntOrNull("party_id"),
        sentAt = getStringOrNull("sent_at"),
        createdAt = getStringOrNull("created_at")
    )

    private fun Cursor.toSmsWhitelistEntry() = SmsWhitelistEntry(
        id = getInt("id"),
        phone = getString("phone"),
        name = getStringOrNull("name"),
        isActive = getIntOrNull("is_active") != 0,
        createdAt = getStringOrNull("created_at")
    )

    private fun Cursor.toNotification() = Notification(
        id = getInt("id"),
        title = getString("title"),
        message = getString("message"),
        type = getStringOrNull("type") ?: "general",
        isRead = getIntOrNull("is_read") != 0,
        createdAt = getStringOrNull("created_at")
    )

    private fun Cursor.toStockMovement() = StockMovement(
        id = getInt("id"),
        uuid = getString("uuid"),
        productId = getIntOrNull("product_id"),
        movementType = getStringOrNull("movement_type") ?: "in",
        quantity = getDoubleOrNull("quantity") ?: 0.0,
        unitCost = getDoubleOrNull("unit_cost") ?: 0.0,
        totalCost = getDoubleOrNull("total_cost") ?: 0.0,
        referenceType = getStringOrNull("reference_type"),
        referenceId = getIntOrNull("reference_id"),
        movementDate = getStringOrNull("movement_date"),
        notes = getStringOrNull("notes"),
        createdBy = getStringOrNull("created_by"),
        createdAt = getStringOrNull("created_at")
    )
}
