package infrastructure.persistence.entities


import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "sales_transaction_lines",

    foreignKeys = [

        ForeignKey(
            entity = SalesTransactionEntity::class,
            parentColumns = ["id"],
            childColumns = ["transaction_id"],
            onDelete = ForeignKey.CASCADE
        )

    ],

    indices = [

        Index(value = ["transaction_id"])

    ]
)
data class SalesTransactionLineEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,


    val transaction_id: Long,


    val product_id: Long?,


    val fuel_type_id: Long?,


    val description: String?,


    val quantity: Double,


    val unit_price: Double,


    val discount_amount: Double = 0.0,


    val tax_amount: Double = 0.0,


    val line_total: Double,


    // Audit

    val created_at: Long = System.currentTimeMillis(),

    val updated_at: Long? = null,


    val created_by: Long? = null,

    val updated_by: Long? = null,


    val deleted_by: Long? = null,


    val is_deleted: Boolean = false,


    val row_version: Long = 1

)
