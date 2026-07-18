package infrastructure.persistence.entities

import androidx.room.*
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
        )
    ]
)


@TypeConverters(
    SyncConverters::class
)
data class BankAccountEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,


    @ColumnInfo(name="uuid")
    override val uuid: String,


    @ColumnInfo(name="account_number")
    val accountNumber:String,


    @ColumnInfo(name="bank_name")
    val bankName:String,


    @ColumnInfo(name="currency_id")
    val currencyId:Long? = null,


    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean = false,
    
    // ADR-012

    @ColumnInfo(name="balance_minor")
    val balanceMinor:Long = 0,


    @ColumnInfo(name="currency_code")
    val currencyCode:String = "SAR",


    // Audit

    override val createdBy:Long,


    override val createdAt:String,


    override val updatedBy:Long? = null,


    override val updatedAt:String? = null,


    override val deletedBy:Long? = null,


    override val deletedAt:String? = null,


    override val isDeleted:Int = 0,


    // Sync

    override val syncStatus:SyncStatus = SyncStatus.PENDING,


    override val syncVersion:Int = 1,


    override val syncAt:String? = null,


    override val deviceId:String? = null,


    // Lock

    override val rowVersion:Int = 1,


    override val remarks:String? = null,


    override val extraData:String? = null


):BaseEntity(

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
