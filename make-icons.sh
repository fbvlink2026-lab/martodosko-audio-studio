#!/data/data/com.termux/files/usr/bin/bash
set -e

# 📌 DAAAN
DOWNLOAD_DIR="android/download"
RES_DIR="android/app/src/main/res"

# 📌 5 LAKI NG ANDROID ICON
declare -A SIZES=(
  ["mdpi"]=48   ["hdpi"]=72   ["xhdpi"]=96   ["xxhdpi"]=144   ["xxxhdpi"]=192
)

echo "=========================================="
echo "   🎨 AWTO-GUMAGAWA NG ICONS"
echo "   KUKUNIN ANG PINAKABAGONG LITRATO — WALANG PILI"
echo "=========================================="

mkdir -p "$DOWNLOAD_DIR"

# ==========================================
# 🔍 KUNIN ANG PINAKABAGONG LITRATO — AWTOMATIKO
# ==========================================
SOURCE_IMAGE=""

# Hanapin ang lahat ng litrato, ayusin mula sa PINAKABAGO hanggang LUMA
mapfile -t ALL_FILES < <(
  find "$DOWNLOAD_DIR" -maxdepth 2 -type f \( -iname "*.png" -o -iname "*.jpg" -o -iname "*.jpeg" -o -iname "*.webp" \) -printf "%T@ %p\n" 2>/dev/null \
  | sort -rn \
  | head -1 \
  | cut -d' ' -f2-
)

if [ ${#ALL_FILES[@]} -gt 0 ] && [ -n "${ALL_FILES[0]}" ] && [ -f "${ALL_FILES[0]}" ]; then
  SOURCE_IMAGE="${ALL_FILES[0]}"
  FNAME=$(basename "$SOURCE_IMAGE")
  FDATE=$(stat -c "%d-%b-%Y" "$SOURCE_IMAGE")
  echo "✅ PINAKABAGO NA LITRATO: $FNAME  ($FDATE)"
  USE_DEFAULT=false
else
  echo "⚠️ Walang nakitang litrato sa: $DOWNLOAD_DIR/"
  echo "🎨 Gagamit ng DEFAULT na asul na icon"
  USE_DEFAULT=true
fi

# ==========================================
# 🔨 BUMUO NG 5 LAKI — ILAGAY SA TAMANG FOLDER
# ==========================================
echo ""
echo "🔨 Pinoproseso..."

for DENSITY in mdpi hdpi xhdpi xxhdpi xxxhdpi; do
  SIZE=${SIZES[$DENSITY]}
  FOLDER="$RES_DIR/mipmap-$DENSITY"
  mkdir -p "$FOLDER"

  OUTPUT="$FOLDER/ic_launcher.png"

  if [ "$USE_DEFAULT" = true ]; then
    magick -size ${SIZE}x${SIZE} xc:"#2196F3" \
      -fill white -draw "circle $((SIZE/2)),$((SIZE/2)) $((SIZE/2)),$((SIZE*1/6))" \
      "$OUTPUT"
  else
    magick "$SOURCE_IMAGE" -resize "${SIZE}x${SIZE}^>" -background "#2196F3" -gravity center -extent "${SIZE}x${SIZE}" "$OUTPUT"
  fi

  echo "   ✅ $DENSITY  →  ${SIZE}x${SIZE}"
done

# ==========================================
# 📄 ADAPTIVE ICON + KULAY + TEMA
# ==========================================
mkdir -p "$RES_DIR/mipmap-anydpi-v26"
cat > "$RES_DIR/mipmap-anydpi-v26/ic_launcher.xml" <<'XML'
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/ic_launcher_background"/>
    <foreground android:drawable="@mipmap/ic_launcher"/>
</adaptive-icon>
XML

mkdir -p "$RES_DIR/values"
cat > "$RES_DIR/values/colors.xml" <<'XML'
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="ic_launcher_background">#2196F3</color>
    <color name="ic_launcher_foreground">#FFFFFF</color>
    <color name="purple_500">#6200EE</color>
    <color name="black">#FF000000</color>
    <color name="white">#FFFFFFFF</color>
</resources>
XML

cat > "$RES_DIR/values/themes.xml" <<'XML'
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="Theme.Martodosko" parent="Theme.Material3.Dark.NoActionBar">
        <item name="colorPrimary">@color/purple_500</item>
        <item name="android:statusBarColor">@color/black</item>
    </style>
</resources>
XML

echo ""
echo "✅ TAPOS NA! Lahat ng icon nalikha."
echo "📂 Lokasyon: $RES_DIR/mipmap-*/"
