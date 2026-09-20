// ==================================================
// FILE: PresetModeration.kt — ✅ PAMAMAHALA NG PRESETS! APRUBUHAN / TANGGIHAN!
// VERSION: 1.0.0 — ✅ LISTAHAN + APRUBUHAN + TANGGIHAN! NAKA-SAVER SA GITHUB!
// UPDATED: 2026-09-21 — KUMPLETO NA!
// ==================================================
package com.martodosko.studio

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

class PresetModeration(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("preset_moderation", Context.MODE_PRIVATE)
    private val prefsUser: SharedPreferences = context.getSharedPreferences("admin_session", Context.MODE_PRIVATE)

    // ✅ KUNIN ANG LAHAT NG NAKABINGING PRESET
    fun getPendingPresets(): JSONArray {
        val jsonStr = prefs.getString("pending_presets", "[]")
        return try {
            JSONArray(jsonStr)
        } catch (e: Exception) {
            JSONArray()
        }
    }

    // ✅ APRUBUHAN ANG PRESET — ILIPAT SA APPROVED
    fun approvePreset(presetId: String): Result<String> {
        val pending = getPendingPresets()
        val approved = getApprovedPresets()
        var presetObj: JSONObject? = null
        var indexToRemove = -1

        for (i in 0 until pending.length()) {
            val obj = pending.getJSONObject(i)
            if (obj.optString("id") == presetId) {
                presetObj = obj
                indexToRemove = i
                break
            }
        }

        if (presetObj == null) return Result.failure(Exception("Preset hindi natagpuan!"))

        // ✅ Ilipat sa Approved
        approved.put(presetObj)
        removePendingAt(indexToRemove)
        saveApprovedPresets(approved)

        return Result.success("✅ Preset na-aprubahan!")
    }

    // ✅ TANGGIHAN ANG PRESET — BURAHIN
    fun rejectPreset(presetId: String, reason: String = ""): Result<String> {
        val pending = getPendingPresets()
        var indexToRemove = -1

        for (i in 0 until pending.length()) {
            if (pending.getJSONObject(i).optString("id") == presetId) {
                indexToRemove = i
                break
            }
        }

        if (indexToRemove == -1) return Result.failure(Exception("Preset hindi natagpuan!"))

        removePendingAt(indexToRemove)
        return Result.success("❌ Preset na-tanggihan! $reason")
    }

    // ✅ LAHAT NG APRUBADONG PRESET
    fun getApprovedPresets(): JSONArray {
        val jsonStr = prefs.getString("approved_presets", "[]")
        return try { JSONArray(jsonStr) }
        catch (e: Exception) { JSONArray() }
    }

    // ✅ DAGDAG NG BAGONG PRESET SA PENDING (gagamitin ng user app)
    fun submitForModeration(presetJson: JSONObject): Boolean {
        val pending = getPendingPresets()
        pending.put(presetJson)
        prefs.edit().putString("pending_presets", pending.toString()).apply()
        return true
    }

    // ==============================================
    // ✅ TULONG — PANLOOB NA PAGGAMIT
    // ==============================================
    private fun removePendingAt(index: Int) {
        val pending = getPendingPresets()
        val newArray = JSONArray()
        for (i in 0 until pending.length()) {
            if (i != index) newArray.put(pending.get(i))
        }
        prefs.edit().putString("pending_presets", newArray.toString()).apply()
    }

    private fun saveApprovedPresets(approved: JSONArray) {
        prefs.edit().putString("approved_presets", approved.toString()).apply()
    }

    // ✅ BILANG NG PENDING
    fun getPendingCount(): Int = getPendingPresets().length()
}
