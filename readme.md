================================================================================
                         🎛️ MARTODOSKO AUDIO STUDIO
                    Mixer + Guitar Effects — All-in-One App
                    Developed by MARTODOSKO © 2026 All Rights Reserved
================================================================================

PROJECT PLAN — FROM START TO FINISH
------------------------------------

--- I. PAGSISIMULA NG PROYEKTO ---

1. Layunin ng Proyekto
   - Gumawa ng Android application na may:
     * Splash Screen na may karapatang-sipi / copyright
     * Swipeable Side Menu Navigation
     * Studio Mixer UI — audio controls at sliders
     * Guitar Effects Cabinet — mga epekto sa tono
     * Low-latency C++ Audio Engine — mabilis na pagproseso ng tunog
     * Auto-Update mula sa GitHub
   - Package Name: com.martodosko.studio
   - Bersyon: 1.0.0

2. Istruktura ng Proyekto
   - android/                  → Android application files
   - android/app/src/main/
       ├── java/com/martodosko/studio/   → Kotlin/Java source code
       ├── res/                          → Resources (icons, tema, layout)
       │    ├── mipmap-*/                → 5 laki ng icon ng app
       │    ├── values/                   → kulay, tema, teksto
       │    └── layout/                   → disenyo ng pahina
       └── AndroidManifest.xml           → Paglalarawan ng app
   - .github/workflows/        → GitHub Actions para awtomatikong pagbuo ng APK
   - android/download/         → Pansamantala — orihinal na litrato para sa icon
   - README.md                 → Ito — dokumentasyon at plano

--- II. MGA PROBLEMA AT SOLUSYON — HAKBANG-HAKBANG ---

1. Unang Hadlang — Bersyon ng Gradle
   - Problema: Pinilit ang panlabas na pag-download ng Gradle — nagkakasalungat ang bersyon
   - Solusyon: Gamitin ang nasa proyekto — ./gradlew lang. Huwag baguhin kung gumagana na.
     * Gradle 8.2 ang gumagana — kaya 8.2 na lang
     * Tinanggal ang: wget + unzip + export PATH + gradle wrapper --gradle-version
     * Direktang: ./gradlew assembleDebug

2. AndroidX Error
   - Mensahe: Configuration contains AndroidX dependencies, but android.useAndroidX not enabled
   - Solusyon: Lumikha ng android/gradle.properties
     android.useAndroidX=true
     android.enableJetifier=false

3. Nawawalang Tema — Material3
   - Mensahe: resource style/Theme.Material3.Dark.NoActionBar not found
   - Solusyon: Idagdag sa dependencies:
     implementation("com.google.android.material:material:1.11.0")
     Lumikha ng themes.xml na nagmamana ng Material3

4. Nawawalang Icon at Tema sa Manifest
   - Mensahe: mipmap/ic_launcher hindi mahanap
   - Mensahe: style/Theme.Martodosko hindi mahanap
   - Solusyon:
     * Lumikha ng 5 laki ng icon: mdpi(48), hdpi(72), xhdpi(96), xxhdpi(144), xxxhdpi(192)
     * Lumikha ng adaptive icon para sa Android 8.0+
     * Lumikha ng Theme.Martodosko sa themes.xml
     * Lumikha ng kulay at pangalan ng app

5. Tamang Daan ng Litrato — Storage ng Telepono
   - Problema: Hinahanap sa loob ng proyekto — hindi makita ang litrato
   - Solusyon:
     * Tunay na daan: /storage/emulated/0/Download
     * Termux storage access: termux-setup-storage
     * Script: Ilista → Pumili ka muna → Pagkatapos iproseso

6. GitHub Actions Workflow — Ang Kabuuan
   - Hakbang:
     1. Kunin ang bersyon mula sa tag o commit
     2. Isulat ang versionCode at versionName sa build.gradle.kts
     3. Tingnan kung may Gradle Wrapper — kung wala lang saka i-setup
     4. Siguraduhing may karapatan ang gradlew
     5. Patakbuhin: ./gradlew assembleDebug
     6. Kung matagumpay — kopyahin ang APK → docs/
     7. I-upload bilang Artifact — mananatili ng 30 araw
     8. I-commit at i-push pabalik ang talaan at APK

--- III. ANG SCRIPT NG PAGGAWA NG ICONS ---

Pangalan: make-icons.sh
Lokasyon: Ugat ng proyekto

Gumagana Ganito:
  1. Tumingin sa: /storage/emulated/0/Download
  2. Ilista ang lahat ng litrato — pinakabago sa taas — may petsa at numero
  3. Hihintayin kang pumili — NUMERO LANG ang itina-type
  4. HINDI magpoproseso hangga't hindi ka nakakapili — SIGURADO KA MUNA
  5. Kapag napili — kopyahin at i-resize sa 5 laki gamit ang ImageMagick
  6. Ilagay sa tamang mipmap-* folder
  7. Lumikha ng kulay, tema, adaptive icon
  8. I-commit at i-push — handa nang mabuo

--- IV. KASALUKUYANG KATAYUAN NG PROYEKTO ---

✅ Tapos na:
  - Gradle 8.2 — walang pinipilit na panlabas
  - AndroidX — naka-on na sa gradle.properties
  - Material3 — kasama na sa dependencies
  - Theme.Martodosko — nalikha na
  - 5 laki ng icon — nalilikha mula sa iyong litrato
  - Tamang daan — storage ng telepono na ang binabasa
  - Pagpili muna bago iproseso — sigurado ka
  - GitHub Actions — buong workflow nakasulat na

⏳ Hinihintay:
  - Matagumpay na pagtakbo ng Actions → APK mabuo
  - I-download ang APK → I-install sa telepono

--- V. PAGPAPATAKBO NG PROYEKTO ---

Sa Termux:
  1. Ilagay ang litrato sa: Telepono → Download folder
  2. ./make-icons.sh          → Pumili ng icon → Awtomatikong ilagay
  3. martostudio              → git pull → commit → push → GitHub Actions tatakbo

Sa GitHub:
  - Pumunta sa Actions tab
  - Tingnan ang pagtakbo — kapag tapos → I-download ang APK sa Artifacts
  - I-install sa telepono — TAPOS NA!

--- VI. BUOD NG MGA TINANGGAL AT IDINAGDAG ---

TINANGGAL:
  - Panlabas na wget + unzip ng Gradle
  - Pagbabago ng wrapper bersyon nang kusa
  - Hindi kailangang pagpilit sa bersyon 8.3

IDINAGDAG:
  - gradle.properties → AndroidX
  - Material3 library
  - themes.xml + colors.xml + strings.xml
  - 5 laki ng icon sa tamang mipmap folder
  - make-icons.sh — pagbasa mula sa tunay na storage ng telepono
  - Pagpili muna bago iproseso — hindi kusa

================================================================================
                              === TAPOS NA ===
================================================================================
