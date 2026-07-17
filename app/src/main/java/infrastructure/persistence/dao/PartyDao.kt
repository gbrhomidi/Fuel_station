package infrastructure.persistence.dao

import androidx.room.*
import infrastructure.persistence.entities.PartyEntity

@Dao
interface PartyDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(party: PartyEntity): Long


    @Update
    suspend fun update(party: PartyEntity)


    @Query("""
        SELECT *
        FROM parties
        WHERE id = :id
        AND is_deleted = 0
    """)
    suspend fun findById(id: Long): PartyEntity?


    @Query("""
        SELECT *
        FROM parties
        WHERE uuid = :uuid
        AND is_deleted = 0
    """)
    suspend fun findByUuid(uuid: String): PartyEntity?


    @Query("""
        SELECT *
        FROM parties
        WHERE party_code = :partyCode
        AND is_deleted = 0
    """)
    suspend fun findByPartyCode(
        partyCode: String
    ): PartyEntity?


    @Query("""
        SELECT *
        FROM parties
        WHERE is_deleted = 0
        ORDER BY created_at DESC
    """)
    suspend fun findAll(): List<PartyEntity>


    @Query("""
        UPDATE parties
        SET 
            is_deleted = 1,
            deleted_at = datetime('now')
        WHERE id = :id
        AND is_deleted = 0
    """)
    suspend fun softDelete(id: Long): Int


    @Query("""
        UPDATE parties
        SET 
            is_deleted = 1,
            deleted_at = datetime('now')
        WHERE uuid = :uuid
        AND is_deleted = 0
    """)
    suspend fun softDeleteByUuid(uuid: String): Int
}
