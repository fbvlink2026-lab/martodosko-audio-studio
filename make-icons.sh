#!/data/data/com.termux/files/usr/bin/bash
set -e

# 📌 DAAAN
DOWNLOAD_DIR="android/download"
RES_DIR="android/app/src/main/res"

# 📌 5 LAKI NG ICON
declare -A SIZES=(
  ["mdpi"]=48   ["hdpi"]=72   ["xhdpi"]=96   ["xxhdpi"]=144   ["xxxhdpi"]=192
)

echo "=========================================="
echo "   🎨 AWTO-GUMAGAWA NG ICONS — PINABILIS"
echo "=========================================="
mkdir -p "$DOWNLOAD_DIR"

# ==========================================
# 📋 IPAKITA LANG ANG PETSA + NUMERO — WALANG MATAGAL NA HANAP
# ==========================================
echo ""
echo "📂 MGA LITRATO SA: $DOWNLOAD_DIR/"
echo "------------------------------------------"

# KUNIN LANG ANG PETSA AT PANGALAN — AYUS AYON SA PETSA (PINAKABAGO SA TAAS)
mapfile -t FILES < <(find "$DOWNLOAD_DIR" -maxdepth 2 -type f \( -iname "*.png" -o -iname "*.jpg" -o -iname "*.jpeg" -o -iname "*.webp" \) -printf "%T@|%Td-%Tb-%TY|%p\n" 2>/dev/null | sort -rn | cut -d'|' -f2-)

if [ ${#FILES[@]} -eq 0 ]; then
  echo "⚠️ Walang nakitang litrato."
  echo "📤 Ilagay ang litrato sa: $DOWNLOAD_DIR/"
  USE_DEFAULT=true
else
  # IPASOK SA LISTAHAN — NUMERO + PETSA + PANGALAN LANG
  INDEX=1
  declare -A FILE_MAP

  for ENTRY in "${FILES[@]}"; do
    FDATE="${ENTRY#*|}"
    FPATH="${ENTRY##*|}"
    FNAME=$(basename "$FPATH")
    echo "   [$INDEX]  $FDATE  →  $FNAME"
    FILE_MAP[$INDEX]="$FPATH"
    ((INDEX++))
  done

  echo "------------------------------------------"
  echo "💡 Ilagay lang ang NUMERO (1 hanggang $((INDEX-1))):"
  read -p "👉 Piliin mo: " CHOICE

  if [ -z "${FILE_MAP[$CHOICE]}" ]; then
    echo "❌ Maling numero — gagamit ng default na icon."
    USE_DEFAULT=true
  else
    SOURCE_IMAGE="${FILE_MAP[$CHOICE]}"
    echo "✅ Pinili: $SOURCE_IMAGE"
    USE_DEFAULT=false
  fi
fi

# ==========================================
# 🔨 GUMAGA NG 5 LAKI — ILAGAY SA TAMANG FOLDER
# ==========================================
echo ""
echo "🔨 Pinoproseso..."
for DENSITY in mdpi hdpi xhdpi xxhdpi xxxhdpi; do
  SIZE=${SIZES[$DENSITY]}
  FOLDER="$RES_DIR/mipmap-$DENSITY"
  mkdir -p "$FOLDER"

  if [ "$USE_DEFAULT" = true ]; then
    magick -size ${SIZE}x${SIZE} xc:"#2196F3" \
      -fill white -draw "circle $((SIZE/2)),$((SIZE/2)) $((SIZE/2)),$((SIZE*1/6))" \
      "$FOLDER/ic_launcher.png"
  else
    magick "$SOURCE_IMAGE" -resize "${SIZE}x${SIZE}^" -gravity center -extent "${SIZE}x${SIZE}" "$FOLDER/ic_launcher.png"
  fi
  echo "   ✅ $DENSITY → ${SIZE}px"
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
echo "✅ TAPOS NA! 5 laki ng icon nalikha."
