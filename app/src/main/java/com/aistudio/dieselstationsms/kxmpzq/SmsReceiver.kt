package com.aistudio.dieselstationsms.kxmpzq

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Telephony
import android.telephony.SmsMessage
import android.util.Log
import androidx.core.content.ContextCompat

class SmsReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "SmsReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            Log.d(TAG, "SMS received")

            try {
                val messages: Array<SmsMessage> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Telephony.Sms.Intents.getMessagesFromIntent(intent)
                } else {
                    @Suppress("DEPRECATION")
                    val pdus = intent.getSerializableExtra("pdus") as Array<*>
                    pdus.map { SmsMessage.createFromPdu(it as ByteArray) }.toTypedArray()
                }

                for (sms in messages) {
                    val sender = sms.originatingAddress ?: "Unknown"
                    val body = sms.messageBody ?: ""

                    Log.d(TAG, "SMS from: $sender, body: $body")
                    processSms(context, sender, body)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error processing SMS: ${e.message}", e)
            }
        }
    }

    private fun processSms(context: Context, sender: String, body: String) {
        try {
            val dbHelper = DatabaseHelper(context)
            val values = android.content.ContentValues().apply {
                put("phone_number", sender)
                put("message_body", body)
                put("message_type", "incoming")
                put("status", "received")
                put("created_at", java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date()))
            }
            dbHelper.insert("sms_messages", null, values)
            Log.d(TAG, "SMS saved to database")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save SMS: ${e.message}", e)
        }
    }
}
