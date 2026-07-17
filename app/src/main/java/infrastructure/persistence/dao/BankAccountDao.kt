package infrastructure.persistence.dao

import androidx.room.*
import infrastructure.persistence.entities.BankAccountEntity

@Dao
interface BankAccountDao {


    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(
        bankAccount: BankAccountEntity
    ): Long


    @Update
    suspend fun update(
        bankAccount: BankAccountEntity
    )


    @Query("""
        SELECT *
        FROM bank_accounts
        WHERE id = :id
        AND is_deleted = 0
    """)
    suspend fun findById(id: Long): BankAccountEntity?


    @Query("""
        SELECT *
        FROM bank_accounts
        WHERE uuid = :uuid
        AND is_deleted = 0
    """)
    suspend fun findByUuid(uuid: String): BankAccountEntity?


    @Query("""
        SELECT *
        FROM bank_accounts
        WHERE account_number = :accountNumber
        AND is_deleted = 0
    """)
    suspend fun findByAccountNumber(
        accountNumber: String
    ): BankAccountEntity?


    @Query("""
        SELECT *
        FROM bank_accounts
        WHERE is_deleted = 0
        ORDER BY created_at DESC
    """)
    suspend fun findAll(): List<BankAccountEntity>


    @Query("""
        UPDATE bank_accounts
        SET
            is_deleted = 1,
            deleted_at = datetime('now')
        WHERE id = :id
        AND is_deleted = 0
    """)
    suspend fun softDelete(id: Long): Int


    @Query("""
        UPDATE bank_accounts
        SET
            is_deleted = 1,
            deleted_at = datetime('now')
        WHERE uuid = :uuid
        AND is_deleted = 0
    """)
    suspend fun softDeleteByUuid(uuid: String): Int
}
