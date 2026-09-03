================================================================================
                         🎛️ MARTODOSKO AUDIO STUDIO
                    Mixer + Guitar Effects — All-in-One App
                    Developed by MARTODOSKO © 2026 All Rights Reserved
================================================================================

PROJECT PLAN — MULA UMPISA HANGGANG HULI
----------------------------------------

--- I. LAYUNIN NG PROYEKTO ---

Gumawa ng Android application na may:
  ✅ Splash Screen — may karapatang-sipi
  ✅ Swipeable Side Menu — paglipat sa pahina
  ✅ Studio Mixer — audio controls at sliders
  ✅ Guitar Effects Cabinet — mga epekto sa tono
  ✅ Low-latency Audio Engine — mabilis na tunog
  ✅ ==============================================
     📱 AWTOMATIKONG PAGBABAGO NG BERSYON
     > SA PAGBUKAS PA LANG NG APP — AGAD NANG HAHANAP NG BAGONG BERSYON
     > KUNG MAY BAGO SA GITHUB — KUSANG DADOWNLOAD, KUSANG I-INSTALL,
     > KUSANG PAPALITAN ANG LUMANG BERSYON SA TELEPONO
     > WALANG TANONG, WALANG PINDUTIN — KUSA LANG GAGAWIN
     ==============================================

Package Name: com.martodosko.studio
Bersyon: 1.0.0

--- II. ISTRUKTURA NG PROYEKTO ---

martodosko-audio-studio/
├── android/
│   ├── app/src/main/
│   │   ├── java/com/martodosko/studio/
│   │   │   ├── MainActivity.kt          ← Pangunahing pahina
│   │   │   └── AutoUpdateManager.kt     ← 🔧 NAGHAHANAP AT NAG-IINSTALL NG BAGONG BERSYON
│   │   ├── res/
│   │   │   ├── mipmap-*/                ← 5 laki ng icon
│   │   │   ├── values/                  ← kulay, tema, teksto
│   │   │   └── layout/                  ← disenyo
│   │   ├── AndroidManifest.xml          ← pahintulot sa Internet at Pag-install
│   │   └── build.gradle.kts             ← bersyon at dependencies
│   ├── gradle.properties                ← AndroidX = ON
│   └── gradlew                          ← Gradle wrapper
├── .github/workflows/
│   └── build-apk.yml                    ← awtomatikong bumuo + ilathala ang bersyon
├── docs/                                ← 📂 LAGAYAN NG APK AT BERSYON SA GITHUB
│   ├── version.json                     ← ← ← DITO TINITINGNAN NG APP KUNG MAY BAGO
│   └── Martodosko-Studio-*.apk          ← ← ← DITO KUKUNIN ANG BAGONG APK
├── android/download/                    ← orihinal na litrato para sa icon
├── make-icons.sh                        ← gumawa ng icon mula sa litrato ng telepono
└── README.md                            ← plano at dokumentasyon

--- III. PAANO GUMAGANA ANG AWTOMATIKONG PAGBABAGO NG BERSYON ---

