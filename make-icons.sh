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
echo "   🎨 GUMAGAWA NG ICONS — PUMILI MUNA BAGO GUMAGAWA"
echo "=========================================="

mkdir -p "$DOWNLOAD_DIR"

# ==========================================
# 📋 ILISTA MUNA — WALANG GAGAWIN HANGGA'T HINDI NAKAPILI
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
  echo "📤 Ilagay muna ang litrato bago tumakbo."
  echo "💡 Pindutin ang [Enter] para gumamit ng DEFAULT na asul na icon,"
  echo "   o pindutin Ctrl+C para itigil at maglagay muna ng litrato."
  read -r
  USE_DEFAULT=true
else
  echo ""
  echo "📂 MGA LITRATO — PINAKABAGO SA TAAS:"
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
  echo "❓ PUMILI KA MUNA — ILAGAY ANG NUMERO BAGO MAGPAPROSESO:"

  while true; do
    read -p "👉 Ang iyong napiling numero: " CHOICE

    # Suriin kung tama ang numero
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
# ✅ DITO LANG MAGSISIMULA ANG PAGPAPROSESO — PAGKATAPUS MONG PUMILI!
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
echo "✅ TAPOS NA! Napili mo muna bago iproseso — sigurado ka!"
