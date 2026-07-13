package com.aistudio.dieselstationsms.kxmpzq.data.di

import android.content.Context
import com.aistudio.dieselstationsms.kxmpzq.data.repository.StationRepository
import com.aistudio.dieselstationsms.kxmpzq.data.repository.StationRepositoryImpl

object AppModule {
    fun provideStationRepository(context: Context): StationRepository {
        return StationRepositoryImpl(context)
    }
}
