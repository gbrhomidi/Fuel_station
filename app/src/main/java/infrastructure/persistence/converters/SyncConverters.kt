package infrastructure.persistence.converters

import androidx.room.TypeConverter
import infrastructure.persistence.types.SyncStatus

/**
 * Room converters.
 * Database: INTEGER
 * Application: Enum
 * ADR-011: Prevent string-based synchronization corruption.
 */
class SyncConverters {

    @TypeConverter
    fun fromSyncStatus(
        status: SyncStatus?
    ): Int? = status?.code

    @TypeConverter
    fun toSyncStatus(
        value: Int?
    ): SyncStatus? = value?.let(SyncStatus::fromCode)
}
