package infrastructure.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import infrastructure.persistence.entities.RolePermissionCrossRef

@Dao
interface RolePermissionDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(mapping: RolePermissionCrossRef): Long

    @Update
    suspend fun update(mapping: RolePermissionCrossRef)

    @Query("""
        SELECT id, uuid, role_id, permission_id, station_id, branch_id,
               can_create, can_read, can_update, can_delete, can_export, can_print, can_approve,
               created_at, updated_at, created_by, is_deleted,
               sync_status, sync_version, sync_at, device_id, remarks, extra_data
        FROM role_permissions
        WHERE id = :id AND is_deleted = 0
        LIMIT 1
    """)
    suspend fun findById(id: Long): RolePermissionCrossRef?

    @Query("""
        SELECT rp.*
        FROM role_permissions rp
        WHERE rp.role_id = :roleId AND rp.is_deleted = 0
    """)
    suspend fun findByRoleId(roleId: Long): List<RolePermissionCrossRef>

    @Query("""
        SELECT rp.*
        FROM role_permissions rp
        WHERE rp.permission_id = :permissionId AND rp.is_deleted = 0
    """)
    suspend fun findByPermissionId(permissionId: Long): List<RolePermissionCrossRef>

    @Query("""
        SELECT rp.*
        FROM role_permissions rp
        WHERE rp.role_id = :roleId AND rp.permission_id = :permissionId AND rp.is_deleted = 0
        LIMIT 1
    """)
    suspend fun findByRoleAndPermission(roleId: Long, permissionId: Long): RolePermissionCrossRef?

    @Query("SELECT * FROM role_permissions WHERE is_deleted = 0")
    suspend fun findAll(): List<RolePermissionCrossRef>

    @Query("UPDATE role_permissions SET is_deleted = 1 WHERE id = :id")
    suspend fun softDelete(id: Long)
}
