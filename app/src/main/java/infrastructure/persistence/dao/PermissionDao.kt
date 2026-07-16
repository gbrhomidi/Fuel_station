package infrastructure.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import infrastructure.persistence.entities.PermissionEntity

@Dao
interface PermissionDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(permission: PermissionEntity): Long

    @Update
    suspend fun update(permission: PermissionEntity)

    @Query("""
        SELECT id, uuid, permission_code, permission_name, permission_name_ar, description,
               module, module_name_ar, action, requires_station, requires_branch, is_active,
               created_at, updated_at, is_deleted, sync_status, sync_version, sync_at,
               device_id, remarks, extra_data
        FROM permissions
        WHERE id = :id AND is_deleted = 0
        LIMIT 1
    """)
    suspend fun findById(id: Long): PermissionEntity?

    @Query("""
        SELECT id, uuid, permission_code, permission_name, permission_name_ar, description,
               module, module_name_ar, action, requires_station, requires_branch, is_active,
               created_at, updated_at, is_deleted, sync_status, sync_version, sync_at,
               device_id, remarks, extra_data
        FROM permissions
        WHERE permission_code = :code AND is_deleted = 0
        LIMIT 1
    """)
    suspend fun findByCode(code: String): PermissionEntity?

    @Query("""
        SELECT id, uuid, permission_code, permission_name, permission_name_ar, description,
               module, module_name_ar, action, requires_station, requires_branch, is_active,
               created_at, updated_at, is_deleted, sync_status, sync_version, sync_at,
               device_id, remarks, extra_data
        FROM permissions
        WHERE is_deleted = 0
        ORDER BY module ASC, permission_name ASC
    """)
    suspend fun findAll(): List<PermissionEntity>

    @Query("UPDATE permissions SET is_deleted = 1 WHERE id = :id")
    suspend fun softDelete(id: Long)
}
