package infrastructure.repositories

import domain.repositories.PaymentRepository
import infrastructure.persistence.dao.PaymentDao
import infrastructure.persistence.entities.PaymentEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentRepositoryImpl @Inject constructor(
    private val paymentDao: PaymentDao
) : PaymentRepository {

    override suspend fun insert(payment: PaymentEntity): Long =
        paymentDao.insert(payment)

    override suspend fun update(payment: PaymentEntity) =
        paymentDao.update(payment)

    override suspend fun findById(id: Long): PaymentEntity? =
        paymentDao.findById(id)

    override suspend fun findByUuid(uuid: String): PaymentEntity? =
        paymentDao.findByUuid(uuid)

    override suspend fun findByCashBox(cashBoxId: Long): List<PaymentEntity> =
        paymentDao.findByCashBox(cashBoxId)

    override suspend fun findByCustomer(customerId: Long): List<PaymentEntity> =
        paymentDao.findByCustomer(customerId)

    override suspend fun findAll(): List<PaymentEntity> =
        paymentDao.findAll()

    override suspend fun softDelete(id: Long): Int =
        paymentDao.softDelete(id)

    override suspend fun softDeleteByUuid(uuid: String): Int =
        paymentDao.softDeleteByUuid(uuid)
}
