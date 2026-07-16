package com.example.app.data.converter

import androidx.room.TypeConverter
import com.example.app.data.model.SyncStatus


/**
 * Room converters.
 *
 * Database:
 * INTEGER
 *
 * Application:
 * Enum
 *
 * ADR-011:
 * Prevent string-based synchronization corruption.
 */
class SyncConverters {


    @TypeConverter
    fun fromSyncStatus(
        status: SyncStatus?
    ): Int? {

        return status?.code
    }



    @TypeConverter
    fun toSyncStatus(
        value: Int?
    ): SyncStatus? {

        return value?.let {

            SyncStatus.fromCode(it)

        }

    }
}
