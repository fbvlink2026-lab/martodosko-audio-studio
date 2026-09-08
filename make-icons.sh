#!/data/data/com.termux/files/usr/bin/bash
set -e

# 📌 TAMA NA DAAAN — TUNAY NA STORAGE NG TELEPONO
# Ito ang daan na nakikita ang iyong mga litrato sa telepono
PHONE_STORAGE="/storage/emulated/0"
DOWNLOAD_DIR="$PHONE_STORAGE/Download"
# Pwede rin dito kung saan mo inilagay:
# DOWNLOAD_DIR="$PHONE_STORAGE/Pictures"
# DOWNLOAD_DIR="$PHONE_STORAGE/Download/Martodosko"

RES_DIR="android/app/src/main/res"

# 📌 5 LAKI NG ANDROID ICON
declare -A SIZES=(
  ["mdpi"]=48   ["hdpi"]=72   ["xhdpi"]=96   ["xxhdpi"]=144   ["xxxhdpi"]=192
)

echo "=========================================="
echo "   🎨 GUMAGAWA NG ICONS — MULA SA STORAGE NG TELEPONO"
echo "=========================================="
echo "📂 Daan na hinahanapan: $DOWNLOAD_DIR"
echo ""

# === SURIIN KUNG NAKIKITA ANG STORAGE ===
if [ ! -d "$DOWNLOAD_DIR" ]; then
  echo "⚠️ HINDI MAKITA ANG DAAAN: $DOWNLOAD_DIR"
  echo ""
  echo "📌 MGA POSIBLENG DAAAN SA TELEPONO:"
  echo "   1. Download       → /storage/emulated/0/Download"
  echo "   2. Pictures       → /storage/emulated/0/Pictures"
  echo "   3. Martodosko      → /storage/emulated/0/Download/Martodosko"
  echo ""
  read -p "👉 Ilagay ang NUMERO kung saan mo inilagay ang litrato: " PILI

  case "$PILI" in
    1) DOWNLOAD_DIR="$PHONE_STORAGE/Download" ;;
    2) DOWNLOAD_DIR="$PHONE_STORAGE/Pictures" ;;
    3) DOWNLOAD_DIR="$PHONE_STORAGE/Download/Martodosko" ;;
    *) echo "❌ Maling numero — susubok sa proyekto folder"; DOWNLOAD_DIR="android/download" ;;
  esac
  echo "✅ Gagamitin na: $DOWNLOAD_DIR"
fi

# === SIGURADUHIN MAY FOLDER ===
mkdir -p "$DOWNLOAD_DIR" 2>/dev/null || true

# ==========================================
# 📋 ILISTA ANG MGA LITRATO — HIHINTAYIN ANG PAGPILI MO
# ==========================================
mapfile -t ALL_FILES < <(
  find "$DOWNLOAD_DIR" -maxdepth 2 -type f \( -iname "*.png" -o -iname "*.jpg" -o -iname "*.jpeg" -o -iname "*.webp" \) -printf "%T@ %Td-%Tb-%TY|%p\n" 2>/dev/null | sort -rn | cut -d' ' -f2-
)

TOTAL=${#ALL_FILES[@]}
SOURCE_IMAGE=""
USE_DEFAULT=false

if [ $TOTAL -eq 0 ]; then
  echo ""
  echo "⚠️ WALANG LITRATO SA: $DOWNLOAD_DIR/"
  echo "💡 Ilagay muna ang litrato sa folder na iyon."
  echo "   Pindutin Enter = gumamit ng DEFAULT na asul na icon"
  echo "   Pindutin Ctrl+C = itigil at ilagay muna ang litrato"
  read -r
  USE_DEFAULT=true
else
  echo ""
  echo "📂 MGA LITRATO SA: $DOWNLOAD_DIR"
  echo "------------------------------------------"

  declare -A FILE_PATHS
  NUM=1

  for ENTRY in "${ALL_FILES[@]}"; do
    FDATE="${ENTRY%|*}"
    FPATH="${ENTRY#*|}"
    FNAME=$(basename "$FPATH")
    echo "   [$NUM]  $FDATE  →  $FNAME"
    FILE_PATHS[$NUM]="$FPATH"
    ((NUM++))
  done

  echo "------------------------------------------"
  echo "❓ PUMILI KA MUNA BAGO MAGPAPROSESO — ILAGAY ANG NUMERO:"

  while true; do
    read -p "👉 Ang iyong napiling numero: " CHOICE

    if [[ "$CHOICE" =~ ^[0-9]+$ ]] && [ -n "${FILE_PATHS[$CHOICE]}" ]; then
      SOURCE_IMAGE="${FILE_PATHS[$CHOICE]}"
      FNAME=$(basename "$SOURCE_IMAGE")
      echo "✅ NAPILI MO: $FNAME"
      break
    else
      echo "⚠️ Walang numerong [$CHOICE] — subukan muli:"
    fi
  done
fi

# ==========================================
# 🔨 DITO LANG MAGSISIMULA ANG PAGPAPROSESO
# ==========================================
echo ""
echo "🔨 NAGSISIMULA NA ANG PAGPAPROSESO..."

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
echo "✅ TAPOS NA! Mula sa storage ng telepono → nabuo ang 5 laki ng icon!"
