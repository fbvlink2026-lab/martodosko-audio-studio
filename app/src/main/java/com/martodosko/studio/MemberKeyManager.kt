// ==================================================
// FILE: MemberKeyManager.kt — ✅ PAGTANGGAP NG KEY CODE!
// VERSION: 1.0.0 — PAGPASOK → PAGTUKOY → PAG-UPGRADE NG USER!
// UPDATED: 2026-09-20
// ==================================================
package com.martodosko.studio

import android.content.Context
import android.content.SharedPreferences

class MemberKeyManager(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("member_system", Context.MODE_PRIVATE)

    // ✅ TIGNAN KUNG TAMA ANG KEY CODE
    fun validateKeyCode(inputKey: String): Boolean {
        val validKeys = prefs.getStringSet("valid_member_keys", emptySet()) ?: emptySet()
        return validKeys.contains(inputKey.uppercase())
    }

    // ✅ ILAGAY ANG KEY CODE → MAGIGING MEMBER!
    fun submitKeyCode(inputKey: String): Boolean {
        val cleanKey = inputKey.uppercase().trim()
        return if (validateKeyCode(cleanKey)) {
            prefs.edit()
                .putString("user_level", "MEMBER")
                .putString("active_member_key", cleanKey)
                .apply()
            true
        } else false
    }

    // ✅ KASALUKUYANG ANTAS NG USER
    fun getUserLevel(): String = prefs.getString("user_level", "GUEST") ?: "GUEST"

    // ✅ TIGNAN KUNG MAY MEMBER KEY
    fun isMember(): Boolean = getUserLevel() == "MEMBER" || getUserLevel() == "ADMIN" || getUserLevel() == "OWNER"

    // ✅ PAG-ALIS NG KASALUKUYANG KEY
    fun signOut() {
        prefs.edit()
            .remove("user_level")
            .remove("active_member_key")
            .apply()
    }
}
