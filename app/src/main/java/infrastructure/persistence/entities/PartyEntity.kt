package infrastructure.persistence.entities

import androidx.room.*
import infrastructure.persistence.base.BaseEntity
import infrastructure.persistence.types.SyncStatus


@Entity(
    tableName = "parties",
    indices = [
        Index(value = ["uuid"], unique = true),
        Index(value = ["party_code"], unique = true),
        Index(value = ["party_type"]),
        Index(value = ["is_deleted"])
    ]
)
data class PartyEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,


    @ColumnInfo(name = "uuid")
    override val uuid: String,


    @ColumnInfo(name = "party_code")
    val partyCode: String,


    @ColumnInfo(name = "name")
    val name: String,


    @ColumnInfo(name = "phone")
    val phone: String? = null,


    @ColumnInfo(name = "address")
    val address: String? = null,


    @ColumnInfo(name = "party_type")
    val partyType: String = "CUSTOMER",


    @ColumnInfo(name = "credit_limit")
    val creditLimit: Double = 0.0,


    @ColumnInfo(name = "created_by")
    override val createdBy: Long,


    @ColumnInfo(name = "created_at")
    override val createdAt: String,


    @ColumnInfo(name = "updated_by")
    override val updatedBy: Long? = null,


    @ColumnInfo(name = "updated_at")
    override val updatedAt: String? = null,


    @ColumnInfo(name = "deleted_by")
    override val deletedBy: Long? = null,


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

): BaseEntity(

    uuid,
    createdBy,
    createdAt,
    updatedBy,
    updatedAt,
    deletedBy,
    deletedAt,
    isDeleted,
    syncStatus,
    syncVersion,
    syncAt,
    deviceId,
    rowVersion,
    remarks,
    extraData
)
