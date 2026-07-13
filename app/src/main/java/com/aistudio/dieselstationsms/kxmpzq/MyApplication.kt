package com.aistudio.dieselstationsms.kxmpzq

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import com.aistudio.dieselstationsms.kxmpzq.data.repository.StationRepository
import com.aistudio.dieselstationsms.kxmpzq.di.AppModule

class MyApplication : Application(), Configuration.Provider {

    companion object {
        private const val TAG = "MyApplication"
    }

    val stationRepository: StationRepository by lazy {
        AppModule.provideStationRepository(this)
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "MyApplication onCreate")
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .build()
    }
}
