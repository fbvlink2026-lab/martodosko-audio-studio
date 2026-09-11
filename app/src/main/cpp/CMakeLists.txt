cmake_minimum_required(VERSION 3.22.1)
project("martodosko")

# ==================================================
# ✅ DADAGDAG — OBOE: Google Low-Latency Audio Library
# ==================================================
include(FetchContent)
FetchContent_Declare(
        oboe
        GIT_REPOSITORY https://github.com/google/oboe.git
        GIT_TAG 1.9.0
)
FetchContent_MakeAvailable(oboe)

# ✅ Awtomatikong piliin ang source file
add_library(
        martodosko
        SHARED
        main.cpp
)

# ✅ Para sa pagpapakita ng mensahe sa Logcat + OBOE + ANDROID
find_library(log-lib log)
target_link_libraries(
        martodosko
        oboe          # ✅ Oboe — Mababang Latency na Audio
        ${log-lib}    # ✅ Logcat — NANDOON PA RIN!
        android       # ✅ Android Platform — kailangan ng Oboe
)
