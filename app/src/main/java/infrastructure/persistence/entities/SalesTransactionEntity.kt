package infrastructure.persistence.entities

import androidx.room.*
import infrastructure.persistence.base.BaseEntity
import infrastructure.persistence.types.SyncStatus


@Entity(
    tableName = "sales_transactions",
    indices = [
        Index(value=["uuid"],unique=true),
        Index(value=["invoice_number"],unique=true),
        Index(value=["customer_party_id"]),
        Index(value=["is_deleted"])
    ]
)
data class SalesTransactionEntity(

    @PrimaryKey(autoGenerate=true)
    val id:Long=0,


    override val uuid:String,


    @ColumnInfo(name="invoice_number")
    val invoiceNumber:String,


    @ColumnInfo(name="customer_party_id")
    val customerPartyId:Long?=null,


    @ColumnInfo(name="total_amount")
    val totalAmount:Double=0.0,


    @ColumnInfo(name="paid_amount")
    val paidAmount:Double=0.0,


    @ColumnInfo(name="status")
    val status:String="OPEN",


    override val createdBy:Long,

    override val createdAt:String,


    override val updatedBy:Long?=null,

    override val updatedAt:String?=null,


    override val deletedBy:Long?=null,

    override val deletedAt:String?=null,


    override val isDeleted:Int=0,


    override val syncStatus:SyncStatus=SyncStatus.PENDING,

    override val syncVersion:Int=1,

    override val syncAt:String?=null,

    override val deviceId:String?=null,

    override val rowVersion:Int=1,

    override val remarks:String?=null,

    override val extraData:String?=null


):BaseEntity(
    uuid,
    createdBy,
    createdAt,
    updatedBy,
    updatedAt,
    deletedBy,
    deletedAt,
    isDeleted,
    syncStatus,
    syncVersion,
    syncAt,
    deviceId,
    rowVersion,
    remarks,
    extraData
)
