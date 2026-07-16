package infrastructure.persistence.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "roles",
    foreignKeys = [
        ForeignKey(
            entity = RoleEntity::class,
            parentColumns = ["id"],
            childColumns = ["parent_role_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["uuid"], unique = true),
        Index(value = ["role_code"], unique = true),
        Index(value = ["parent_role_id"])
    ]
)
data class RoleEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "uuid") override val uuid: String? = null,
    @ColumnInfo(name = "role_code") val roleCode: String = "",
    @ColumnInfo(name = "role_name") val roleName: String = "",
    @ColumnInfo(name = "role_name_ar") val roleNameAr: String? = null,
    @ColumnInfo(name = "description") val description: String? = null,
    @ColumnInfo(name = "description_ar") val descriptionAr: String? = null,
    @ColumnInfo(name = "level") val level: Int = 1,
    @ColumnInfo(name = "parent_role_id") val parentRoleId: Long? = null,
    @ColumnInfo(name = "is_system_role") val isSystemRole: Int = 0,
    @ColumnInfo(name = "is_active") val isActive: Int = 1,
    @ColumnInfo(name = "created_at") override val createdAt: String? = null,
    @ColumnInfo(name = "updated_at") override val updatedAt: String? = null,
    @ColumnInfo(name = "deleted_at") override val deletedAt: String? = null,
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
    deletedAt = deletedAt,
    isDeleted = isDeleted,
    syncStatus = syncStatus,
    syncVersion = syncVersion,
    syncAt = syncAt,
    deviceId = deviceId,
    remarks = remarks,
    extraData = extraData
)
