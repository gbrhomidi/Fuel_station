package com.aistudio.dieselstationsms.kxmpzq.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.aistudio.dieselstationsms.kxmpzq.startup.ServiceLaunchResult
import com.aistudio.dieselstationsms.kxmpzq.startup.ServiceStatusRepository
import com.aistudio.dieselstationsms.kxmpzq.startup.SmsServiceLauncher
import com.aistudio.dieselstationsms.kxmpzq.startup.StartupReason

/**
 * يعيد تشغيل خدمة SMS بعد إقلاع الجهاز.
 *
 * مستقبل SMS نفسه معرف في AndroidManifest.xml؛ لذلك لا يسجل هذا المستقبل
 * مستقبلاً إضافياً ولا يتعامل مع الرسائل الواردة. مسؤوليته الوحيدة هي طلب
 * تشغيل SMSService عبر منسق بدء التشغيل المشترك.
 */
class BootReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "BootReceiver"

        private val SUPPORTED_ACTIONS = setOf(
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_LOCKED_BOOT_COMPLETED,
            "android.intent.action.QUICKBOOT_POWERON"
        )
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        if (action !in SUPPORTED_ACTIONS) return

        val appContext = context.applicationContext
        val result = try {
            SmsServiceLauncher(
                appContext,
                ServiceStatusRepository(appContext)
            ).launch(StartupReason.BOOT)
        } catch (error: Exception) {
            Log.e(TAG, "Failed to launch SMSService after $action", error)
            return
        }

        when (result) {
            is ServiceLaunchResult.Success ->
                Log.i(TAG, "SMSService launch requested after $action")

            is ServiceLaunchResult.AlreadyRunning ->
                Log.i(TAG, "SMSService already running after $action")

            is ServiceLaunchResult.Failure ->
                Log.e(TAG, "SMSService launch failed after $action: ${result.error}")
        }
    }
}
