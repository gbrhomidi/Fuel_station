package com.aistudio.dieselstationsms.kxmpzq

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "fuel_station.db"
        private const val DATABASE_VERSION = 12
        private const val TAG = "DatabaseHelper"
    }

    override fun onCreate(db: SQLiteDatabase) {
        Log.d(TAG, "Creating database tables...")

        db.execSQL("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE, password TEXT, full_name TEXT, email TEXT, phone TEXT, role_id INTEGER, is_active INTEGER, created_at TEXT, updated_at TEXT, is_deleted INTEGER DEFAULT 0)")
        db.execSQL("CREATE TABLE IF NOT EXISTS roles (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE, permissions TEXT, description TEXT)")
        db.execSQL("CREATE TABLE IF NOT EXISTS parties (id INTEGER PRIMARY KEY AUTOINCREMENT, uuid TEXT, name TEXT, commercial_name TEXT, phone TEXT, email TEXT, address TEXT, balance REAL, credit_limit REAL, points INTEGER, vip_level INTEGER, vehicle_type TEXT, fleet_size INTEGER, is_active INTEGER, created_at TEXT, updated_at TEXT, is_deleted INTEGER DEFAULT 0)")
        db.execSQL("CREATE TABLE IF NOT EXISTS products (id INTEGER PRIMARY KEY AUTOINCREMENT, uuid TEXT, name TEXT, category_id INTEGER, sku TEXT, barcode TEXT, description TEXT, unit_price REAL, cost_price REAL, quantity REAL, min_stock REAL, is_active INTEGER, created_at TEXT, is_deleted INTEGER DEFAULT 0)")
        db.execSQL("CREATE TABLE IF NOT EXISTS categories (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, description TEXT, parent_id INTEGER)")
        db.execSQL("CREATE TABLE IF NOT EXISTS fuel_types (id INTEGER PRIMARY KEY AUTOINCREMENT, uuid TEXT, name TEXT, price_per_liter REAL, description TEXT, is_active INTEGER, is_deleted INTEGER DEFAULT 0)")
        db.execSQL("CREATE TABLE IF NOT EXISTS tanks (id INTEGER PRIMARY KEY AUTOINCREMENT, uuid TEXT, name TEXT, fuel_type_id INTEGER, capacity REAL, current_quantity REAL, min_level REAL, status TEXT, last_refill_date TEXT, notes TEXT, is_deleted INTEGER DEFAULT 0)")
        db.execSQL("CREATE TABLE IF NOT EXISTS pumps (id INTEGER PRIMARY KEY AUTOINCREMENT, uuid TEXT, name TEXT, tank_id INTEGER, fuel_type_id INTEGER, status TEXT, total_dispensed REAL, last_maintenance TEXT, is_deleted INTEGER DEFAULT 0)")
        db.execSQL("CREATE TABLE IF NOT EXISTS sales_transactions (id INTEGER PRIMARY KEY AUTOINCREMENT, uuid TEXT, party_id INTEGER, product_id INTEGER, fuel_type_id INTEGER, pump_id INTEGER, quantity REAL, price_per_liter REAL, total_amount REAL, payment_method TEXT, status TEXT, delivery_location TEXT, delivery_time TEXT, driver_id INTEGER, vehicle_id INTEGER, order_type TEXT, shift_id INTEGER, notes TEXT, created_at TEXT, updated_at TEXT, is_deleted INTEGER DEFAULT 0)")
        db.execSQL("CREATE TABLE IF NOT EXISTS fuel_sales (id INTEGER PRIMARY KEY AUTOINCREMENT, uuid TEXT, sale_id INTEGER, shift_id INTEGER, pump_id INTEGER, fuel_type_id INTEGER, quantity REAL, price_per_liter REAL, total_amount REAL, payment_method TEXT, customer_id INTEGER, vehicle_plate TEXT, sale_date TEXT, sale_time TEXT, notes TEXT, is_deleted INTEGER DEFAULT 0)")
        db.execSQL("CREATE TABLE IF NOT EXISTS deliveries (id INTEGER PRIMARY KEY AUTOINCREMENT, uuid TEXT, sale_id INTEGER, party_id INTEGER, vehicle_id INTEGER, driver_id INTEGER, delivery_date TEXT, quantity REAL, fuel_type TEXT, price_per_liter REAL, total_amount REAL, status TEXT, location TEXT, notes TEXT, is_deleted INTEGER DEFAULT 0)")
        db.execSQL("CREATE TABLE IF NOT EXISTS payments (id INTEGER PRIMARY KEY AUTOINCREMENT, uuid TEXT, party_id INTEGER, amount REAL, payment_method TEXT, reference_number TEXT, notes TEXT, created_at TEXT, is_deleted INTEGER DEFAULT 0)")
        db.execSQL("CREATE TABLE IF NOT EXISTS deposits (id INTEGER PRIMARY KEY AUTOINCREMENT, uuid TEXT, party_id INTEGER, amount REAL, deposit_method TEXT, reference_number TEXT, notes TEXT, created_at TEXT, is_deleted INTEGER DEFAULT 0)")
        db.execSQL("CREATE TABLE IF NOT EXISTS employees (id INTEGER PRIMARY KEY AUTOINCREMENT, uuid TEXT, full_name TEXT, phone TEXT, email TEXT, position TEXT, salary REAL, hire_date TEXT, is_active INTEGER, created_at TEXT, is_deleted INTEGER DEFAULT 0)")
        db.execSQL("CREATE TABLE IF NOT EXISTS shifts (id INTEGER PRIMARY KEY AUTOINCREMENT, uuid TEXT, cashier_id INTEGER, start_time TEXT, end_time TEXT, starting_cash REAL, ending_cash REAL, total_sales REAL, status TEXT, notes TEXT, created_at TEXT, is_deleted INTEGER DEFAULT 0)")
        db.execSQL("CREATE TABLE IF NOT EXISTS maintenance_requests (id INTEGER PRIMARY KEY AUTOINCREMENT, uuid TEXT, title TEXT, description TEXT, equipment_type TEXT, equipment_id INTEGER, priority TEXT, status TEXT, requested_by INTEGER, assigned_to INTEGER, completed_at TEXT, created_at TEXT, is_deleted INTEGER DEFAULT 0)")
        db.execSQL("CREATE TABLE IF NOT EXISTS sms_messages (id INTEGER PRIMARY KEY AUTOINCREMENT, uuid TEXT, phone_number TEXT, message_body TEXT, message_type TEXT, status TEXT, party_id INTEGER, sent_at TEXT, created_at TEXT)")
        db.execSQL("CREATE TABLE IF NOT EXISTS sms_whitelist (id INTEGER PRIMARY KEY AUTOINCREMENT, phone TEXT UNIQUE, name TEXT, is_active INTEGER, created_at TEXT)")
        db.execSQL("CREATE TABLE IF NOT EXISTS notifications (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, message TEXT, type TEXT, is_read INTEGER, created_at TEXT)")
        db.execSQL("CREATE TABLE IF NOT EXISTS settings (key TEXT PRIMARY KEY, value TEXT, updated_at TEXT)")
        db.execSQL("CREATE TABLE IF NOT EXISTS stock_movements (id INTEGER PRIMARY KEY AUTOINCREMENT, uuid TEXT, product_id INTEGER, movement_type TEXT, quantity REAL, unit_cost REAL, total_cost REAL, reference_type TEXT, reference_id INTEGER, movement_date TEXT, notes TEXT, created_by TEXT, created_at TEXT, is_deleted INTEGER DEFAULT 0)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d(TAG, "Upgrading database from $oldVersion to $newVersion")
        db.execSQL("DROP TABLE IF EXISTS users")
        db.execSQL("DROP TABLE IF EXISTS roles")
        db.execSQL("DROP TABLE IF EXISTS parties")
        db.execSQL("DROP TABLE IF EXISTS products")
        db.execSQL("DROP TABLE IF EXISTS categories")
        db.execSQL("DROP TABLE IF EXISTS fuel_types")
        db.execSQL("DROP TABLE IF EXISTS tanks")
        db.execSQL("DROP TABLE IF EXISTS pumps")
        db.execSQL("DROP TABLE IF EXISTS sales_transactions")
        db.execSQL("DROP TABLE IF EXISTS fuel_sales")
        db.execSQL("DROP TABLE IF EXISTS deliveries")
        db.execSQL("DROP TABLE IF EXISTS payments")
        db.execSQL("DROP TABLE IF EXISTS deposits")
        db.execSQL("DROP TABLE IF EXISTS employees")
        db.execSQL("DROP TABLE IF EXISTS shifts")
        db.execSQL("DROP TABLE IF EXISTS maintenance_requests")
        db.execSQL("DROP TABLE IF EXISTS sms_messages")
        db.execSQL("DROP TABLE IF EXISTS sms_whitelist")
        db.execSQL("DROP TABLE IF EXISTS notifications")
        db.execSQL("DROP TABLE IF EXISTS settings")
        db.execSQL("DROP TABLE IF EXISTS stock_movements")
        onCreate(db)
    }

    fun getReadableDatabase(): SQLiteDatabase = this.readableDatabase
    fun getWritableDatabase(): SQLiteDatabase = this.writableDatabase

    fun authenticateUser(username: String, password: String): Cursor? {
        val db = readableDatabase
        return db.rawQuery(
            "SELECT * FROM users WHERE username = ? AND password = ? AND (is_deleted = 0 OR is_deleted IS NULL)",
            arrayOf(username, password)
        )
    }

    fun insert(table: String, nullColumnHack: String?, values: ContentValues): Long {
        return writableDatabase.insert(table, nullColumnHack, values)
    }

    fun update(table: String, values: ContentValues, whereClause: String?, whereArgs: Array<String>?): Int {
        return writableDatabase.update(table, values, whereClause, whereArgs)
    }

    fun delete(table: String, whereClause: String?, whereArgs: Array<String>?): Int {
        return writableDatabase.delete(table, whereClause, whereArgs)
    }

    fun execSQL(sql: String, bindArgs: Array<String>?) {
        writableDatabase.execSQL(sql, bindArgs)
    }

    fun rawQuery(sql: String, selectionArgs: Array<String>?): Cursor {
        return readableDatabase.rawQuery(sql, selectionArgs)
    }
}
