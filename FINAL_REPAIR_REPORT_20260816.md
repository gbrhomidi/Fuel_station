# تقرير إصلاح وتدقيق مستودع محطة الوقود

**التاريخ:** 16 أغسطس 2026

**المستودع المرجعي:** [gbrhomidi/station-Abo_Ahmed2](https://github.com/gbrhomidi/station-Abo_Ahmed2)

**نسخة العمل:** `station-Abo_Ahmed2_sms_audit`

**Commit المرجعي:** `9ad28fd9f0212a2010440c2ed33c9481f4ad774a`

## الخلاصة التنفيذية

تم تطبيق الإصلاحات المثبتة في مسار SMS التفاعلي ودورة حياة الخدمة وطلبات الأذونات، كما تم دمج مجموعة شاشات HTML التي اجتازت فحص P0 السابق. لم يتم تعديل `DatabaseHelper.kt`. بقي اعتماد الإنتاج **BLOCKED / NOT VERIFIED** لأن بيئة العمل لا تحتوي Android SDK صالحاً للبناء المحلي، ولم يتم تشغيل APK على جهاز Android فعلي أو تنفيذ اختبار SMS هاتف-إلى-هاتف.

> **FIXED لا تساوي VERIFIED.** الإصلاحات التالية مثبتة ساكنياً من المصدر، أما استقبال SMS الفعلي وإرسال الرد ووصوله فلم تُثبت على جهاز حقيقي في هذه الجلسة.

## الإصلاحات المنفذة

| الملف أو النطاق | التغيير المثبت | الحالة |
|---|---|---|
| `receiver/BootReceiver.kt` | استبدال الـstub بمسار `SmsServiceLauncher` مع `ServiceStatusRepository` و`StartupReason.BOOT`، مع تجاهل الأفعال غير المدعومة. | FIXED / STATIC PASS |
| `AndroidManifest.xml` | تفعيل `BootReceiver` و`PackageUpdatedReceiver`، والإبقاء على مستقبل SMS في Manifest فقط، وإزالة `LOCKED_BOOT_COMPLETED` لأن التخزين والخدمة الحاليين ليسا direct-boot aware. | FIXED / STATIC PASS |
| `receiver/PackageUpdatedReceiver.kt` | توحيد التشغيل بعد تحديث الحزمة مع منسق الخدمة نفسه عبر `StartupReason.APP_UPDATED`. | FIXED / STATIC PASS |
| `MainActivity.kt` | توسيع طلب الأذونات إلى أذونات الهاتف والموقع والوسائط والإشعارات، وفصل الأذونات الحرجة لمسار SMS عن الأذونات الاختيارية، وإضافة Intent خاص بطلب تجاهل تحسينات البطارية. | FIXED / STATIC PASS |
| `sms/SmsSecurity.kt` | إيقاف استخدام `sms_whitelist` كقائمة SMSC؛ لا توجد قائمة SMSC مستقلة مثبتة في المخطط الحالي، لذلك لا يُرفض SMS الوارد بسبب مركز خدمة غير معرف. | FIXED / STATIC PASS |
| `sms/SmsProcessor.kt` | عند عدم العثور على العميل، يُرسل رد SMS فعلي للتسجيل عبر `sendReplyOnce`، ولا يُكمل claim إلا بعد نجاح الرد؛ عند الفشل يُحرر claim لإتاحة إعادة المحاولة. | FIXED / STATIC PASS |
| `assets/main.html` | إزالة `fetch` المحلي من تحميل القائمة، والاعتماد على المسارات الثابتة وBridge للبيانات التشغيلية. | FIXED / STATIC PASS |
| `assets/screens/*.html` | دمج 31 شاشة من حزمة P0 المعتمدة التي تستخدم allow-list bridge بدلاً من `AndroidInterface.execute`. | FIXED / STATIC PASS |
| `DatabaseHelper.kt` | لم يُعدّل في هذه الجولة. النسخة الموجودة في worktree المرجعي تحمل `VERSION = 16`؛ هذا توثيق للحالة، وليس تغييراً عليها. | UNCHANGED |

## أدلة الفحص الساكن

أُجريت الفحوصات على worktree النهائي بعد الدمج:

| الفحص | النتيجة |
|---|---:|
| عدد ملفات HTML | 39 |
| `localhost` أو `127.0.0.1` | 0 |
| `AndroidInterface.execute` | 0 |
| استدعاءات `.execute(` العامة | 0 |
| `fetch(` في HTML | 0 |
| `invokeExistingBridgeAction` | 64 |
| دوال `@JavascriptInterface` المكتشفة | 237 |
| شاشات HTML المفحوصة في عقد Bridge | 38 |
| دوال AndroidInterface مباشرة غير موجودة في MainActivity | 0 |
| فشل JavaScript syntax checker | 0 |
| `git diff --check` | PASS |
| تعديل `DatabaseHelper.kt` في Git status | لا يوجد |

الأدلة الخام مرفقة داخل مجلد `evidence/` في الحزمة، وتشمل `bridge_contract_evidence_final.json` و`REPAIR_AUDIT_FINDINGS_PHASE2.md` ونسخة مدقق الفحص الساكن.

## مسار SMS الذهبي بعد الإصلاح

المسار المصدرّي الحالي هو:

```text
SMS_RECEIVED
  → SmsReceiver الموجود في Manifest
  → goAsync + WakeLock محدود
  → SmsProcessor
  → SmsSecurity.claimSms
  → SmsSecurity.isTrustedSmsc دون خلط sms_whitelist
  → SmsCustomerResolver
  → عند عميل معروف: مسار الحوار التجاري
  → عند عميل غير معروف: رد التسجيل الفعلي عبر SmsReplyManager
  → completeSmsClaim بعد نجاح المرحلة
```

هذا يثبت **التصميم والمسار الساكن** فقط. لا يثبت وصول broadcast من Telephony ولا نجاح `SmsManager` على شبكة فعلية ولا وصول الرد للهاتف المرسل.

## نتيجة البناء والتحقق التشغيلي

محاولة البناء المحلي لم تصل إلى مرحلة Kotlin compilation لأن Gradle أبلغ عن عدم وجود Android SDK:

```text
SDK location not found. Define a valid SDK location with an ANDROID_HOME environment variable
or by setting the sdk.dir path in local.properties.
```

لذلك الحالة الدقيقة هي:

| بوابة | الحالة |
|---|---|
| Static source audit | PASS |
| HTML/Bridge static audit | PASS |
| Local Gradle compilation | NOT RUN — Android SDK unavailable |
| GitHub Actions build | NOT RUN — GitHub integration unavailable in this session |
| APK generation | NOT VERIFIED |
| Installation on real Android device | NOT VERIFIED |
| SMS_RECEIVED from real telephony | NOT VERIFIED |
| `SmsManager` send callback | NOT VERIFIED |
| Delivery to sender phone | NOT VERIFIED |
| End-to-end `اريد ديزل` | NOT VERIFIED |
| Production Gate | BLOCKED |

## ملاحظات تشغيلية لازمة قبل اعتماد الإنتاج

يجب تشغيل workflow البناء على GitHub Actions أو بيئة تحتوي Android SDK، ثم تثبيت APK الناتج على جهاز Android حقيقي يمنح أذونات SEND/RECEIVE SMS. بعد ذلك يُختبر السيناريو على رقم عميل موجود في `party_contacts` برسالة `اريد ديزل`، ويُسجل كل من وصول broadcast، claim، رد الكمية، الرسالة التالية، المعاملة التجارية، استدعاء `SmsManager`، callback، ووصول الرد. كما يجب اختبار رقم غير مسجل للتحقق من رسالة التسجيل الجديدة.

لا يجوز اعتبار إشعار «نظام SMS يعمل بكفاءة» أو مجرد استدعاء `SmsManager` دليلاً على نجاح التسليم أو على ذرية المعاملة التجارية.

## المراجع

[1]: https://github.com/gbrhomidi/station-Abo_Ahmed2 "المستودع المرجعي للمشروع"

[2]: https://developer.android.com/develop/background-work/services/fgs "Android foreground services"

[3]: https://developer.android.com/reference/android/provider/Telephony.Sms.Intents#SMS_RECEIVED_ACTION "Android SMS_RECEIVED broadcast"
