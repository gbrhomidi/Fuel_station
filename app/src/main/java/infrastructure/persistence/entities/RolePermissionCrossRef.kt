package infrastructure.persistence.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "role_permissions",
    foreignKeys = [
        ForeignKey(
            entity = RoleEntity::class,
            parentColumns = ["id"],
            childColumns = ["role_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PermissionEntity::class,
            parentColumns = ["id"],
            childColumns = ["permission_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = StationEntity::class,
            parentColumns = ["id"],
            childColumns = ["station_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["uuid"], unique = true),
        Index(value = ["role_id", "permission_id", "station_id"], unique = true)
    ]
)
data class RolePermissionCrossRef(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "uuid") override val uuid: String? = null,
    @ColumnInfo(name = "role_id") val roleId: Long = 0,
    @ColumnInfo(name = "permission_id") val permissionId: Long = 0,
    @ColumnInfo(name = "station_id") val stationId: Long? = null,
    @ColumnInfo(name = "branch_id") val branchId: Long? = null,
    @ColumnInfo(name = "can_create") val canCreate: Int = 0,
    @ColumnInfo(name = "can_read") val canRead: Int = 1,
    @ColumnInfo(name = "can_update") val canUpdate: Int = 0,
    @ColumnInfo(name = "can_delete") val canDelete: Int = 0,
    @ColumnInfo(name = "can_export") val canExport: Int = 0,
    @ColumnInfo(name = "can_print") val canPrint: Int = 0,
    @ColumnInfo(name = "can_approve") val canApprove: Int = 0,
    @ColumnInfo(name = "created_at") override val createdAt: String? = null,
    @ColumnInfo(name = "updated_at") override val updatedAt: String? = null,
    @ColumnInfo(name = "created_by") val createdBy: Long? = null,
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
