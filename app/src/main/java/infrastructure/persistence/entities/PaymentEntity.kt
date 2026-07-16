package infrastructure.persistence.entities


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

import infrastructure.persistence.converter.SyncConverters
import infrastructure.persistence.model.SyncStatus



/**
 * PaymentEntity
 *
 * Financial payment persistence model.
 *
 * Architectural Contracts:
 *
 * ADR-007:
 * Database schema ownership belongs to persistence layer.
 *
 * ADR-009:
 * Audit trail must remain immutable.
 *
 * ADR-011:
 * rowVersion enables optimistic locking.
 *
 * ADR-012:
 * Repository owns transactions.
 */
@Entity(
    tableName = "payments",

    foreignKeys = [

        ForeignKey(
            entity = SalesTransactionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sale_id"],
            onDelete = ForeignKey.SET_NULL
        ),


        ForeignKey(
            entity = PartyEntity::class,
            parentColumns = ["id"],
            childColumns = ["customer_party_id"],
            onDelete = ForeignKey.SET_NULL
        ),


        ForeignKey(
            entity = PartyEntity::class,
            parentColumns = ["id"],
            childColumns = ["supplier_party_id"],
            onDelete = ForeignKey.SET_NULL
        ),


        ForeignKey(
            entity = CurrencyEntity::class,
            parentColumns = ["id"],
            childColumns = ["currency_id"],
            onDelete = ForeignKey.SET_NULL
        ),


        ForeignKey(
            entity = BankAccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["bank_account_id"],
            onDelete = ForeignKey.SET_NULL
        ),


        ForeignKey(
            entity = CashBoxEntity::class,
            parentColumns = ["id"],
            childColumns = ["cash_box_id"],
            onDelete = ForeignKey.SET_NULL
        ),


        ForeignKey(
            entity = PaymentEntity::class,
            parentColumns = ["id"],
            childColumns = ["original_payment_id"],
            onDelete = ForeignKey.SET_NULL
        ),


        /**
         * Audit ownership.
         *
         * Financial records cannot lose creator identity.
         */
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
            value = ["payment_code"],
            unique = true
        ),


        Index(
            value = [
                "cash_box_id",
                "is_deleted"
            ]
        ),


        Index(
            value = [
                "sale_id",
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
        ),


        Index(
            value = ["status"]
        )
    ]
)


@TypeConverters(SyncConverters::class)
data class PaymentEntity(


    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    override val id: Long = 0,


    @ColumnInfo(name = "uuid")
    override val uuid: String,


    // =========================
    // Payment Information
    // =========================


    @ColumnInfo(name = "payment_code")
    val paymentCode: String,


    @ColumnInfo(name = "sale_id")
    val saleId: Long? = null,


    @ColumnInfo(name = "customer_party_id")
    val customerPartyId: Long? = null,


    @ColumnInfo(name = "supplier_party_id")
    val supplierPartyId: Long? = null,


    @ColumnInfo(name = "payment_type")
    val paymentType: String,


    @ColumnInfo(name = "payment_method")
    val paymentMethod: String,


    /**
     * Storage representation only.
     *
     * Domain converts to BigDecimal.
     */
    @ColumnInfo(name = "amount")
    val amount: Double,


    @ColumnInfo(name = "currency_id")
    val currencyId: Long? = null,


    @ColumnInfo(name = "exchange_rate")
    val exchangeRate: Double = 1.0,


    @ColumnInfo(name = "amount_in_default")
    val amountInDefault: Double? = null,


    @ColumnInfo(name = "is_partial")
    val isPartial: Int = 0,


    @ColumnInfo(name = "total_invoice_amount")
    val totalInvoiceAmount: Double? = null,


    @ColumnInfo(name = "remaining_after")
    val remainingAfter: Double? = null,


    // =========================
    // Payment Methods Metadata
    // =========================


    @ColumnInfo(name = "cheque_number")
    val chequeNumber: String? = null,


    @ColumnInfo(name = "cheque_date")
    val chequeDate: String? = null,


    @ColumnInfo(name = "cheque_status")
    val chequeStatus: String = "pending",


    @ColumnInfo(name = "bank_account_id")
    val bankAccountId: Long? = null,


    @ColumnInfo(name = "transfer_reference")
    val transferReference: String? = null,


    @ColumnInfo(name = "card_last_four")
    val cardLastFour: String? = null,


    @ColumnInfo(name = "transaction_id")
    val transactionId: String? = null,


    // =========================
    // CashBox
    // =========================


    @ColumnInfo(name = "cash_box_id")
    val cashBoxId: Long? = null,


    // =========================
    // Refund
    // =========================


    @ColumnInfo(name = "status")
    val status: String = "completed",


    @ColumnInfo(name = "is_refund")
    val isRefund: Int = 0,


    @ColumnInfo(name = "original_payment_id")
    val originalPaymentId: Long? = null,


    @ColumnInfo(name = "refund_reason")
    val refundReason: String? = null,


    @ColumnInfo(name = "notes")
    val notes: String? = null,



    // =========================
    // Audit Contract
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


    @ColumnInfo(name = "is_deleted")
    override val isDeleted: Int = 0,



    // =========================
    // Sync Contract
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
    // Optimistic Locking
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

    id = id,

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
