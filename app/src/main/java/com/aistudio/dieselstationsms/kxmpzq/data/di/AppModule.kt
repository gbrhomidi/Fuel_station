package com.aistudio.dieselstationsms.kxmpzq.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import infrastructure.persistence.database.AppDatabase
import infrastructure.persistence.dao.RoleDao
import infrastructure.persistence.entities.RoleEntity
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideRoleDao(appDatabase: AppDatabase): RoleDao {
        return appDatabase.roleDao()
    }

    // باقي الـ Providers...
}
