// ==================================================
// FILE: StatsManager.kt — ✅ BUONG ESTADISTIKA NG PROYEKTO!
// VERSION: 1.0.0 — ✅ USER / PRESET / KEY / AKTIBIDAD!
// UPDATED: 2026-09-21 — KUMPLETO NA!
// ==================================================
package com.martodosko.studio

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONObject

class StatsManager(private val context: Context) {

    private val prefsMember = context.getSharedPreferences("member_system", Context.MODE_PRIVATE)
    private val prefsAdmin = context.getSharedPreferences("admin_session", Context.MODE_PRIVATE)
    private val prefsPreset = context.getSharedPreferences("preset_moderation", Context.MODE_PRIVATE)
    private val prefsGithub = context.getSharedPreferences("github_config", Context.MODE_PRIVATE)

    // ✅ BUONG ESTADISTIKA — ISANG TUWANG KUHAIN LAHAT
    fun getAllStats(): JSONObject {
        return JSONObject().apply {
            put("total_member_keys", getTotalMemberKeys())
            put("active_members", getActiveMembers())
            put("total_presets_pending", getPendingPresetsCount())
            put("total_presets_approved", getApprovedPresetsCount())
            put("github_enabled", isGitHubConnected())
            put("session_active", isSessionActive())
            put("current_user_level", getCurrentUserLevel())
        }
    }

    // ==============================================
    // ✅ BAWAT KATEGORYA NG ESTADISTIKA
    // ==============================================

    // Bilang ng lahat ng valid member keys
    fun getTotalMemberKeys(): Int {
        val keys = prefsMember.getStringSet("valid_member_keys", emptySet())
        return keys?.size ?: 0
    }

    // Bilang ng kasalukuyang aktibong user
    fun getActiveMembers(): Int {
        val active = prefsAdmin.getStringSet("active_users", emptySet())
        return active?.size ?: 0
    }

    // Preset na naghihintay ng aprobasyon
    fun getPendingPresetsCount(): Int {
        val jsonStr = prefsPreset.getString("pending_presets", "[]")
        return try { org.json.JSONArray(jsonStr).length() }
        catch (e: Exception) { 0 }
    }

    // Preset na na-aprubahan na
    fun getApprovedPresetsCount(): Int {
        val jsonStr = prefsPreset.getString("approved_presets", "[]")
        return try { org.json.JSONArray(jsonStr).length() }
        catch (e: Exception) { 0 }
    }

    // GitHub connected ba?
    fun isGitHubConnected(): Boolean {
        return prefsGithub.contains("encrypted_github_token")
    }

    // May aktibong admin session ba?
    fun isSessionActive(): Boolean {
        return prefsAdmin.getBoolean("session_active", false)
    }

    // Antas ng kasalukuyang user
    fun getCurrentUserLevel(): String {
        return prefsAdmin.getString("user_level", "GUEST") ?: "GUEST"
    }

    // ✅ I-RESET ANG LAHAT NG ESTADISTIKA — OWNER LANG
    fun resetAllStats() {
        prefsPreset.edit().clear().apply()
        prefsMember.edit().remove("valid_member_keys").apply()
    }
}
