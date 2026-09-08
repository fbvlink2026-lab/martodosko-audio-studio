cmake_minimum_required(VERSION 3.22.1)
project("martodosko")

# ✅ Awtomatikong piliin ang source file
add_library(
        martodosko
        SHARED
        main.cpp
)

# ✅ Para sa pagpapakita ng mensahe sa Logcat
find_library(log-lib log)
target_link_libraries(martodosko ${log-lib})

# ==================================================
# ✅ KUNG GUSTO MO NA ANG AUDIO ENGINE — DAGDAG LANG:
# ==================================================
# target_link_libraries(martodosko ${log-lib} OpenSLES android)
