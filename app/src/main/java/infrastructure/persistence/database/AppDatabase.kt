package infrastructure.persistence.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

import infrastructure.persistence.converters.SyncConverters
import infrastructure.persistence.converters.MoneyConverters


// DAOs
import infrastructure.persistence.dao.CompanyDao
import infrastructure.persistence.dao.CurrencyDao
import infrastructure.persistence.dao.StationDao
import infrastructure.persistence.dao.FuelTypeDao
import infrastructure.persistence.dao.UserDao
import infrastructure.persistence.dao.RoleDao
import infrastructure.persistence.dao.PermissionDao
import infrastructure.persistence.dao.RolePermissionDao
import infrastructure.persistence.dao.CashBoxDao
import infrastructure.persistence.dao.ReceiptDao
import infrastructure.persistence.dao.PaymentDao
import infrastructure.persistence.dao.TankDao
import infrastructure.persistence.dao.PumpDao
import infrastructure.persistence.dao.PumpNozzleDao
import infrastructure.persistence.dao.PartyDao
import infrastructure.persistence.dao.SalesTransactionDao
import infrastructure.persistence.dao.BankAccountDao


// Entities
import infrastructure.persistence.entities.CompanyEntity
import infrastructure.persistence.entities.StationEntity
import infrastructure.persistence.entities.CurrencyEntity
import infrastructure.persistence.entities.FuelTypeEntity
import infrastructure.persistence.entities.UserEntity
import infrastructure.persistence.entities.RoleEntity
import infrastructure.persistence.entities.PermissionEntity
import infrastructure.persistence.entities.RolePermissionCrossRef
import infrastructure.persistence.entities.CashBoxEntity
import infrastructure.persistence.entities.ReceiptEntity
import infrastructure.persistence.entities.PaymentEntity
import infrastructure.persistence.entities.TankEntity
import infrastructure.persistence.entities.PumpEntity
import infrastructure.persistence.entities.PumpNozzleEntity
import infrastructure.persistence.entities.PartyEntity
import infrastructure.persistence.entities.SalesTransactionEntity
import infrastructure.persistence.entities.BankAccountEntity



@Database(
    entities = [

        CompanyEntity::class,
        StationEntity::class,
        CurrencyEntity::class,
        FuelTypeEntity::class,

        UserEntity::class,
        RoleEntity::class,
        PermissionEntity::class,
        RolePermissionCrossRef::class,

        CashBoxEntity::class,
        ReceiptEntity::class,
        PaymentEntity::class,

        TankEntity::class,
        PumpEntity::class,
        PumpNozzleEntity::class,


        // Phase Financial Expansion
        PartyEntity::class,
        SalesTransactionEntity::class,
        BankAccountEntity::class

    ],

    version = 3,

    exportSchema = true
)


@TypeConverters(
    SyncConverters::class,
    MoneyConverters::class
)


abstract class AppDatabase : RoomDatabase() {


    abstract fun companyDao(): CompanyDao

    abstract fun stationDao(): StationDao

    abstract fun currencyDao(): CurrencyDao

    abstract fun fuelTypeDao(): FuelTypeDao


    abstract fun userDao(): UserDao

    abstract fun roleDao(): RoleDao

    abstract fun permissionDao(): PermissionDao

    abstract fun rolePermissionDao(): RolePermissionDao



    abstract fun cashBoxDao(): CashBoxDao

    abstract fun receiptDao(): ReceiptDao

    abstract fun paymentDao(): PaymentDao



    abstract fun tankDao(): TankDao

    abstract fun pumpDao(): PumpDao

    abstract fun pumpNozzleDao(): PumpNozzleDao



    // Financial Domain DAOs

    abstract fun partyDao(): PartyDao

    abstract fun salesTransactionDao(): SalesTransactionDao

    abstract fun bankAccountDao(): BankAccountDao



    companion object {


        @Volatile
        private var INSTANCE: AppDatabase? = null


        private const val DATABASE_NAME =
            "petro_station_db"



        fun getInstance(
            context: Context
        ): AppDatabase {


            return INSTANCE ?: synchronized(this) {


                val instance =
                  Room.databaseBuilder(
                  context.applicationContext,
                  AppDatabase::class.java,
                  DATABASE_NAME
        )
   

                    /*
 * Development Mode
 *
 * The application is still under active development.
 * No production users exist yet.
 *
 * Therefore destructive migrations are enabled.
 *
 * Before the first production release:
 * - Remove fallbackToDestructiveMigration()
 * - Implement real Migration classes.
 */
.fallbackToDestructiveMigration()
.fallbackToDestructiveMigrationOnDowngrade()
.build()



                INSTANCE = instance

                instance

            }
        }
    }
}
