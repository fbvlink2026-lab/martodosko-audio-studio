# 📋 Build Failure Log — Martodosko Audio Studio

> Ito ang talaan ng mga pagkabigo habang binubuo ang proyekto.

---

## ❌ Error 1: Missing Gradle Wrapper JAR
**Petsa:** 2026-09-03
**Sanhi:** Kulang ang `gradle-wrapper.jar` — binary file na kailangan para mapatakbo ang Gradle
**Solusyon:** I-download ang totoong jar mula sa opisyal na Gradle repo

---

## ❌ Error 2: No repositories defined — Cannot resolve kotlin-stdlib
**Petsa:** 2026-09-03
**Sanhi:** Walang nakasaad na repositories sa `settings.gradle.kts` — hindi alam kung saan kukuha ang Kotlin at Android dependencies
**Solusyon:** Idagdag ang `google()` at `mavenCentral()` sa repository block

---

> ✅ Mga Pag-aayos: Tingnan ang git commit history para sa mga pagbabago.
