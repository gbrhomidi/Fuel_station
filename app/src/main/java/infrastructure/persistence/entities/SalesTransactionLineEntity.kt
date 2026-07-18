package infrastructure.persistence.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import infrastructure.persistence.base.BaseEntity
import infrastructure.persistence.types.SyncStatus


@Entity(
    tableName = "sales_transaction_lines",

    foreignKeys = [

        ForeignKey(
            entity = SalesTransactionEntity::class,
            parentColumns = ["id"],
            childColumns = ["transaction_id"],
            onDelete = ForeignKey.CASCADE
        ),

        ForeignKey(
            entity = FuelTypeEntity::class,
            parentColumns = ["id"],
            childColumns = ["fuel_type_id"],
            onDelete = ForeignKey.SET_NULL
        )

    ],

    indices = [

        Index(
            value = ["transaction_id"]
        ),

        Index(
            value = ["fuel_type_id"]
        ),

        Index(
            value = ["product_id"]
        ),

        Index(
            value = ["uuid"],
            unique = true
        ),

        Index(
            value = ["created_by"]
        ),

        Index(
            value = ["is_deleted"]
        )

    ]
)


data class SalesTransactionLineEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,


    // =========================
    // Identity
    // =========================

    @ColumnInfo(name = "uuid")
    override val uuid: String,


    // =========================
    // Parent Transaction
    // =========================

    @ColumnInfo(name = "transaction_id")
    val transactionId: Long,


    // =========================
    // Fuel / Product Reference
    // =========================

    @ColumnInfo(name = "fuel_type_id")
    val fuelTypeId: Long? = null,


    @ColumnInfo(name = "product_id")
    val productId: Long? = null,


    // =========================
    // Pump Information
    // =========================

    @ColumnInfo(name = "pump_id")
    val pumpId: Long? = null,


    @ColumnInfo(name = "nozzle_id")
    val nozzleId: Long? = null,


    // =========================
    // Quantity
    // =========================
    // ADR-012
    // Store money safely as minor units

    @ColumnInfo(name = "quantity_minor")
    val quantityMinor: Long = 0,


    // =========================
    // Money
    // =========================

    @ColumnInfo(name = "unit_price_minor")
    val unitPriceMinor: Long = 0,


    @ColumnInfo(name = "subtotal_minor")
    val subtotalMinor: Long = 0,


    @ColumnInfo(name = "discount_minor")
    val discountMinor: Long = 0,


    @ColumnInfo(name = "tax_minor")
    val taxMinor: Long = 0,


    @ColumnInfo(name = "total_minor")
    val totalMinor: Long = 0,


    @ColumnInfo(name = "currency_code")
    val currencyCode: String = "SAR",



    // =========================
    // Audit
    // =========================

    override val createdBy: Long,

    override val createdAt: String,


    override val updatedBy: Long? = null,

    override val updatedAt: String? = null,


    override val deletedBy: Long? = null,

    override val deletedAt: String? = null,


    // =========================
    // Soft Delete
    // =========================

    override val isDeleted: Int = 0,


    // =========================
    // Sync
    // =========================

    override val syncStatus: SyncStatus = SyncStatus.PENDING,


    override val syncVersion: Int = 1,


    override val syncAt: String? = null,


    override val deviceId: String? = null,


    // =========================
    // Optimistic Lock
    // =========================

    override val rowVersion: Int = 1,


    // =========================
    // Metadata
    // =========================

    override val remarks: String? = null,

    override val extraData: String? = null


) : BaseEntity(

    uuid = uuid,

    createdBy = createdBy,
    createdAt = createdAt,

    updatedBy = updatedBy,
    updatedAt = updatedAt,

    deletedBy = deletedBy,
    deletedAt = deletedAt,

    isDeleted = isDeleted,

    syncStatus = syncStatus,
    syncVersion = syncVersion,
    syncAt = syncAt,

    deviceId = deviceId,

    rowVersion = rowVersion,

    remarks = remarks,
    extraData = extraData
)
