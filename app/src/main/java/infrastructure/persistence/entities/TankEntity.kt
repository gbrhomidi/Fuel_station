package infrastructure.persistence.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tanks",
    foreignKeys = [
        ForeignKey(
            entity = StationEntity::class,
            parentColumns = ["id"],
            childColumns = ["station_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = FuelTypeEntity::class,
            parentColumns = ["id"],
            childColumns = ["fuel_type_id"],
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
        Index(value = ["tank_code"], unique = true)
    ]
)
data class TankEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "uuid")
    val uuid: String,

    @ColumnInfo(name = "tank_code")
    val tankCode: String,

    @ColumnInfo(name = "tank_name")
    val tankName: String,

    @ColumnInfo(name = "tank_name_ar")
    val tankNameAr: String? = null,

    @ColumnInfo(name = "station_id")
    val stationId: Long,

    @ColumnInfo(name = "fuel_type_id")
    val fuelTypeId: Long,

    @ColumnInfo(name = "capacity_liters")
    val capacityLiters: Double,

    @ColumnInfo(name = "minimum_level")
    val minimumLevel: Double = 500.0,

    @ColumnInfo(name = "maximum_level")
    val maximumLevel: Double? = null,

    @ColumnInfo(name = "current_quantity")
    val currentQuantity: Double = 0.0,

    @ColumnInfo(name = "usable_capacity")
    val usableCapacity: Double? = null,

    @ColumnInfo(name = "dead_volume")
    val deadVolume: Double = 0.0,

    @ColumnInfo(name = "tank_shape")
    val tankShape: String = "cylindrical",

    @ColumnInfo(name = "length_meters")
    val lengthMeters: Double? = null,

    @ColumnInfo(name = "diameter_meters")
    val diameterMeters: Double? = null,

    @ColumnInfo(name = "height_meters")
    val heightMeters: Double? = null,

    @ColumnInfo(name = "location")
    val location: String? = null,

    @ColumnInfo(name = "installation_date")
    val installationDate: String? = null,

    @ColumnInfo(name = "manufacturer")
    val manufacturer: String? = null,

    @ColumnInfo(name = "serial_number")
    val serialNumber: String? = null,

    @ColumnInfo(name = "model")
    val model: String? = null,

    @ColumnInfo(name = "sensor_serial")
    val sensorSerial: String? = null,

    @ColumnInfo(name = "sensor_type")
    val sensorType: String? = null,

    @ColumnInfo(name = "sensor_calibration_date")
    val sensorCalibrationDate: String? = null,

    @ColumnInfo(name = "sensor_accuracy")
    val sensorAccuracy: Double? = null,

    @ColumnInfo(name = "leak_detection")
    val leakDetection: Int = 0,

    @ColumnInfo(name = "overfill_protection")
    val overfillProtection: Int = 0,

    @ColumnInfo(name = "emergency_valve")
    val emergencyValve: Int = 0,

    @ColumnInfo(name = "last_inspection_date")
    val lastInspectionDate: String? = null,

    @ColumnInfo(name = "next_inspection_date")
    val nextInspectionDate: String? = null,

    @ColumnInfo(name = "inspection_certificate")
    val inspectionCertificate: String? = null,

    @ColumnInfo(name = "status")
    val status: String = "active",

    @ColumnInfo(name = "status_reason")
    val statusReason: String? = null,

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
