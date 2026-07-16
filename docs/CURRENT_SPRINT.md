# CURRENT_SPRINT.md
## SPRINT 2 - Tanks & Pumps (الخزانات والمضخات)

**تاريخ البدء:** 2026-07-16  
**الدفعة:** الثانية (Infrastructure - Storage & Dispensing)  
**الجداول المستهدفة:** `tanks`, `pumps`, `pump_nozzles`  
**المرجع:** `reference/ddl/inventory.sql`  
**الحالة:** 🔄 قيد التنفيذ

---

### 📋 المهام المفتوحة

| # | المهمة | المسؤول | الحالة |
| :---: | :--- | :--- | :---: |
| 1 | إنشاء `TankEntity.kt` | ChatGPT | ⏳ في الانتظار |
| 2 | إنشاء `PumpEntity.kt` | ChatGPT | ⏳ في الانتظار |
| 3 | إنشاء `PumpNozzleEntity.kt` | ChatGPT | ⏳ في الانتظار |
| 4 | إنشاء `TankDao.kt` | ChatGPT | ⏳ في الانتظار |
| 5 | إنشاء `PumpDao.kt` | ChatGPT | ⏳ في الانتظار |
| 6 | إنشاء `PumpNozzleDao.kt` | ChatGPT | ⏳ في الانتظار |
| 7 | مراجعة الكيانات والـ DAOs | Gemini | ⏳ في الانتظار |
| 8 | كتابة اختبارات `TankDaoTest.kt` | Kimi | ⏳ في الانتظار |
| 9 | كتابة اختبارات `PumpDaoTest.kt` | Kimi | ⏳ في الانتظار |
| 10 | كتابة اختبارات `PumpNozzleDaoTest.kt` | Kimi | ⏳ في الانتظار |
| 11 | إغلاق الدفعة وإصدار DECISION | DeepSeek | ⏳ في الانتظار |

---

### 📌 المراجع المعتمدة لهذه الدفعة

- **DDL المرجعي:** `reference/ddl/inventory.sql`
- **المخطط الكامل:** `reference/database/master_schema.sql`
- **تتبع المصادر:** `reference/database/TRACEABILITY.md`

---

### ✅ معايير القبول (Acceptance Criteria)

- [ ] جميع الكيانات مطابقة للـ DDL حرفياً.
- [ ] جميع الـ DAOs تحتوي على `WHERE is_deleted = 0`.
- [ ] لا يوجد `@Delete` في أي DAO.
- [ ] جميع الاختبارات تجتاز بنجاح (PASS).
- [ ] Gemini يُصدر SIGN-OFF.
- [ ] Kimi يُصدر تقرير PASS/FAIL.
- [ ] DeepSeek يُصدر DECISION ويغلق الدفعة.

---

### 📅 التحديثات

| التاريخ | التحديث | بواسطة |
| :--- | :--- | :--- |
| 2026-07-16 | إنشاء CURRENT_SPRINT.md | DeepSeek |
| 2026-07-16 | تحديث لبدء SPRINT 2 | DeepSeek |
