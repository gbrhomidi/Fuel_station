package infrastructure.persistence.converters

import androidx.room.TypeConverter
import infrastructure.persistence.types.SyncStatus

/**
 * Room converters for SyncStatus.
 * 
 * Database: INTEGER (NOT NULL, DEFAULT 0)
 * Application: Enum
 * 
 * ADR-011: Prevent string-based synchronization corruption.
 * 
 * NOTE: Non-null by contract. All records must have SyncStatus.
 * Default: PENDING (code=0) for new records.
 * Legacy null handling belongs in Migration, not here.
 */
class SyncConverters {

    @TypeConverter
    fun fromSyncStatus(
        status: SyncStatus
    ): Int = status.code

    @TypeConverter
    fun toSyncStatus(
        value: Int
    ): SyncStatus = SyncStatus.fromCode(value)
}
