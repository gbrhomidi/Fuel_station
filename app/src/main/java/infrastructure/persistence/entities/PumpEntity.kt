package infrastructure.persistence.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pumps",
    foreignKeys = [
        ForeignKey(
            entity = StationEntity::class,
            parentColumns = ["id"],
            childColumns = ["station_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = TankEntity::class,
            parentColumns = ["id"],
            childColumns = ["tank_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["created_by"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["uuid"], unique = true),
        Index(value = ["pump_code"], unique = true)
    ]
)
data class PumpEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "uuid")
    val uuid: String,

    @ColumnInfo(name = "pump_code")
    val pumpCode: String,

    @ColumnInfo(name = "pump_number")
    val pumpNumber: String,

    @ColumnInfo(name = "pump_name")
    val pumpName: String? = null,

    @ColumnInfo(name = "pump_name_ar")
    val pumpNameAr: String? = null,

    @ColumnInfo(name = "station_id")
    val stationId: Long,

    @ColumnInfo(name = "tank_id")
    val tankId: Long,

    @ColumnInfo(name = "serial_number")
    val serialNumber: String? = null,

    @ColumnInfo(name = "manufacturer")
    val manufacturer: String? = null,

    @ColumnInfo(name = "model")
    val model: String? = null,

    @ColumnInfo(name = "installation_date")
    val installationDate: String? = null,

    @ColumnInfo(name = "max_flow_rate")
    val maxFlowRate: Double? = null,

    @ColumnInfo(name = "meter_start")
    val meterStart: Double = 0.0,

    @ColumnInfo(name = "meter_current")
    val meterCurrent: Double = 0.0,

    @ColumnInfo(name = "meter_last_reset")
    val meterLastReset: String? = null,

    @ColumnInfo(name = "status")
    val status: String = "active",

    @ColumnInfo(name = "status_reason")
    val statusReason: String? = null,

    @ColumnInfo(name = "last_maintenance")
    val lastMaintenance: String? = null,

    @ColumnInfo(name = "next_maintenance")
    val nextMaintenance: String? = null,

    @ColumnInfo(name = "maintenance_interval")
    val maintenanceInterval: Int = 90,

    @ColumnInfo(name = "created_at")
    val createdAt: String? = null,

    @ColumnInfo(name = "updated_at")
    val updatedAt: String? = null,

    @ColumnInfo(name = "deleted_at")
    val deletedAt: String? = null,

    @ColumnInfo(name = "created_by")
    val createdBy: Long? = null,

    @ColumnInfo(name = "updated_by")
    val updatedBy: Long? = null,

    @ColumnInfo(name = "deleted_by")
    val deletedBy: Long? = null,

    @ColumnInfo(name = "is_deleted")
    val isDeleted: Int = 0,

    @ColumnInfo(name = "sync_status")
    val syncStatus: String = "synced",

    @ColumnInfo(name = "sync_version")
    val syncVersion: Int = 1,

    @ColumnInfo(name = "sync_at")
    val syncAt: String? = null,

    @ColumnInfo(name = "device_id")
    val deviceId: String? = null,

    @ColumnInfo(name = "remarks")
    val remarks: String? = null,

    @ColumnInfo(name = "extra_data")
    val extraData: String? = null
)
