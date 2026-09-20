package com.martodosko.studio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment

class AdminHomeFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = LinearLayout(requireContext())
        root.orientation = LinearLayout.VERTICAL
        root.setPadding(32, 48, 32, 32)

        val title = TextView(requireContext())
        title.text = "🎛️ ADMIN PANEL"
        title.textSize = 24f
        title.setTextColor(0xFFFFD700.toInt())
        title.setPadding(0, 0, 0, 24)
        root.addView(title)

        val desc = TextView(requireContext())
        desc.text = "Pumili ng kategorya sa kanang menu:\n\n" +
                    "🔑 Key Generator — Gumawa ng bagong Key Code\n" +
                    "👤 User Management — Pamahalaan ang mga miyembro\n" +
                    "📋 Preset Moderation — Aprubahan/Tanggihan ang mga preset\n" +
                    "📊 Estatistika — Tingnan ang buong bilang ng data"
        desc.textSize = 15f
        desc.setTextColor(0xFFCCCCCC.toInt())
        root.addView(desc)

        return root
    }
}
