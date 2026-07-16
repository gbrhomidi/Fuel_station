package infrastructure.persistence.entities

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

/**
 * الكيان الأساسي المجرد لجميع جداول قاعدة البيانات.
 * يحتوي على الحقول المشتركة الموجودة في جميع الجداول.
 */
abstract class BaseEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    open val id: Long = 0,

    @ColumnInfo(name = "uuid")
    open val uuid: String? = null,

    @ColumnInfo(name = "created_at")
    open val createdAt: String? = null,

    @ColumnInfo(name = "updated_at")
    open val updatedAt: String? = null,

    @ColumnInfo(name = "deleted_at")
    open val deletedAt: String? = null,

    @ColumnInfo(name = "is_deleted", defaultValue = "0")
    open val isDeleted: Int = 0,

    @ColumnInfo(name = "sync_status", defaultValue = "'synced'")
    open val syncStatus: String = "synced",

    @ColumnInfo(name = "sync_version", defaultValue = "1")
    open val syncVersion: Int = 1,

    @ColumnInfo(name = "sync_at")
    open val syncAt: String? = null,

    @ColumnInfo(name = "device_id")
    open val deviceId: String? = null,

    @ColumnInfo(name = "remarks")
    open val remarks: String? = null,

    @ColumnInfo(name = "extra_data")
    open val extraData: String? = null
)
