package infrastructure.persistence.entities
import infrastructure.persistence.base.BaseEntity


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

import com.example.app.data.converter.SyncConverters
import com.example.app.data.model.SyncStatus



@Entity(
    tableName = "receipts",

    foreignKeys = [

        ForeignKey(
            entity = PartyEntity::class,
            parentColumns = ["id"],
            childColumns = ["customer_party_id"],
            onDelete = ForeignKey.SET_NULL
        ),


        ForeignKey(
            entity = PaymentEntity::class,
            parentColumns = ["id"],
            childColumns = ["payment_id"],
            onDelete = ForeignKey.SET_NULL
        ),


        ForeignKey(
            entity = CashBoxEntity::class,
            parentColumns = ["id"],
            childColumns = ["cash_box_id"],
            onDelete = ForeignKey.SET_NULL
        ),


        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["created_by"],
            onDelete = ForeignKey.RESTRICT
        )
    ],


    indices = [

        Index(
            value = ["uuid"],
            unique = true
        ),

        Index(
            value = ["receipt_number"],
            unique = true
        ),


        Index(
            value = ["payment_id"]
        ),


        Index(
            value = [
                "cash_box_id",
                "is_deleted"
            ]
        ),


        Index(
            value = [
                "customer_party_id",
                "is_deleted"
            ]
        ),


        Index(
            value = ["created_at"]
        )
    ]
)


@TypeConverters(
    SyncConverters::class
)

data class ReceiptEntity(


    @PrimaryKey(
        autoGenerate = true
    )
    val id: Long = 0,


    @ColumnInfo(name = "uuid")
    override val uuid: String?,


    @ColumnInfo(name = "receipt_number")
    val receiptNumber: String,


    @ColumnInfo(name = "customer_party_id")
    val customerPartyId: Long?,


    @ColumnInfo(name = "payment_id")
    val paymentId: Long?,


    @ColumnInfo(name = "cash_box_id")
    val cashBoxId: Long?,


    @ColumnInfo(name = "receipt_type")
    val receiptType: String,


    @ColumnInfo(name = "received_from")
    val receivedFrom: String,


    @ColumnInfo(name = "received_by")
    val receivedBy: Long?,


    @ColumnInfo(name = "amount")
    val amount: Double,


    @ColumnInfo(name = "currency_id")
    val currencyId: Long?,


    @ColumnInfo(name = "cash_amount")
    val cashAmount: Double = 0.0,


    @ColumnInfo(name = "cheque_amount")
    val chequeAmount: Double = 0.0,


    @ColumnInfo(name = "bank_amount")
    val bankAmount: Double = 0.0,


    @ColumnInfo(name = "other_amount")
    val otherAmount: Double = 0.0,


    @ColumnInfo(name = "status")
    val status: String = "ACTIVE",



    // Audit

    @ColumnInfo(name = "created_by")
    override val createdBy: Long,


    @ColumnInfo(name = "created_at")
    override val createdAt: String,



    @ColumnInfo(name = "updated_at")
    override val updatedAt: String? = null,


    @ColumnInfo(name = "deleted_at")
    override val deletedAt: String? = null,


    @ColumnInfo(name = "is_deleted")
    override val isDeleted: Int = 0,



    // Sync

    @ColumnInfo(name = "sync_status")
    override val syncStatus: SyncStatus = SyncStatus.PENDING,


    @ColumnInfo(name = "sync_version")
    override val syncVersion: Int = 1,


    @ColumnInfo(name = "sync_at")
    override val syncAt: String? = null,


    @ColumnInfo(name = "device_id")
    override val deviceId: String? = null,



    @ColumnInfo(name = "remarks")
    override val remarks: String? = null,


    @ColumnInfo(name = "extra_data")
    override val extraData: String? = null,


    // Optimistic Lock

    @ColumnInfo(name = "row_version")
    override val rowVersion: Int = 1



) : BaseEntity(

    uuid = uuid,

    createdBy = createdBy,

    createdAt = createdAt,

    updatedAt = updatedAt,

    deletedAt = deletedAt,

    isDeleted = isDeleted,

    syncStatus = syncStatus,

    syncVersion = syncVersion,

    syncAt = syncAt,

    deviceId = deviceId,

    remarks = remarks,

    extraData = extraData,

    rowVersion = rowVersion
)
