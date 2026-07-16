package infrastructure.persistence.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import infrastructure.persistence.model.SyncStatus

/**
 * CashBox entity for Room database.
 *
 * ADR-011: Offline Optimistic Synchronization Contract.
 */
@Entity(tableName = "cash_boxes")
data class CashBoxEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,

    val initialBalance: Double,

    val currentBalance: Double,

    val openedAt: Long,

    val closedAt: Long? = null,

    val syncStatus: SyncStatus = SyncStatus.PENDING,

    val serverId: String? = null
)
