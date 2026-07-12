package com.aistudio.dieselstationsms.kxmpzq

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import com.aistudio.dieselstationsms.kxmpzq.data.repository.StationRepository
import com.aistudio.dieselstationsms.kxmpzq.di.AppModule

/**
 * ═══════════════════════════════════════════════════════════════
 * MyApplication — تم تحديثه ليدعم StationRepository
 * ═══════════════════════════════════════════════════════════════
 *
 * التعديل: إضافة خاصية stationRepository متاحة للتطبيق كاملاً
 *          استعداداً لـ ViewModels وNavigation Compose
 */
class MyApplication : Application(), Configuration.Provider {

    companion object {
        private const val TAG = "MyApplication"
    }

    /**
     * StationRepository Singleton — يُوفر الوصول المباشر للبيانات
     * بدون المرور بـ localhost:8080 أو NanoHTTPD
     */
    val stationRepository: StationRepository by lazy {
        AppModule.provideStationRepository(this)
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "MyApplication onCreate — Repository initialized")
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .build()
    }
}