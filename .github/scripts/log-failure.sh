#!/bin/bash
set -e

cd "$GITHUB_WORKSPACE"
TIMESTAMP=$(date '+%Y-%m-%d %H:%M:%S UTC')
RUN_URL="$GITHUB_SERVER_URL/$GITHUB_REPOSITORY/actions/runs/$GITHUB_RUN_ID"

cat > docs/failed.md <<MDEND
# 📋 Build Failure Log — Martodosko Audio Studio

> AWTO GENERATED — Huling na-update: ${TIMESTAMP}

---

## ❌ Huling Pagkabigo
- **Petsa:** ${TIMESTAMP}
- **Workflow Run:** ${RUN_URL}

### Buong Error Log:
\`\`\`
$(cat build-log.txt 2>/dev/null || echo "Walang log na mabasa")
\`\`\`

---

## 📋 Kasaysayan ng Pagkabigo at Pag-aayos

### ❌ Error 1 — Missing Gradle Wrapper JAR
**Sanhi:** Kulang ang `gradle-wrapper.jar` — kailangan para mapatakbo ang Gradle
**Solusyon:** Idinagdag ang totoong jar file

### ❌ Error 2 — No repositories defined
**Sanhi:** Walang `google()` at `mavenCentral()` sa settings.gradle.kts
**Solusyon:** Idinagdag ang repositories block
MDEND

git config --global user.name "github-actions[bot]"
git config --global user.email "github-actions[bot]@users.noreply.github.com"
git add docs/failed.md
git commit -m "📝 AWTO-LOG: Naitala ang pagkabigo sa build" --allow-empty || true
git push || true
