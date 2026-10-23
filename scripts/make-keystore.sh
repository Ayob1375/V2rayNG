#!/bin/bash
# ============================================================
# ساخت keystore امضای APK برای v2rayNG سفارشی
# Build APK signing keystore for the custom v2rayNG
#
# استفاده / Usage:
#   bash make-keystore.sh
#
# خروجی / Output:
#   android_keystore.jks            -> فایل امضا
#   android_keystore_base64.txt     -> نسخه base64 برای GitHub Secrets
#
# این اسکریپت فقط یک بار اجرا شود و خروجی‌ها امن نگه داشته شوند!
# Run once and keep the outputs safe!
# ============================================================
set -o errexit

KEYSTORE_FILE="android_keystore.jks"
ALIAS="v2rayng-custom"
VALIDITY_DAYS=10950  # ~30 years

if [ -f "$KEYSTORE_FILE" ]; then
  echo "⚠️  $KEYSTORE_FILE از قبل وجود دارد. برای جلوگیری از بازنویسی، اسکریپت متوقف شد."
  echo "    $KEYSTORE_FILE already exists; aborting to avoid overwrite."
  exit 1
fi

echo "🔑 در حال ساخت keystore..."
echo "   به سوالات پاسخ دهید (حداقل یک رمز ۶ حرفی قوی برای store و key وارد کنید - هر دو را یکسان بگذارید)."
echo

keytool -genkey -v \
  -keystore "$KEYSTORE_FILE" \
  -alias "$ALIAS" \
  -keyalg RSA -keysize 2048 \
  -validity $VALIDITY_DAYS \
  -storetype JKS

echo
echo "📦 در حال ساخت نسخه base64 برای GitHub Secrets..."
base64 -w0 "$KEYSTORE_FILE" > android_keystore_base64.txt

echo
echo "✅ تمام شد! فایل‌های ساخته‌شده:"
echo "   - $KEYSTORE_FILE              (نگه‌دارید! بدون آن نمی‌توانید نسخه بعدی را آپدیت کنید)"
echo "   - android_keystore_base64.txt (محتوایش را در Secret پایین می‌گذارید)"
echo
echo "یادآوری secrets برای گیت‌هاب:"
echo "   APP_KEYSTORE_BASE64   = محتوای android_keystore_base64.txt"
echo "   APP_KEYSTORE_PASSWORD = رمزی که به سوال «Keystore password» دادید"
echo "   APP_KEYSTORE_ALIAS    = $ALIAS"
echo "   APP_KEY_PASSWORD      = همان رمز (اگر یکسان گذاشته‌اید)"
