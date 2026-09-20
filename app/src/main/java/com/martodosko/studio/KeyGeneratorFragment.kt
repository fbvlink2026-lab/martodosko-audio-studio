// ==================================================
// FILE: KeyGeneratorFragment.kt — ✅ KUMPLETONG KEY GENERATOR! MEMBER • ADMIN • REVOKE • EXPIRE!
// VERSION: 1.0.0 — ✅ GUMAGAWA NG KEY • TINGNAN LAHAT • BAWAL ANG HINDI OWNER!
// UPDATED: 2026-09-21 — 👑 OWNER LANG LANG LANG!
// ==================================================
package com.martodosko.studio

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class KeyGeneratorFragment : Fragment() {

    private lateinit var prefs: SharedPreferences
    private lateinit var prefsKeys: SharedPreferences
    private lateinit var tvUserLevel: TextView
    private lateinit var spinnerKeyType: Spinner
    private lateinit var etName: EditText
    private lateinit var spinnerExpiry: Spinner
    private lateinit var btnGenerate: Button
    private lateinit var btnClearAll: Button
    private lateinit var keysContainer: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var tvStatus: TextView

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    private val KEY_PREFIX_MEMBER = "MEMBER_"
    private val KEY_PREFIX_ADMIN = "ADMIN_"
    private val KEY_OWNER = "OWNER_KEY"

    private val expiryOptions = arrayOf(
        "Walang Expiry",
        "1 Oras",
        "1 Araw",
        "1 Linggo",
        "1 Buwan",
        "1 Taon"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_key_generator, container, false)
        prefs = requireContext().getSharedPreferences("admin_session", Context.MODE_PRIVATE)
        prefsKeys = requireContext().getSharedPreferences("issued_keys", Context.MODE_PRIVATE)

        initViews(view)
        checkPermission()
        setupSpinners()
        loadIssuedKeys()

        return view
    }

    private fun initViews(view: View) {
        tvUserLevel = view.findViewById(R.id.tv_keygen_user_level)
        spinnerKeyType = view.findViewById(R.id.spinner_key_type)
        etName = view.findViewById(R.id.et_member_name)
        spinnerExpiry = view.findViewById(R.id.spinner_expiry)
        btnGenerate = view.findViewById(R.id.btn_generate_key)
        btnClearAll = view.findViewById(R.id.btn_clear_all_keys)
        keysContainer = view.findViewById(R.id.keys_container)
        progressBar = view.findViewById(R.id.keygen_progress)
        tvStatus = view.findViewById(R.id.keygen_status)
    }

    // ==============================================
    // 👑 PERMISSION CHECK — OWNER LANG!
    // ==============================================
    private fun checkPermission() {
        val userLevel = prefs.getString("user_level", "GUEST") ?: "GUEST"
        tvUserLevel.text = "Kasalukuyang Antas: $userLevel"

        if (userLevel != "OWNER") {
            showStatus("❌ 👑 OWNER lang ang makakagamit ng Key Generator!", false)
            btnGenerate.isEnabled = false
            btnClearAll.isEnabled = false
            return
        }

        btnGenerate.setOnClickListener { generateNewKey() }
        btnClearAll.setOnClickListener { showClearAllDialog() }
    }

    private fun setupSpinners() {
        val keyTypes = arrayOf("MEMBER — Limitado", "ADMIN — Pamamahala")
        spinnerKeyType.adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_dropdown_item, keyTypes)

        spinnerExpiry.adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_dropdown_item, expiryOptions)
    }

    // ==============================================
    // 🔑 GENERATE NEW KEY
    // ==============================================
    private fun generateNewKey() {
        val name = etName.text.toString().trim()
        if (name.isEmpty()) {
            Toast.makeText(context, "Ilagay ang pangalan ng miyembro!", Toast.LENGTH_SHORT).show()
            return
        }

        val keyTypePosition = spinnerKeyType.selectedItemPosition
        val expiryPosition = spinnerExpiry.selectedItemPosition

        val prefix = if (keyTypePosition == 0) KEY_PREFIX_MEMBER else KEY_PREFIX_ADMIN
        val level = if (keyTypePosition == 0) "MEMBER" else "ADMIN"

        // Generate unique key
        val randomPart = UUID.randomUUID().toString().take(8).uppercase()
        val newKey = "$prefix${randomPart}_${System.currentTimeMillis()}"

        // Calculate expiry
        val expiresAt = calculateExpiry(expiryPosition)
        val created = System.currentTimeMillis()

        // Save to SharedPreferences
        prefsKeys.edit()
            .putString("key_${newKey}_name", name)
            .putString("key_${newKey}_level", level)
            .putLong("key_${newKey}_created", created)
            .putLong("key_${newKey}_expires", expiresAt)
            .putBoolean("key_${newKey}_active", true)
            .apply()

        // Show result
        showKeyDialog(newKey, name, level, expiresAt)

        // Refresh list
        etName.text.clear()
        loadIssuedKeys()
    }

    private fun calculateExpiry(position: Int): Long {
        val now = System.currentTimeMillis()
        return when (position) {
            0 -> 0 // Walang expiry
            1 -> now + (60 * 60 * 1000) // 1 Oras
            2 -> now + (24 * 60 * 60 * 1000) // 1 Araw
            3 -> now + (7 * 24 * 60 * 60 * 1000) // 1 Linggo
            4 -> now + (30L * 24 * 60 * 60 * 1000) // 1 Buwan
            5 -> now + (365L * 24 * 60 * 60 * 1000) // 1 Taon
            else -> 0
        }
    }

    private fun showKeyDialog(key: String, name: String, level: String, expires: Long) {
        val expiryText = if (expires == 0L) "Walang Expiry" else dateFormat.format(Date(expires))
        val message = """
            ✅ NABUO ANG BAGONG KEY!
            
            👤 Pangalan: $name
            🔑 Antas: $level
            📅 Mag-e-expire: $expiryText
            
            ─────────────────────
            $key
            ─────────────────────
            
            Ibigay ito sa miyembro. Huwag ipamahagi sa iba.
        """.trimIndent()

        AlertDialog.Builder(requireContext())
            .setTitle("✅ BAGONG KEY NABUO")
            .setMessage(message)
            .setPositiveButton("Kopyahin", null)
            .setNeutralButton("Tapos na", null)
            .show()
    }

    // ==============================================
    // 📋 LOAD ALL ISSUED KEYS
    // ==============================================
    private fun loadIssuedKeys() {
        keysContainer.removeAllViews()
        val allKeys = prefsKeys.all.filterKeys { it.startsWith("key_") && it.endsWith("_name") }

        if (allKeys.isEmpty()) {
            tvStatus.text = "📋 Wala pang naibigay na key."
            return
        }

        tvStatus.text = "📋 ${allKeys.size} key na naibigay:"

        allKeys.keys.forEach { nameKey ->
            val keyId = nameKey.removeSuffix("_name").removePrefix("key_")
            val name = prefsKeys.getString("${keyId}_name", "—") ?: "—"
            val level = prefsKeys.getString("${keyId}_level", "MEMBER") ?: "MEMBER"
            val created = prefsKeys.getLong("${keyId}_created", 0L)
            val expires = prefsKeys.getLong("${keyId}_expires", 0L)
            val active = prefsKeys.getBoolean("${keyId}_active", true)

            addKeyItem(keyId, name, level, created, expires, active)
        }
    }

    private fun addKeyItem(
        keyId: String,
        name: String,
        level: String,
        created: Long,
        expires: Long,
        active: Boolean
    ) {
        val card = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
            setBackgroundColor(0xFF1E1E2F.toInt())
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 8) }
        }

        // Top row: Name + Level + Status
        val topRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
        }

        val nameTv = TextView(requireContext()).apply {
            text = name
            textSize = 15f
            setTextColor(0xFFFFFFFF.toInt())
            setTypeface(null, android.graphics.Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val levelTv = TextView(requireContext()).apply {
            text = level
            textSize = 12f
            setTextColor(if (level == "ADMIN") 0xFFFF9800.toInt() else 0xFF4CAF50.toInt())
            setPadding(8, 2, 8, 2)
            setBackgroundColor(0xFF2A2A3A.toInt())
        }

        val statusTv = TextView(requireContext()).apply {
            text = if (active) "✅ AKTIBO" else "❌ BINAWAL"
            textSize = 11f
            setTextColor(if (active) 0xFF4CAF50.toInt() else 0xFFFF5252.toInt())
            setPadding(8, 2, 8, 2)
            setBackgroundColor(0xFF2A2A3A.toInt())
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(8, 0, 0, 0) }
        }

        topRow.addView(nameTv)
        topRow.addView(levelTv)
        topRow.addView(statusTv)

        // Dates
        val dateText = if (expires == 0L) {
            "📅 Binuo: ${dateFormat.format(Date(created))} • Walang Expiry"
        } else {
            "📅 Binuo: ${dateFormat.format(Date(created))} • Mag-e-expire: ${dateFormat.format(Date(expires))}"
        }
        val dateTv = TextView(requireContext()).apply {
            text = dateText
            textSize = 11f
            setTextColor(0xFF888888.toInt())
            setPadding(0, 6, 0, 0)
        }

        // Key snippet
        val keyTv = TextView(requireContext()).apply {
            text = "🔑 $keyId"
            textSize = 10f
            setTextColor(0xFF666666.toInt())
            setPadding(0, 4, 0, 0)
            setTextIsSelectable(true)
        }

        // Buttons
        val btnRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 12, 0, 0)
        }

        val revokeBtn = Button(requireContext()).apply {
            text = if (active) "🚫 BAWALIN" else "✅ BUHAYIN"
            textSize = 12f
            setBackgroundColor(if (active) 0xFF8B0000.toInt() else 0xFF2E7D32.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            setOnClickListener { toggleKeyStatus(keyId, !active) }
            layoutParams = LinearLayout.LayoutParams(0, 40, 1f).apply { setMargins(0, 0, 4, 0) }
        }

        val deleteBtn = Button(requireContext()).apply {
            text = "🗑️ BURAHIN"
            textSize = 12f
            setBackgroundColor(0xFF4A1A1A.toInt())
            setTextColor(0xFFFF5252.toInt())
            setOnClickListener { showDeleteDialog(keyId, name) }
            layoutParams = LinearLayout.LayoutParams(0, 40, 1f).apply { setMargins(4, 0, 0, 0) }
        }

        btnRow.addView(revokeBtn)
        btnRow.addView(deleteBtn)

        card.addView(topRow)
        card.addView(dateTv)
        card.addView(keyTv)
        card.addView(btnRow)

        keysContainer.addView(card)
    }

    // ==============================================
    // 🚫 REVOKE / RE-ACTIVATE KEY
    // ==============================================
    private fun toggleKeyStatus(keyId: String, newActive: Boolean) {
        prefsKeys.edit()
            .putBoolean("key_${keyId}_active", newActive)
            .apply()
        loadIssuedKeys()
        Toast.makeText(context, if (newActive) "✅ Key naibalik na!" else "🚫 Key binawalan na!", Toast.LENGTH_SHORT).show()
    }

    // ==============================================
    // 🗑️ DELETE SINGLE KEY
    // ==============================================
    private fun showDeleteDialog(keyId: String, name: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("🗑️ Burahin ang Key?")
            .setMessage("Burahin ang key para kay: $name?\nHindi na ito mababawi.")
            .setPositiveButton("Burahin") { _, _ ->
                deleteKey(keyId)
            }
            .setNegativeButton("Kanselahin", null)
            .show()
    }

    private fun deleteKey(keyId: String) {
        prefsKeys.edit()
            .remove("key_${keyId}_name")
            .remove("key_${keyId}_level")
            .remove("key_${keyId}_created")
            .remove("key_${keyId}_expires")
            .remove("key_${keyId}_active")
            .apply()
        loadIssuedKeys()
        Toast.makeText(context, "🗑️ Burado na ang key!", Toast.LENGTH_SHORT).show()
    }

    // ==============================================
    // 🗑️ CLEAR ALL KEYS
    // ==============================================
    private fun showClearAllDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("⚠️ Burahin ang LAHAT ng Key?")
            .setMessage("Ito ay buburahin ang lahat ng naibigay na key.\nHindi na ito mababawi.")
            .setPositiveButton("LAHAT BURAHIN") { _, _ ->
                prefsKeys.edit().clear().apply()
                loadIssuedKeys()
                Toast.makeText(context, "🗑️ Lahat ng key ay burado na!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Kanselahin", null)
            .show()
    }

    private fun showStatus(msg: String, success: Boolean) {
        tvStatus.text = msg
        tvStatus.setTextColor(if (success) 0xFF4CAF50.toInt() else 0xFFFF5252.toInt())
    }
}
