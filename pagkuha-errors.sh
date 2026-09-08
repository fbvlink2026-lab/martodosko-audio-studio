#!/data/data/com.termux/files/usr/bin/bash
set -e

# 📌 DAAAN KUNG SAAN ISUSULAT ANG MGA ERROR
DOCS_DIR="docs"
OUTPUT_FILE="$DOCS_DIR/failed.md"

echo "=========================================="
echo "   📜 KUKUNIN LANG ANG MGA ERROR"
echo "   📂 Isusulat sa: $OUTPUT_FILE"
echo "=========================================="
echo ""

mkdir -p "$DOCS_DIR"

# 📋 PAGPILI KUNG SAAN GALING ANG LOG
echo "📌 Saan galing ang log?"
echo "   [1] I-paste ko dito ang buong log"
echo "   [2] Ibigay ko ang link ng GitHub Actions"
read -p "👉 Piliin mo (1 o 2): " PILI

BUONG_LOG=""

if [ "$PILI" = "1" ]; then
  echo ""
  echo "📋 I-paste ang BUONG LOG — tapos pindutin Ctrl+D:"
  BUONG_LOG=$(cat)
elif [ "$PILI" = "2" ]; then
  echo ""
  read -p "👉 I-paste ang link: " LINK
  echo "🔍 Binabasa ang link..."
  BUONG_LOG=$(curl -s "$LINK")
else
  echo "❌ Maling numero — itinigil."
  exit 1
fi

echo ""
echo "🔍 Hinahanap ang mga error..."

# ==========================================
# 📤 KUNIN LANG ANG MGA ERROR — WALANG IBA
# ==========================================
MGA_ERROR=$(echo "$BUONG_LOG" | grep -iE \
  'error:|FAILED|Exception|went wrong|not found|failed|Error:|BUILD FAILED|Execution failed' \
  --color=never || true)

# ==========================================
# ✅ ISULAT SA docs/failed.md
# ==========================================
{
  echo "# ❌ MGA ERROR SA PAGBUO NG APK"
  echo ""
  echo "📅 Petsa: $(date '+%d-%b-%Y %H:%M:%S')"
  echo ""
  echo "---"
  echo ""
  if [ -z "$MGA_ERROR" ]; then
    echo "✅ **WALANG NAKITANG ERROR — Matagumpay ang pagbuo!**"
  else
    echo "## 📋 MGA DAHILAN NG PAGBAGSAK:"
    echo ""
    echo "\`\`\`"
    echo "$MGA_ERROR"
    echo "\`\`\`"
  fi
  echo ""
  echo "---"
} > "$OUTPUT_FILE"

# ==========================================
# ✅ IPAPAKITA RIN DITO SA SCREEN
# ==========================================
echo ""
echo "=========================================="
echo "   📄 NAKASULAT NA SA: $OUTPUT_FILE"
echo "=========================================="
echo ""
cat "$OUTPUT_FILE"

# ==========================================
# 📤 I-COMMIT AT I-PUSH AGAD
# ==========================================
echo ""
read -p "💡 I-commit at i-push ba sa GitHub? [Y/n]: " SAGOT
if [[ "$SAGOT" != "n" && "$SAGOT" != "N" ]]; then
  git pull origin main --rebase
  git add "$OUTPUT_FILE"
  git commit -m "📄 docs/failed.md: Naitala ang mga error sa pagbuo"
  git push origin main
  echo "✅ Nai-push na sa GitHub — nakikita na sa docs/failed.md"
fi
