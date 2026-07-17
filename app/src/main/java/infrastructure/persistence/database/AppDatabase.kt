package infrastructure.persistence.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import infrastructure.persistence.dao.CompanyDao
import infrastructure.persistence.dao.CurrencyDao
import infrastructure.persistence.dao.StationDao
import infrastructure.persistence.dao.FuelTypeDao
import infrastructure.persistence.dao.UserDao
import infrastructure.persistence.entities.CompanyEntity
import infrastructure.persistence.entities.CurrencyEntity
import infrastructure.persistence.entities.StationEntity
import infrastructure.persistence.entities.FuelTypeEntity
import infrastructure.persistence.entities.UserEntity

@Database(
    entities = [
        CompanyEntity::class,
        StationEntity::class,
        CurrencyEntity::class,
        FuelTypeEntity::class,
        UserEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun companyDao(): CompanyDao
    abstract fun stationDao(): StationDao
    abstract fun currencyDao(): CurrencyDao
    abstract fun fuelTypeDao(): FuelTypeDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private const val DATABASE_NAME = "petro_station_db"

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                .addCallback(object : Callback() {
                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        db.execSQL("PRAGMA foreign_keys = ON;")
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
                
                INSTANCE = instance
                instance
            }
        }
    }
}
