package com.martodosko.studio
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment

class PresetModerationFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = LinearLayout(requireContext())
        root.orientation = LinearLayout.VERTICAL
        root.setPadding(32, 48, 32, 32)
        val title = TextView(requireContext())
        title.text = "📋 PRESET MODERATION"
        title.textSize = 22f
        title.setTextColor(0xFF40E0D0.toInt())
        title.setPadding(0,0,0,24)
        root.addView(title)
        root.addView(TextView(requireContext()).apply { text = "Listahan ng mga preset na naghihintay ng aprobasyon..." })
        return root
    }
}
