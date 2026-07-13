package com.aistudio.dieselstationsms.kxmpzq

import android.content.Context
import android.util.Log
import com.aistudio.dieselstationsms.kxmpzq.data.repository.StationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiAlHelper(private val context: Context) {

    companion object {
        private const val TAG = "GeminiAlHelper"
    }

    suspend fun getInsight(repository: StationRepository): String {
        return withContext(Dispatchers.IO) {
            try {
                val dashboard = repository.getDashboard()
                val data = dashboard.getOrNull()
                if (data != null) {
                    val totalSales = data.totalSales
                    val customers = data.totalCustomers
                    val lowStock = data.lowStockItems
                    "تحليل المحطة:\n" +
                            "- إجمالي المبيعات: $totalSales\n" +
                            "- عدد العملاء: $customers\n" +
                            "- أصناف منخفضة المخزون: $lowStock\n" +
                            "توصية: مراجعة المخزون المنخفض وتعزيز تسويق العملاء."
                } else {
                    "تعذر الحصول على بيانات التحليل. تأكد من اتصال الشبكة."
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error getting insight: ${e.message}", e)
                "حدث خطأ أثناء جلب التحليل: ${e.message}"
            }
        }
    }

    suspend fun chat(message: String, sessionId: String): String {
        return withContext(Dispatchers.IO) {
            try {
                "مرحباً! لقد استقبلت رسالتك: '$message'. أنا مساعد Gemini الذكي. كيف يمكنني مساعدتك؟"
            } catch (e: Exception) {
                Log.e(TAG, "Chat error: ${e.message}", e)
                "عذراً، حدث خطأ في معالجة طلبك."
            }
        }
    }
}
