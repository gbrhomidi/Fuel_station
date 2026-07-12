#!/bin/bash
# ═══════════════════════════════════════════════════════════════
# cleanup.sh — سكربت تنظيف المشروع بعد التحويل
# ═══════════════════════════════════════════════════════════════

echo "🧹 Starting cleanup after Native Android conversion..."

# ─── 1. حذف ملفات assets (HTML/CSS/JS) — لم تعد مطلوبة ───
echo "📁 Removing assets/web files..."
rm -rf app/src/main/assets/web_interface.html
rm -rf app/src/main/assets/style.css
rm -rf app/src/main/assets/ui.js
rm -rf app/src/main/assets/api.js
rm -rf app/src/main/assets/.env
rm -rf app/src/main/assets/html5-qrcode.min.js
# احتفظ بـ index.html إذا كان موجوداً كـ fallback، أو احذفه أيضاً
# rm -rf app/src/main/assets/index.html

echo "✅ Assets cleaned"

# ─── 2. التحقق من عدم وجود references لـ NanoHTTPD ───
echo "🔍 Checking for remaining NanoHTTPD references..."
if grep -r "NanoHTTPD\|nanohttpd\|localhost:8080\|127.0.0.1:8080" app/src/main/java/ --include="*.kt"; then
    echo "⚠️  WARNING: Found remaining NanoHTTPD references!"
else
    echo "✅ No NanoHTTPD references found"
fi

# ─── 3. التحقق من عدم وجود WebView references ───
echo "🔍 Checking for remaining WebView references..."
if grep -r "WebView\|loadUrl\|addJavascriptInterface\|evaluateJavascript" app/src/main/java/ --include="*.kt" | grep -v "//"; then
    echo "⚠️  WARNING: Found remaining WebView references!"
else
    echo "✅ No WebView references found"
fi

# ─── 4. التحقق من وجود Compose imports ───
echo "🔍 Verifying Compose setup..."
if grep -r "androidx.compose" app/src/main/java/ --include="*.kt" | head -5; then
    echo "✅ Compose imports found"
else
    echo "❌ ERROR: No Compose imports found!"
fi

# ─── 5. Gradle sync check ───
echo "🔄 Running Gradle sync..."
./gradlew :app:dependencies --configuration implementation | grep -i "nanohttpd"
if [ $? -eq 0 ]; then
    echo "⚠️  WARNING: NanoHTTPD still in dependencies!"
else
    echo "✅ NanoHTTPD removed from dependencies"
fi

echo ""
echo "═══════════════════════════════════════════════════════════════"
echo "🎉 Cleanup complete!"
echo "═══════════════════════════════════════════════════════════════"
echo ""
echo "Next steps:"
echo "  1. ./gradlew :app:clean"
echo "  2. ./gradlew :app:assembleDebug"
echo "  3. Test the app thoroughly"
echo ""