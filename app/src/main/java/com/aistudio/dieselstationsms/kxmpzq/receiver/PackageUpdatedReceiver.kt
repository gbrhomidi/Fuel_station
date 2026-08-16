package com.aistudio.dieselstationsms.kxmpzq.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.aistudio.dieselstationsms.kxmpzq.startup.ServiceLaunchResult
import com.aistudio.dieselstationsms.kxmpzq.startup.ServiceStatusRepository
import com.aistudio.dieselstationsms.kxmpzq.startup.SmsServiceLauncher
import com.aistudio.dieselstationsms.kxmpzq.startup.StartupReason
import com.aistudio.dieselstationsms.kxmpzq.utils.SystemEventLogger

/**
 * يعيد تشغيل SMSService بعد استبدال حزمة التطبيق.
 *
 * يستخدم نفس منسق بدء التشغيل الذي تستخدمه MainActivity وBootReceiver؛
 * ولا يسجل مستقبلاً ديناميكياً ولا يتعامل مع قاعدة البيانات مباشرة.
 */
class PackageUpdatedReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "PackageUpdatedReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_MY_PACKAGE_REPLACED) return

        val appContext = context.applicationContext
        val result = try {
            SmsServiceLauncher(
                appContext,
                ServiceStatusRepository(appContext)
            ).launch(StartupReason.APP_UPDATED)
        } catch (error: Exception) {
            Log.e(TAG, "Failed to launch SMSService after app update", error)
            SystemEventLogger.recordError(
                appContext,
                TAG,
                error.message
            )
            return
        }

        when (result) {
            is ServiceLaunchResult.Success -> {
                SystemEventLogger.record(appContext, "APP_UPDATED")
                Log.i(TAG, "SMSService launch requested after app update")
            }

            is ServiceLaunchResult.AlreadyRunning -> {
                SystemEventLogger.record(appContext, "APP_UPDATED_SERVICE_ALREADY_RUNNING")
                Log.i(TAG, "SMSService already running after app update")
            }

            is ServiceLaunchResult.Failure -> {
                SystemEventLogger.recordError(
                    appContext,
                    TAG,
                    result.error
                )
                Log.e(TAG, "SMSService launch failed after app update: ${result.error}")
            }
        }
    }
}
