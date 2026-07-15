package com.aistudio.dieselstationsms.kxmpzq.data.model

import java.util.UUID

data class User(
    val id: Int = 0,
    val username: String,
    val fullName: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val roleId: Int? = null,
    val isActive: Boolean = true,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

data class Role(
    val id: Int = 0,
    val name: String,
    val permissions: String? = null,
    val description: String? = null
)

data class AuthResult(
    val success: Boolean,
    val user: User? = null,
    val token: String? = null,
    val error: String? = null
)

data class Party(
    val id: Int = 0,
    val uuid: String = UUID.randomUUID().toString(),
    val name: String,
    val commercialName: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val address: String? = null,
    val balance: Double = 0.0,
    val creditLimit: Double = 0.0,
    val points: Int = 0,
    val vipLevel: Int = 0,
    val vehicleType: String? = null,
    val fleetSize: Int = 0,
    val isActive: Boolean = true,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

data class Product(
    val id: Int = 0,
    val uuid: String = UUID.randomUUID().toString(),
    val name: String,
    val categoryId: Int? = null,
    val sku: String? = null,
    val barcode: String? = null,
    val description: String? = null,
    val unitPrice: Double = 0.0,
    val costPrice: Double = 0.0,
    val quantity: Double = 0.0,
    val minStock: Double = 0.0,
    val isActive: Boolean = true,
    val createdAt: String? = null
)

data class Category(
    val id: Int = 0,
    val name: String,
    val description: String? = null,
    val parentId: Int? = null
)

data class FuelType(
    val id: Int = 0,
    val uuid: String = UUID.randomUUID().toString(),
    val name: String,
    val pricePerLiter: Double = 0.0,
    val description: String? = null,
    val isActive: Boolean = true
)

data class Tank(
    val id: Int = 0,
    val uuid: String = UUID.randomUUID().toString(),
    val name: String,
    val fuelTypeId: Int? = null,
    val capacity: Double = 0.0,
    val currentQuantity: Double = 0.0,
    val minLevel: Double = 0.0,
    val status: String = "active",
    val lastRefillDate: String? = null,
    val notes: String? = null
)

data class Pump(
    val id: Int = 0,
    val uuid: String = UUID.randomUUID().toString(),
    val name: String,
    val tankId: Int? = null,
    val fuelTypeId: Int? = null,
    val status: String = "active",
    val totalDispensed: Double = 0.0,
    val lastMaintenance: String? = null
)

data class Sale(
    val id: Int = 0,
    val uuid: String = UUID.randomUUID().toString(),
    val partyId: Int? = null,
    val productId: Int? = null,
    val fuelTypeId: Int? = null,
    val pumpId: Int? = null,
    val quantity: Double = 0.0,
    val pricePerLiter: Double = 0.0,
    val totalAmount: Double = 0.0,
    val paymentMethod: String = "cash",
    val status: String = "completed",
    val deliveryLocation: String? = null,
    val deliveryTime: String? = null,
    val driverId: Int? = null,
    val vehicleId: Int? = null,
    val orderType: String = "sale",
    val shiftId: Int? = null,
    val notes: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

data class FuelSale(
    val id: Int = 0,
    val uuid: String = UUID.randomUUID().toString(),
    val saleId: Int? = null,
    val shiftId: Int? = null,
    val pumpId: Int? = null,
    val fuelTypeId: Int? = null,
    val quantity: Double = 0.0,
    val pricePerLiter: Double = 0.0,
    val totalAmount: Double = 0.0,
    val paymentMethod: String = "cash",
    val customerId: Int? = null,
    val vehiclePlate: String? = null,
    val saleDate: String? = null,
    val saleTime: String? = null,
    val notes: String? = null
)

data class Delivery(
    val id: Int = 0,
    val uuid: String = UUID.randomUUID().toString(),
    val saleId: Int? = null,
    val partyId: Int? = null,
    val vehicleId: Int? = null,
    val driverId: Int? = null,
    val deliveryDate: String,
    val quantity: Double = 0.0,
    val fuelType: String = "diesel",
    val pricePerLiter: Double = 0.0,
    val totalAmount: Double = 0.0,
    val status: String = "delivered",
    val location: String? = null,
    val notes: String? = null
)

data class Payment(
    val id: Int = 0,
    val uuid: String = UUID.randomUUID().toString(),
    val partyId: Int? = null,
    val amount: Double = 0.0,
    val paymentMethod: String = "cash",
    val referenceNumber: String? = null,
    val notes: String? = null,
    val createdAt: String? = null
)

data class Deposit(
    val id: Int = 0,
    val uuid: String = UUID.randomUUID().toString(),
    val partyId: Int? = null,
    val amount: Double = 0.0,
    val depositMethod: String = "cash",
    val referenceNumber: String? = null,
    val notes: String? = null,
    val createdAt: String? = null
)

data class Employee(
    val id: Int = 0,
    val uuid: String = UUID.randomUUID().toString(),
    val fullName: String,
    val phone: String? = null,
    val email: String? = null,
    val position: String? = null,
    val salary: Double = 0.0,
    val hireDate: String? = null,
    val isActive: Boolean = true,
    val createdAt: String? = null
)

data class Shift(
    val id: Int = 0,
    val uuid: String = UUID.randomUUID().toString(),
    val cashierId: Int? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val startingCash: Double = 0.0,
    val endingCash: Double = 0.0,
    val totalSales: Double = 0.0,
    val status: String = "open",
    val notes: String? = null,
    val createdAt: String? = null
)

data class MaintenanceRequest(
    val id: Int = 0,
    val uuid: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String? = null,
    val equipmentType: String? = null,
    val equipmentId: Int? = null,
    val priority: String = "medium",
    val status: String = "pending",
    val requestedBy: Int? = null,
    val assignedTo: Int? = null,
    val completedAt: String? = null,
    val createdAt: String? = null
)

data class SmsMessage(
    val id: Int = 0,
    val uuid: String = UUID.randomUUID().toString(),
    val phoneNumber: String,
    val messageBody: String,
    val messageType: String = "incoming",
    val status: String = "pending",
    val partyId: Int? = null,
    val sentAt: String? = null,
    val createdAt: String? = null
)

data class SmsWhitelistEntry(
    val id: Int = 0,
    val phone: String,
    val name: String? = null,
    val isActive: Boolean = true,
    val createdAt: String? = null
)

data class Notification(
    val id: Int = 0,
    val title: String,
    val message: String,
    val type: String = "general",
    val isRead: Boolean = false,
    val createdAt: String? = null
)

data class AppSetting(
    val key: String,
    val value: String,
    val updatedAt: String? = null
)

data class DashboardData(
    val totalSales: Double = 0.0,
    val totalTransactions: Int = 0,
    val totalCustomers: Int = 0,
    val totalFuelDispensed: Double = 0.0,
    val activeShifts: Int = 0,
    val lowStockItems: Int = 0,
    val pendingMaintenance: Int = 0,
    val todayRevenue: Double = 0.0,
    val weeklyRevenue: Double = 0.0,
    val monthlyRevenue: Double = 0.0
)

data class DailySales(
    val date: String,
    val totalAmount: Double,
    val transactionCount: Int,
    val fuelDispensed: Double
)

data class EodReport(
    val date: String,
    val totalSales: Double,
    val totalPayments: Double,
    val totalDeposits: Double,
    val cashInHand: Double,
    val shiftCount: Int,
    val transactionCount: Int
)

data class MonthlySales(
    val month: String,
    val totalAmount: Double,
    val transactionCount: Int
)

data class ProfitReport(
    val fromDate: String,
    val toDate: String,
    val totalRevenue: Double,
    val totalCost: Double,
    val grossProfit: Double,
    val profitMargin: Double
)

data class AiInsight(
    val insight: String,
    val recommendations: List<String> = emptyList()
)

data class AiChatResponse(
    val response: String,
    val sessionId: String = "default"
)

data class StockMovement(
    val id: Int = 0,
    val uuid: String = UUID.randomUUID().toString(),
    val productId: Int? = null,
    val movementType: String = "in",
    val quantity: Double = 0.0,
    val unitCost: Double = 0.0,
    val totalCost: Double = 0.0,
    val referenceType: String? = null,
    val referenceId: Int? = null,
    val movementDate: String? = null,
    val notes: String? = null,
    val createdBy: String? = null,
    val createdAt: String? = null
)

data class ExportData(
    val parties: List<Party> = emptyList(),
    val products: List<Product> = emptyList(),
    val sales: List<Sale> = emptyList(),
    val payments: List<Payment> = emptyList(),
    val employees: List<Employee> = emptyList(),
    val generatedAt: String = ""
)
