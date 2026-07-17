package infrastructure.repositories

import domain.repositories.ReceiptRepository
import infrastructure.persistence.dao.ReceiptDao
import infrastructure.persistence.entities.ReceiptEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReceiptRepositoryImpl @Inject constructor(
    private val receiptDao: ReceiptDao
) : ReceiptRepository {

    override suspend fun insert(receipt: ReceiptEntity): Long =
        receiptDao.insert(receipt)

    override suspend fun update(receipt: ReceiptEntity) =
        receiptDao.update(receipt)

    override suspend fun findById(id: Long): ReceiptEntity? =
        receiptDao.findById(id)

    override suspend fun findByReceiptNumber(receiptNumber: String): ReceiptEntity? =
        receiptDao.findByReceiptNumber(receiptNumber)

    override suspend fun findByCashBox(cashBoxId: Long): List<ReceiptEntity> =
        receiptDao.findByCashBox(cashBoxId)

    override suspend fun findByPayment(paymentId: Long): ReceiptEntity? =
        receiptDao.findByPayment(paymentId)

    override suspend fun findAll(): List<ReceiptEntity> =
        receiptDao.findAll()

    override suspend fun softDelete(id: Long): Int =
        receiptDao.softDelete(id)

    override suspend fun softDeleteByReceiptNumber(receiptNumber: String): Int =
        receiptDao.softDeleteByReceiptNumber(receiptNumber)
}
