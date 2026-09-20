// ==================================================
// FILE: KeyGeneratorFragment.kt — ✅ REWORKED! WALANG XML! BINUBUO LAHAT SA KOTLIN!
// VERSION: 2.0.0 — ✅ OWNER-ONLY KEY GENERATOR! EXPIRE, REVOKE, DELETE! WALANG FINDBYVIEWID!
// UPDATED: 2026-09-21 — LAHAT NG UI BINUO SA onCreateView! WALANG XML KAILANGAN!
// ==================================================
package com.martodosko.studio

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
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
    ): View {
        val root = ScrollView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(0xFF12121F.toInt())
            setPadding(20, 20, 20, 30)
        }

        val mainContainer = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        prefs = requireContext().getSharedPreferences("admin_session", Context.MODE_PRIVATE)
        prefsKeys = requireContext().getSharedPreferences("issued_keys", Context.MODE_PRIVATE)

        // ==============================================
        // 🎨 HEADER
        // ==============================================
        mainContainer.addView(createHeader())

        // ==============================================
        // 👑 CURRENT USER LEVEL
        // ==============================================
        tvUserLevel = TextView(requireContext()).apply {
            text = "⏰ Kinakarga..."
            textSize = 14f
            setTextColor(0xFF888888.toInt())
            setBackgroundColor(0xFF1E1E2F.toInt())
            setPadding(14, 12, 14, 12)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 20) }
        }
        mainContainer.addView(tvUserLevel)

        // ==============================================
        // 🔑 KEY TYPE
        // ==============================================
        mainContainer.addView(createLabel("🔑 URI NG KEY"))
        spinnerKeyType = Spinner(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 16) }
        }
        mainContainer.addView(spinnerKeyType)

        // ==============================================
        // 👤 PANGALAN NG MIYEMBRO
        // ==============================================
        mainContainer.addView(createLabel("👤 PANGALAN NG MIYEMBRO"))
        etName = EditText(requireContext()).apply {
            hint = "Ilagay ang pangalan..."
            setBackgroundColor(0xFF1A1A2E.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            setHintTextColor(0xFF666666.toInt())
            setPadding(14, 14, 14, 14)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 16) }
        }
        mainContainer.addView(etName)

        // ==============================================
        // 📅 EXPIRY
        // ==============================================
        mainContainer.addView(createLabel("📅 MAG-E-EXPIRE"))
        spinnerExpiry = Spinner(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 20) }
        }
        mainContainer.addView(spinnerExpiry)

        // ==============================================
        // 🔘 BUTTONS — GENERATE • CLEAR ALL
        // ==============================================
        val btnRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 20) }
        }

        btnGenerate = createButton("🔑 BUMUO NG KEY", 0xFF2E7D32.toInt())
        btnClearAll = createButton("🗑️ BURAHIN LAHAT", 0xFFB71C1C.toInt())

        btnRow.addView(btnGenerate)
        btnRow.addView(btnClearAll)
        mainContainer.addView(btnRow)

        // ==============================================
        // 📋 STATUS
        // ==============================================
        tvStatus = TextView(requireContext()).apply {
            text = "📋 Handa na — I-check muna ang iyong antas..."
            textSize = 13f
            setTextColor(0xFF888888.toInt())
            setPadding(4, 8, 4, 8)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 12) }
        }
        mainContainer.addView(tvStatus)

        // ==============================================
        // 📦 ISSUED KEYS CONTAINER
        // ==============================================
        keysContainer = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        mainContainer.addView(keysContainer)

        // ==============================================
        // ✅ SETUP
        // ==============================================
        setupSpinners()
        checkPermission()
        loadIssuedKeys()

        root.addView(mainContainer)
        return root
    }

    private fun createHeader(): View {
        val card = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 28, 24, 28)
            setBackgroundColor(0xFF1E1E2F.toInt())
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 20) }
        }

        val title = TextView(requireContext()).apply {
            text = "🔑 KEY GENERATOR"
            textSize = 22f
            setTextColor(0xFF40E0D0.toInt())
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val subtitle = TextView(requireContext()).apply {
            text = "Bumuo ng access key para sa mga miyembro — OWNER lang ang pwede"
            textSize = 12f
            setTextColor(0xFF888888.toInt())
            setPadding(0, 4, 0, 0)
        }

        card.addView(title)
        card.addView(subtitle)
        return card
    }

    private fun createLabel(text: String): TextView {
        return TextView(requireContext()).apply {
            this.text = text
            textSize = 14f
            setTextColor(0xFFCCCCCC.toInt())
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(4, 0, 0, 8)
        }
    }

    private fun createButton(text: String, color: Int): Button {
        return Button(requireContext()).apply {
            this.text = text
            textSize = 12f
            setBackgroundColor(color)
            setTextColor(0xFFFFFFFF.toInt())
            layoutParams = LinearLayout.LayoutParams(0, 50, 1f).apply { setMargins(4, 0, 4, 0) }
        }
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

        showStatus("✅ 👑 OWNER — Pwede kang bumuo ng key!", true)
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

        val randomPart = UUID.randomUUID().toString().take(8).uppercase()
        val newKey = "$prefix${randomPart}_${System.currentTimeMillis()}"

        val expiresAt = calculateExpiry(expiryPosition)
        val created = System.currentTimeMillis()

        prefsKeys.edit()
            .putString("key_${newKey}_name", name)
            .putString("key_${newKey}_level", level)
            .putLong("key_${newKey}_created", created)
            .putLong("key_${newKey}_expires", expiresAt)
            .putBoolean("key_${newKey}_active", true)
            .apply()

        showKeyDialog(newKey, name, level, expiresAt)

        etName.text.clear()
        loadIssuedKeys()
    }

    private fun calculateExpiry(position: Int): Long {
        val now = System.currentTimeMillis()
        return when (position) {
            0 -> 0
            1 -> now + (60 * 60 * 1000)
            2 -> now + (24 * 60 * 60 * 1000)
            3 -> now + (7 * 24 * 60 * 60 * 1000)
            4 -> now + (30L * 24 * 60 * 60 * 1000)
            5 -> now + (365L * 24 * 60 * 60 * 1000)
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
            tvStatus.setTextColor(0xFF888888.toInt())
            return
        }

        tvStatus.text = "📋 ${allKeys.size} key na naibigay:"
        tvStatus.setTextColor(0xFF4CAF50.toInt())

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

        val keyTv = TextView(requireContext()).apply {
            text = "🔑 $keyId"
            textSize = 10f
            setTextColor(0xFF666666.toInt())
            setPadding(0, 4, 0, 0)
            setTextIsSelectable(true)
        }

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
