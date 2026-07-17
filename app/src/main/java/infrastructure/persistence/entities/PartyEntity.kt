package infrastructure.persistence.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import infrastructure.persistence.base.BaseEntity
import infrastructure.persistence.types.SyncStatus


@Entity(
    tableName = "parties",

    indices = [
        Index(
            value = ["uuid"],
            unique = true
        ),

        Index(
            value = ["party_code"],
            unique = true
        ),

        Index(
            value = ["is_deleted"]
        )
    ]
)
data class PartyEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,


    @ColumnInfo(name = "uuid")
    override val uuid: String,


    @ColumnInfo(name = "party_code")
    val partyCode: String,


    @ColumnInfo(name = "party_type")
    val partyType: String,


    @ColumnInfo(name = "name")
    val name: String,


    @ColumnInfo(name = "phone")
    val phone: String? = null,


    @ColumnInfo(name = "email")
    val email: String? = null,


    @ColumnInfo(name = "created_by")
    override val createdBy: Long,


    @ColumnInfo(name = "created_at")
    override val createdAt: String,


    @ColumnInfo(name = "updated_at")
    override val updatedAt: String? = null,


    @ColumnInfo(name = "deleted_at")
    override val deletedAt: String? = null,


    @ColumnInfo(name = "is_deleted")
    override val isDeleted: Int = 0,


    @ColumnInfo(name = "sync_status")
    override val syncStatus: SyncStatus = SyncStatus.PENDING,


    @ColumnInfo(name = "sync_version")
    override val syncVersion: Int = 1,


    @ColumnInfo(name = "sync_at")
    override val syncAt: String? = null,


    @ColumnInfo(name = "device_id")
    override val deviceId: String? = null,


    @ColumnInfo(name = "row_version")
    override val rowVersion: Int = 1,


    @ColumnInfo(name = "remarks")
    override val remarks: String? = null,


    @ColumnInfo(name = "extra_data")
    override val extraData: String? = null


) : BaseEntity(

    uuid = uuid,

    createdBy = createdBy,
    createdAt = createdAt,

    updatedAt = updatedAt,

    deletedAt = deletedAt,

    isDeleted = isDeleted,

    syncStatus = syncStatus,
    syncVersion = syncVersion,
    syncAt = syncAt,

    deviceId = deviceId,

    rowVersion = rowVersion,

    remarks = remarks,
    extraData = extraData
)
