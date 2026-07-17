package infrastructure.persistence.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import infrastructure.persistence.entities.RoleEntity



@Dao
interface RoleDao {


    @Insert(
        onConflict = OnConflictStrategy.ABORT
    )
    suspend fun insert(
        role: RoleEntity
    ): Long



    @Update
    suspend fun update(
        role: RoleEntity
    )



    @Query("""
        SELECT *
        FROM roles
        WHERE id = :id
        AND is_deleted = 0
        LIMIT 1
    """)
    suspend fun findById(
        id: Long
    ): RoleEntity?



    @Query("""
        SELECT *
        FROM roles
        WHERE role_code = :code
        AND is_deleted = 0
        LIMIT 1
    """)
    suspend fun findByCode(
        code: String
    ): RoleEntity?



    @Query("""
        SELECT *
        FROM roles
        WHERE is_deleted = 0
        ORDER BY level ASC, role_name ASC
    """)
    suspend fun findAll():
            List<RoleEntity>



    @Query("""
        UPDATE roles
        SET is_deleted = 1,
            deleted_at = datetime('now')
        WHERE id = :id
    """)
    suspend fun softDelete(
        id: Long
    )

}