╔══════════════════════════════════════════════════════════════╗
║           📱 SA PAGBUKAS PA LANG NG APLIKASYON                ║
╠══════════════════════════════════════════════════════════════╣
║                                                              ║
║   STEP 1  →  AGAD BAGO PA LUMABAS ANG ANYO NG APP            ║
║              Binubuksan ang Internet — tinitingnan ang:      ║
║              https://raw.githubusercontent.com/              ║
║              fbvlink2026-lab/martodosko-audio-studio/        ║
║              main/docs/version.json                          ║
║                                                              ║
║   STEP 2  →  KINUKUMBARA ANG BERSYON                         ║
║              Bersyon sa Telepono   vs   Bersyon sa GitHub    ║
║                                                              ║
║   STEP 3  →  KUNG PAREHO — WALANG GAGAWIN ✅                  ║
║              Patuloy na gagana ang app — walang abala        ║
║                                                              ║
║   STEP 4  →  ⬇️ KUNG MAY BAGONG BERSYON SA GITHUB             ║
║              HINDI HIHINGI NG PAHINTULOT — KUSANG GAGAWIN:    ║
║                                                              ║
║              ├─ ✅ Tahimik na dina-download ang bagong APK   ║
║              │   mula sa docs/Martodosko-Studio-vX.X.X.apk    ║
║              ├─ ✅ Pagkatapos — KUSANG BUBUKAS ANG INSTALLER  ║
║              │   "I-install ang pag-update?" — HINDI KA      ║
║              │   TANUNGIN — KUSA LANG I-INSTALL              ║
║              ├─ ✅ PAPALITAN ANG LUMANG BERSYON SA TELEPONO   ║
║              │   — nandoon na ang bago — wala nang luma      ║
║              └─ ✅ Sa susunod na pagbukas — GAMIT KA NA       ║
║                  NG BAGONG BERSYON AGAD                      ║
║                                                              ║
╚══════════════════════════════════════════════════════════════╝

🔑 KAILANGANG PAHINTULOT SA AndroidManifest.xml:
  <uses-permission android:name="android.permission.INTERNET" />
  <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />
  <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

--- IV. MGA NAHARAPANG PROBLEMA AT SOLUSYON ---

1. ❌ Gradle — ayaw tumakbo
   → ✅ Gamitin ang nasa proyekto na gradlew — huwag na i-download ang panlabas na Gradle

2. ❌ AndroidX Error — hindi naka-ON
   → ✅ Idinagdag sa gradle.properties:
      android.useAndroidX=true
      android.enableJetifier=false

3. ❌ Hindi mahanap ang Material3 Theme
   → ✅ Idinagdag ang Material3 library sa dependencies:
      implementation("com.google.android.material:material:1.11.0")

4. ❌ Nawawalang ic_launcher at Theme.Martodosko
   → ✅ Gumawa ng 5 laki ng icon + themes.xml + colors.xml + strings.xml

5. ❌ Hindi makita ang litrato sa Termux
   → ✅ Binago ang daan papunta sa tunay na storage:
      /storage/emulated/0/Download

6. ❌ Agad nagpoproseso nang hindi ka nagpili
   → ✅ Hihinto muna — ilista muna — pipili ka — NUMERO LANG — bago iproseso

--- V. GUMAGAWA NG BAGONG BERSYON SA GITHUB ---

Kapag may bago kang pagbabago:
  1. I-push sa main → GitHub Actions AGAD TATAKBO
  2. Bumubuo ng APK gamit ./gradlew assembleDebug
  3. ✅ BINUBUO RIN ANG docs/version.json — may bagong bersyonCode at pangalan
  4. Inilalagay ang APK sa docs/
  5. I-commit at i-push pabalik → AGAD NAKIKITA NG LAHAT NG APP ANG BAGO

Sa susunod na pagbukas ng kahit sinong may naka-install na app:
  → AGAD NAKIKITA ANG BAGONG BERSYON
  → KUSANG DINADOWNLOAD
  → KUSANG INI-INSTALL
  → PAPALITAN ANG LUMANG BERSYON
  → GANOON LANG — TAPOS NA ✅

--- VI. BUOD NG KASUNDUAN SA PROYEKTO ---

✅ NAKASULAT NA SA PLANO:
  - Mula simula hanggang pagbuo ng APK
  - Pag-upload sa GitHub
  - 📱 SA PAGBUKAS PA LANG NG APP — AGAD NANG HAHANAP SA GITHUB
  - ⬇️ KUNG MAY BAGO — KUSANG DINADOWNLOAD
  - 📲 KUSANG I-INSTALL — WALANG TANONG
  - 🔄 PAPALITAN ANG LUMANG BERSYON SA TELEPONO
  - ✅ GAMIT NA ANG BAGO — HINDI MO NA KAILANGANG GAWIN PA

================================================================================
                              === TAPOS NA ===
================================================================================
