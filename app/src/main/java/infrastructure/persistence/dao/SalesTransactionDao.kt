package infrastructure.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import infrastructure.persistence.entities.SalesTransactionEntity


@Dao
interface SalesTransactionDao {


    @Insert(
        onConflict = OnConflictStrategy.ABORT
    )
    suspend fun insert(
        transaction: SalesTransactionEntity
    ): Long



    @Update
    suspend fun update(
        transaction: SalesTransactionEntity
    )



    @Query(
        """
        SELECT *
        FROM sales_transactions
        WHERE id = :id
        AND is_deleted = 0
        """
    )
    suspend fun findById(
        id: Long
    ): SalesTransactionEntity?



    @Query(
        """
        SELECT *
        FROM sales_transactions
        WHERE uuid = :uuid
        AND is_deleted = 0
        """
    )
    suspend fun findByUuid(
        uuid: String
    ): SalesTransactionEntity?



    @Query(
        """
        SELECT *
        FROM sales_transactions
        WHERE customer_party_id = :partyId
        AND is_deleted = 0
        ORDER BY created_at DESC
        """
    )
    suspend fun findByCustomer(
        partyId: Long
    ): List<SalesTransactionEntity>



    @Query(
        """
        SELECT *
        FROM sales_transactions
        WHERE is_deleted = 0
        ORDER BY created_at DESC
        """
    )
    suspend fun findAll(): List<SalesTransactionEntity>



    @Query(
        """
        UPDATE sales_transactions
        SET 
            is_deleted = 1,
            deleted_at = datetime('now')
        WHERE id = :id
        AND is_deleted = 0
        """
    )
    suspend fun softDelete(
        id: Long
    ): Int



    @Query(
        """
        UPDATE sales_transactions
        SET 
            is_deleted = 1,
            deleted_at = datetime('now')
        WHERE uuid = :uuid
        AND is_deleted = 0
        """
    )
    suspend fun softDeleteByUuid(
        uuid: String
    ): Int
}
