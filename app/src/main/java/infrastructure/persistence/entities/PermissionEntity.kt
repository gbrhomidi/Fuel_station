package infrastructure.persistence.entities
import infrastructure.persistence.base.BaseEntity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "permissions",
    indices = [
        Index(value = ["uuid"], unique = true),
        Index(value = ["permission_code"], unique = true),
        Index(value = ["module"])
    ]
)
data class PermissionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "uuid") override val uuid: String? = null,
    @ColumnInfo(name = "permission_code") val permissionCode: String = "",
    @ColumnInfo(name = "permission_name") val permissionName: String = "",
    @ColumnInfo(name = "permission_name_ar") val permissionNameAr: String? = null,
    @ColumnInfo(name = "description") val description: String? = null,
    @ColumnInfo(name = "module") val module: String = "",
    @ColumnInfo(name = "module_name_ar") val moduleNameAr: String? = null,
    @ColumnInfo(name = "action") val action: String = "",
    @ColumnInfo(name = "requires_station") val requiresStation: Int = 0,
    @ColumnInfo(name = "requires_branch") val requiresBranch: Int = 0,
    @ColumnInfo(name = "is_active") val isActive: Int = 1,
    @ColumnInfo(name = "created_at") override val createdAt: String? = null,
    @ColumnInfo(name = "updated_at") override val updatedAt: String? = null,
    @ColumnInfo(name = "is_deleted") override val isDeleted: Int = 0,
    @ColumnInfo(name = "sync_status") override val syncStatus: String = "synced",
    @ColumnInfo(name = "sync_version") override val syncVersion: Int = 1,
    @ColumnInfo(name = "sync_at") override val syncAt: String? = null,
    @ColumnInfo(name = "device_id") override val deviceId: String? = null,
    @ColumnInfo(name = "remarks") override val remarks: String? = null,
    @ColumnInfo(name = "extra_data") override val extraData: String? = null
) : BaseEntity(
    id = id,
    uuid = uuid,
    createdAt = createdAt,
    updatedAt = updatedAt,
    isDeleted = isDeleted,
    syncStatus = syncStatus,
    syncVersion = syncVersion,
    syncAt = syncAt,
    deviceId = deviceId,
    remarks = remarks,
    extraData = extraData
)
