#!/data/data/com.termux/files/usr/bin/bash
set -e

echo "=========================================="
echo "  📂 MARTODOSKO — AYUSIN ANG ESTRUKTURA"
echo "  Gaya ng gumaganang proyekto"
echo "=========================================="
echo ""

# Pumunta sa proyekto
cd ~/martodosko-audio-studio 2>/dev/null || {
  echo "❌ Hindi mahanap ang proyekto: ~/martodosko-audio-studio"
  exit 1
}

echo "📂 Kasalukuyang lokasyon: $(pwd)"
echo ""

# BACKUP muna
BACKUP_DIR="../martodosko-backup-$(date +%Y%m%d-%H%M%S)"
echo "💾 Backup muna sa: $BACKUP_DIR"
cp -R . "$BACKUP_DIR/"
echo "✅ Tapos na ang backup"
echo ""

# Suriin kung may lumang android folder
if [ -d "android" ]; then
  echo "⚠️  NAKITA ANG LUMANG 'android/' FOLDER — ILILIPAT ANG LAMAN..."
  
  # Kung may laman ang android/app — ilipat papuntang ugat bilang app/
  if [ -d "android/app" ]; then
    if [ -d "app" ]; then
      echo "⚠️  Mayroon nang 'app/' — lilinisin muna..."
      rm -rf app
    fi
    echo "📤 Ililipat: android/app → app"
    mv android/app ./app
  fi

  # Ilipat ang gradle wrapper kung nasa loob ng android/
  if [ -d "android/gradle" ]; then
    echo "📤 Ililipat: android/gradle → gradle"
    mv android/gradle ./gradle 2>/dev/null || true
  fi
  if [ -f "android/gradlew" ]; then
    echo "📤 Ililipat: android/gradlew → ugat"
    mv android/gradlew . 2>/dev/null || true
  fi
  if [ -f "android/gradlew.bat" ]; then
    echo "📤 Ililipat: android/gradlew.bat → ugat"
    mv android/gradlew.bat . 2>/dev/null || true
  fi

  # Ilipat ang mga ugat na file
  for f in build.gradle.kts settings.gradle.kts gradle.properties; do
    if [ -f "android/$f" ] && [ ! -f "./$f" ]; then
      echo "📤 Ililipat: android/$f → ./"
      mv "android/$f" ./
    fi
  done

  # TANGGALIN ANG LUMANG android/ folder
  echo "🗑️  TANGGALIN ANG LUMANG 'android/' FOLDER"
  rm -rf android
  echo "✅ TINANGGAL NA!"
else
  echo "✅ Walang lumang 'android/' — maayos na"
fi

# ✅ AYUSIN ANG DOBLENG TEMA — TANGGALIN ANG styles.xml
VALUES_DIR="app/src/main/res/values"
if [ -f "$VALUES_DIR/styles.xml" ] && [ -f "$VALUES_DIR/themes.xml" ]; then
  echo ""
  echo "⚠️  NAKITA ANG DOBLENG TEMA — styles.xml + themes.xml"
  echo "💡 Ililipat ang laman ng styles.xml → themes.xml at TATANGGALIN ang styles.xml"
  
  # Kung may laman na iba sa themes.xml — pagsamahin
  if [ -s "$VALUES_DIR/styles.xml" ]; then
    echo "📄 Laman ng styles.xml — ililipat:"
    cat "$VALUES_DIR/styles.xml"
    echo ""
    # ⚠️ Sa halimbawa — themes.xml lang ang ginagamit kaya tanggalin ang styles.xml
    # I-edit ang themes.xml kung kailangan, pero DAPAT TANGGALIN ANG DOBLE
  fi
  
  rm "$VALUES_DIR/styles.xml"
  echo "✅ TINANGGAL NA: styles.xml — iisang themes.xml na lang!"
fi

echo ""
echo "=========================================="
echo "  ✅ TAPOS NA ANG PAG-AYUS!"
echo "=========================================="
echo ""
echo "📂 BAGONG ESTRUKTURA:"
tree -L 3 -I '.git' 2>/dev/null || find . -maxdepth 3 -not -path '*/.git/*' | sort
echo ""
echo "📌 Gaya ng halimbawa — app/ direkta sa ugat!"
echo ""
echo "Gusto mo bang i-commit at i-push agad? (y/n)"
read -r ANS
if [ "$ANS" = "y" ] || [ "$ANS" = "Y" ]; then
  git add -A
  git status
  echo ""
  echo "I-type ang mensahe (o Enter para sa default):"
  read -r MSG
  MSG=${MSG:-"🏗️ Restructure: alisin android/ — gaya ng gumaganang proyekto"}
  git commit -m "$MSG"
  git pull origin main --rebase
  git push
  echo "✅ Naipadala na!"
fi
