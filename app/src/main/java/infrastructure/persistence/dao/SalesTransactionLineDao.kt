package infrastructure.persistence.dao


import androidx.room.*

import infrastructure.persistence.entities.SalesTransactionLineEntity


@Dao
interface SalesTransactionLineDao {


    @Insert(
        onConflict = OnConflictStrategy.REPLACE
    )
    suspend fun insert(
        line: SalesTransactionLineEntity
    ): Long



    @Insert(
        onConflict = OnConflictStrategy.REPLACE
    )
    suspend fun insertAll(
        lines: List<SalesTransactionLineEntity>
    )



    @Update
    suspend fun update(
        line: SalesTransactionLineEntity
    )



    @Delete
    suspend fun delete(
        line: SalesTransactionLineEntity
    )



    @Query(
        """
        SELECT *
        FROM sales_transaction_lines
        WHERE transaction_id = :transactionId
        AND is_deleted = 0
        """
    )
    suspend fun getByTransaction(
        transactionId: Long
    ): List<SalesTransactionLineEntity>



    @Query(
        """
        UPDATE sales_transaction_lines
        SET 
        is_deleted = 1,
        deleted_by = :userId,
        updated_at = :time
        WHERE id = :id
        """
    )
    suspend fun softDelete(
        id: Long,
        userId: Long,
        time: Long
    )

}
