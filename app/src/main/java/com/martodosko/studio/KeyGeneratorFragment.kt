package com.martodosko.studio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import kotlin.random.Random

class KeyGeneratorFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = LinearLayout(requireContext())
        root.orientation = LinearLayout.VERTICAL
        root.setPadding(32, 48, 32, 32)

        val title = TextView(requireContext())
        title.text = "🔑 KEY GENERATOR"
        title.textSize = 22f
        title.setTextColor(0xFFFFD700.toInt())
        title.setPadding(0, 0, 0, 24)
        root.addView(title)

        val btnGenerate = Button(requireContext())
        btnGenerate.text = "➕ GUMAWA NG BAGONG KEY CODE"
        btnGenerate.setBackgroundColor(0xFF2E7D32.toInt())
        btnGenerate.setTextColor(0xFFFFFFFF.toInt())
        btnGenerate.setOnClickListener {
            val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
            val newKey = StringBuilder()
            repeat(4) { blockIndex ->
                repeat(4) { newKey.append(chars[Random.nextInt(chars.length)]) }
                if (blockIndex < 3) newKey.append("-")
            }
            Toast.makeText(requireContext(), "✅ Nalikha: $newKey", Toast.LENGTH_LONG).show()
        }
        root.addView(btnGenerate)

        return root
    }
}
