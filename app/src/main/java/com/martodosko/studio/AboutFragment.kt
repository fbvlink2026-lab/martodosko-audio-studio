// ==================================================
// FILE: AboutFragment.kt — ✅ KUMPLETONG ABOUT + AUTO VERSION DETECT!
// VERSION: 1.0.0 — AUTO KUHA NG VERSION! MAY COLLAPSE/EXPAND! TUGMA SA ESTILO!
// UPDATED: 2026-09-17 — WALANG MANUAL NA VERSION! KUSANG KUKUHA!
// ==================================================
package com.martodosko.studio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment

class AboutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // ✅ AUTO-DETECT NG VERSION — HINDI NA KAILANGANG I-MANUAL!
        val versionName = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0).versionName

        // ✅ SCROLL VIEW
        val scrollView = ScrollView(requireContext())
        scrollView.setBackgroundColor(0xFF081218.toInt())

        // ✅ MAIN CONTAINER
        val root = LinearLayout(requireContext())
        root.orientation = LinearLayout.VERTICAL
        root.setPadding(32, 32, 32, 32)

        // ✅ TITLE
        val title = TextView(requireContext())
        title.text = "ℹ️ TUNGKOL — MARTODOSKO AUDIO STUDIO"
        title.textSize = 22f
        title.setTextColor(0xFF40E0D0.toInt())
        title.setPadding(0, 0, 0, 24)
        root.addView(title)

        // ==============================================
        // 📌 BAHAGI 1 — IMPORMASYON NG APP
        // ==============================================
        addCollapsibleSection(
            root = root,
            title = "📱 IMPORMASYON NG APLIKASYON",
            content = """
  📌 PANGALAN:      Martodosko Audio Studio
  🔢 BERSYON:      v$versionName
  📅 PETSA:        2026-09-17
  👤 DEVELOPER:    Martodosko Studio
  📄 LISENSYA:     All Rights Reserved
  🌐 WEBSITE:      https://github.com/martodosko/martodosko-audio-studio

  📲 MINIMUM OS:   Android 10 (API 29) o mas bago
  🎯 LAYUNIN:      Propesyonal na Audio Processing — Direkta sa Telepono
            """.trimIndent()
        )

        // ==============================================
        // 📌 BAHAGI 2 — TUNGKOL SA APP
        // ==============================================
        addCollapsibleSection(
            root = root,
            title = "📖 TUNGKOL SA MARTODOSKO AUDIO STUDIO",
            content = """
  Martodosko Audio Studio ay isang kumpletong mobile audio workstation na
  dinisenyo para sa mga musikero, mang-aawit, at audio enthusiasts.

  ✅ KUMPLETONG MIXER — Vocal + Guitar Channels
  ✅ EFFECTS PROCESSING — Reverb, Delay, EQ, Compressor, Distortion, Chorus, Wah, at marami pa!
  ✅ LOW LATENCY AUDIO — <10ms — halos walang antala!
  ✅ C++ NATIVE ENGINE — pinakamabilis na pagproseso ng tunog
  ✅ PRESET SYSTEM — I-save at i-load ang iyong paboritong settings
  ✅ PEDAL INTERFACE — parang totoong hardware — madaling gamitin!

  "Ang musika — para sa lahat, kahit saan."
            """.trimIndent()
        )

        // ==============================================
        // 📌 BAHAGI 3 — AUDIO ENGINE
        // ==============================================
        addCollapsibleSection(
            root = root,
            title = "⚡ AUDIO ENGINE — TEKNOLOHIYA",
            content = """
  🔊 DIREKTANG SIGNAL PATH — WALANG PAGKAANTALA
     • Ang audio signal ay dumadaloy direkta mula input → C++ Engine → output
     • Hindi dumadaan sa mabagal na Java/Kotlin — mas mabilis ng 10-50x

  ⚙️ C++ NATIVE CODE — PINAKAMABILIS NA PARAAN
     • Low-level audio processing — direktang nakikipag-usap sa hardware
     • Optimisado para sa bawat telepono — pinakamababang latency

  📊 MGA SPECIFICATION:
     • Latency:        < 10ms (Zero Latency Mode ON)
     • Sample Rate:    44.1kHz / 48kHz
     • Bit Depth:      16-bit / 24-bit
     • Audio Format:   PCM 16-bit / 32-bit float
     • Processing:     Real-time — walang buffer delay
            """.trimIndent()
        )

        // ==============================================
        // 📌 BAHAGI 4 — MGA TAMBAYAN
        // ==============================================
        addCollapsibleSection(
            root = root,
            title = "🌐 MGA TAMBAYAN AT SUPORTA",
            content = """
  💬 DISKUSYON AT TULONG
     • GitHub:      https://github.com/martodosko/martodosko-audio-studio
     • Issues:      I-report ang problema sa GitHub → Issues
     • Updates:     Tingnan ang "Check Update" sa Side Menu
     • Community:   Pindot ang "Join Us" sa Side Menu — sumali sa usapan!

  📧 KONTAKTA KAMI
     • Para sa mungkahi, ulat ng bug, o pakikipagtulungan
     • GitHub Discussions — bukas para sa lahat!

  ⭐ KUNG NAGUSTUHAN MO ANG APP
     • Bigyan ng star sa GitHub — malaking tulong ito!
     • Ibahagi sa ibang musikero — para sa lahat ang musika!
            """.trimIndent()
        )

        // ==============================================
        // 📌 BAHAGI 5 — PAGPAPASALAMAT
        // ==============================================
        addCollapsibleSection(
            root = root,
            title = "🙏 PAGPAPASALAMAT",
            content = """
  🎵 SA LAHAT NG MUSIKERO — Salamat sa pagtitiwala!
     Ang app na ito ay ginawa para sa inyo — para makagawa ng musika
     kahit saan, kahit anong oras, gamit lang ang iyong telepono.

  💡 SA LAHAT NG NAGBIGAY NG MUNGKAHI — Salamat!
     Ang bawat suhestiyon ay pinag-isipan at isinasaalang-alang.

  👨‍💻 MGA KONTRIBUSYON
     • Martodosko Studio — Konsepto, Disenyo, at Pagbuo
     • Open Source Libraries — Salamat sa komunidad ng developer!

  ═══════════════════════════════════════════════════════
                ✅ MARTODOSKO AUDIO STUDIO — v$versionName
                  "Para sa musika, para sa lahat."
                         © 2026 Martodosko Studio
  ═══════════════════════════════════════════════════════
            """.trimIndent()
        )

        scrollView.addView(root)
        return scrollView
    }

    // ==============================================
    // ✅ PAMAMARAAN — GUMAGAWA NG NABABASA PINDUTIN NA SECTION!
    // ==============================================
    private fun addCollapsibleSection(
        root: LinearLayout,
        title: String,
        content: String
    ) {
        val context = root.context

        // ✅ HEADER — PINDUTIN PARA BUKAS/TIKLOP
        val header = TextView(context)
        header.text = "▼  $title"
        header.textSize = 16f
        header.setTextColor(0xFF40E0D0.toInt())
        header.setPadding(0, 20, 0, 12)

        // ✅ CONTENT — NAKATIKLOP NANG UNA
        val contentView = TextView(context)
        contentView.text = content
        contentView.setTextColor(0xFFE0E0E0.toInt())
        contentView.textSize = 12f
        contentView.setLineSpacing(4f, 1f)
        contentView.setPadding(16, 0, 0, 16)
        contentView.visibility = View.GONE

        // ✅ PINDUTIN → BUKAS O TIKLOP!
        header.setOnClickListener {
            if (contentView.visibility == View.GONE) {
                contentView.visibility = View.VISIBLE
                header.text = "▲  $title"
            } else {
                contentView.visibility = View.GONE
                header.text = "▼  $title"
            }
        }

        root.addView(header)
        root.addView(contentView)
    }
}
