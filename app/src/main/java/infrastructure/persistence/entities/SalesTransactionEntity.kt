package infrastructure.persistence.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import infrastructure.persistence.base.BaseEntity
import infrastructure.persistence.types.SyncStatus


@Entity(
    tableName = "sales_transactions",
    indices = [
        Index(value = ["uuid"], unique = true),
        Index(value = ["invoice_number"], unique = true),
        Index(value = ["customer_party_id"]),
        Index(value = ["currency_id"]),
        Index(value = ["created_by"]),
        Index(value = ["is_deleted"])
    ]
)
data class SalesTransactionEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,


    // =========================
    // Identity
    // =========================

    @ColumnInfo(name = "uuid")
    override val uuid: String,


    @ColumnInfo(name = "invoice_number")
    val invoiceNumber: String,


    // =========================
    // Relations
    // =========================

    @ColumnInfo(name = "customer_party_id")
    val customerPartyId: Long? = null,


    @ColumnInfo(name = "currency_id")
    val currencyId: Long? = null,


    // =========================
    // Money Contract ADR-012
    // =========================

    @ColumnInfo(name = "total_amount_minor")
    val totalAmountMinor: Long = 0,


    @ColumnInfo(name = "paid_amount_minor")
    val paidAmountMinor: Long = 0,


    @ColumnInfo(name = "currency_code")
    val currencyCode: String = "SAR",


    // =========================
    // State
    // =========================

    @ColumnInfo(name = "status")
    val status: String = "OPEN",


    // =========================
    // Audit
    // =========================

    @ColumnInfo(name = "created_by")
    override val createdBy: Long,


    @ColumnInfo(name = "created_at")
    override val createdAt: String,


    @ColumnInfo(name = "updated_by")
    override val updatedBy: Long? = null,


    @ColumnInfo(name = "updated_at")
    override val updatedAt: String? = null,


    @ColumnInfo(name = "deleted_by")
    override val deletedBy: Long? = null,


    @ColumnInfo(name = "deleted_at")
    override val deletedAt: String? = null,


    // =========================
    // Soft Delete
    // =========================

    @ColumnInfo(name = "is_deleted")
    override val isDeleted: Int = 0,


    // =========================
    // Sync
    // =========================

    @ColumnInfo(name = "sync_status")
    override val syncStatus: SyncStatus = SyncStatus.PENDING,


    @ColumnInfo(name = "sync_version")
    override val syncVersion: Int = 1,


    @ColumnInfo(name = "sync_at")
    override val syncAt: String? = null,


    @ColumnInfo(name = "device_id")
    override val deviceId: String? = null,


    // =========================
    // Optimistic Lock
    // =========================

    @ColumnInfo(name = "row_version")
    override val rowVersion: Int = 1,


    // =========================
    // Metadata
    // =========================

    @ColumnInfo(name = "remarks")
    override val remarks: String? = null,


    @ColumnInfo(name = "extra_data")
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
