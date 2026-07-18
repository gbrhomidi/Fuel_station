package infrastructure.persistence.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

import infrastructure.persistence.base.BaseEntity
import infrastructure.persistence.converters.SyncConverters
import infrastructure.persistence.types.SyncStatus


@Entity(
    tableName = "bank_accounts",

    foreignKeys = [
        ForeignKey(
            entity = CurrencyEntity::class,
            parentColumns = ["id"],
            childColumns = ["currency_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],

    indices = [
        Index(
            value = ["uuid"],
            unique = true
        ),

        Index(
            value = ["account_number"],
            unique = true
        ),

        Index(
            value = ["currency_id"]
        ),

        Index(
            value = ["is_deleted"]
        )
    ]
)

@TypeConverters(
    SyncConverters::class
)

data class BankAccountEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,


    // Identity

    @ColumnInfo(name = "uuid")
    override val uuid: String,


    // Bank Information

    @ColumnInfo(name = "account_number")
    val accountNumber: String,


    @ColumnInfo(name = "bank_name")
    val bankName: String,


    // Currency

    @ColumnInfo(name = "currency_id")
    val currencyId: Long? = null,


    @ColumnInfo(name = "currency_code")
    val currencyCode: String = "SAR",


    // ADR-012 Financial Precision
    // Stored as minor units to avoid floating point errors

    @ColumnInfo(name = "balance_minor")
    val balanceMinor: Long = 0,


    // Audit

    override val createdBy: Long,

    override val createdAt: String,


    override val updatedBy: Long? = null,

    override val updatedAt: String? = null,


    // Soft Delete
    // SQLite representation:
    // 0 = Active
    // 1 = Deleted

    override val isDeleted: Int = 0,

    override val deletedBy: Long? = null,

    override val deletedAt: String? = null,


    // Synchronization

    override val syncStatus: SyncStatus = SyncStatus.PENDING,


    override val syncVersion: Int = 1,


    override val syncAt: String? = null,


    override val deviceId: String? = null,


    // Optimistic Locking

    override val rowVersion: Int = 1,


    // Extra Metadata

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
