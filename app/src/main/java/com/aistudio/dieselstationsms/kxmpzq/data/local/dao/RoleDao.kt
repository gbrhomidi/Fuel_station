package com.aistudio.dieselstationsms.kxmpzq.data.local.dao

import androidx.room.*
import com.aistudio.dieselstationsms.kxmpzq.data.local.entity.RoleEntity

@Dao
interface RoleDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(role: RoleEntity)

    @Update
    suspend fun update(role: RoleEntity): Int

    @Query("SELECT id, name, code, description, isActive, createdAt, updatedAt, version FROM roles WHERE id = :id LIMIT 1")
    suspend fun findById(id: String): RoleEntity?

    @Query("SELECT id, name, code, description, isActive, createdAt, updatedAt, version FROM roles WHERE name = :name LIMIT 1")
    suspend fun findByName(name: String): RoleEntity?

    @Query("SELECT id, name, code, description, isActive, createdAt, updatedAt, version FROM roles ORDER BY name ASC")
    suspend fun findAll(): List<RoleEntity>
}
