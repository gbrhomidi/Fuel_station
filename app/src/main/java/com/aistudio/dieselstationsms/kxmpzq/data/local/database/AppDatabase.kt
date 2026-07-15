package com.aistudio.dieselstationsms.kxmpzq.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aistudio.dieselstationsms.kxmpzq.data.local.entity.RoleEntity
import com.aistudio.dieselstationsms.kxmpzq.data.local.dao.RoleDao
import com.aistudio.dieselstationsms.kxmpzq.data.local.converter.Converters

@Database(
    entities = [RoleEntity::class],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun roleDao(): RoleDao
}
