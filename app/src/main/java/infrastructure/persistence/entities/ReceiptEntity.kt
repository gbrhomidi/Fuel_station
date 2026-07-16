package infrastructure.persistence.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import infrastructure.persistence.model.SyncStatus

/**
 * Receipt entity for Room database.
 *
 * ADR-011: Offline Optimistic Synchronization Contract.
 */
@Entity(tableName = "receipts")
data class ReceiptEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val receiptNumber: String,

    val totalAmount: Double,

    val paymentId: Long,

    val issuedAt: Long,

    val syncStatus: SyncStatus = SyncStatus.PENDING,

    val serverId: String? = null
)
