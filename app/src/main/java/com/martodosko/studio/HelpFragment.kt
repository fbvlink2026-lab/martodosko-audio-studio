// ==================================================
// FILE: HelpFragment.kt — ✅ WALANG GUHIT! TUNAY NA PANEL! MALINIS NA TEKSTO!
// VERSION: 2.3.0 — ✅ TINANGGAL LAHAT NG ┌┐└┘│─ GUHIT! TUNAY NA ROUNDED PANEL ANG FRAME!
// UPDATED: 2026-09-19 — WALANG TEXT-BOX GUHIT — UI CARD LANG ANG MAKIKITA!
// ==================================================
package com.martodosko.studio

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment

class HelpFragment : Fragment() {

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val versionName = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0).versionName

        // ✅ SCROLL VIEW
        val scrollView = ScrollView(requireContext())
        scrollView.setBackgroundColor(0xFF081218.toInt())

        // ✅ MAIN CONTAINER
        val root = LinearLayout(requireContext())
        root.orientation = LinearLayout.VERTICAL
        root.setPadding(20, 20, 20, 20)

        // ✅ TITLE
        val title = TextView(requireContext())
        title.text = "❓ HELP — MARTODOSKO AUDIO STUDIO"
        title.textSize = 22f
        title.setTextColor(0xFF40E0D0.toInt())
        title.setPadding(0, 0, 0, 24)
        root.addView(title)

        // ==============================================
        // 📌 BAHAGI 1 — MAIN SCREEN
        // ==============================================
        addCollapsibleSection(root, "🏠 BAHAGI 1 — MAIN SCREEN (HOME)", """
Ito ang unang makikita mo pagbukas ng app — dito mo makikita at makokontrol
ang lahat ng iyong aktibong preset.

ANO ANG MAKIKITA MO:
  • Aktibong Presets — Ipinapakita kung ilan at alin ang kasalukuyang nakabukas
  • Pedal — Parang totoong effect pedal — nasa gitna ng screen
  • Pangalan ng Preset — Nasa itaas ng pedal (hal: Clean Voice)
  • Uri ng Preset — VOCAL o GUITAR
  • 3 Knobs — EFFECTS, VOLUME, NOISE GATE
  • ON/OFF Switch — Pindot para i-on o i-off ang epekto
  • Save Preset — I-save ang kasalukuyang settings

3 KNOBS — PAANO GUMAGANA:

  EFFECTS — Pangunahing kontrol ng lahat ng epekto
     • Kapag pinihit → kusang nagbabago ang lahat ng knobs
       sa loob ng Vocal o Guitar Channel
     • Vocal: Reverb, Delay, EQ, Compressor — sabay-sabay!
     • Guitar: Distortion, Chorus, Reverb, Wah — sabay-sabay!

  VOLUME — Kabuuang lakas ng tunog
     • Kapag pinihit → kusang gumagalaw ang Master Volume
       ng kaukulang Channel

  NOISE GATE — Pag-alis ng ugong at ingay
     • Gumagana sa tunog — tinatanggal ang background noise
     • Hindi nagpapakita ng pagbabago sa Channel — hiwalay
       na kontrol pero gumagana pa rin

ON/OFF SWITCH:
  • Naka-ON → Aktibo ang lahat ng epekto — napoproseso ang tunog
  • Naka-OFF → BYPASS — dumaan lang ang orihinal na tunog — WALANG EPEKTO
  • Bawat pedal may sariling switch — hindi apektado ang iba pang pedal

SAVE PRESET:
  1. I-adjust ang 3 knobs ayon sa gusto mo
  2. Pindot ang SAVE PRESET na buton
  3. Ilagay ang pangalan ng preset (hal: Mabigat na Reverb)
  4. Piliin ang uri — VOCAL o GUITAR
  5. Naka-save na! Lalabas agad sa Preset List

TANDAAN: Ang 3 knobs sa harap ay BUOD ng lahat ng kontrol sa loob ng
Channel. Kapag inilapat ang preset — LAHAT ng detalyadong settings ay
naaalala — hindi lang tatlo!
        """.trimIndent())

        // ==============================================
        // 📌 BAHAGI 2 — PRESETS
        // ==============================================
        addCollapsibleSection(root, "📋 BAHAGI 2 — PRESETS", """
Dito mo pipiliin kung aling preset ang gusto mong gamitin.
PWEDE KAHIT ILAN ANG PILIIN — hindi lang isa!

PAANO PUMILI:
  1. Buksan ang Side Menu → Pindot Presets
  2. Makikita mo ang buong listahan — naka-grupo: VOCAL at GUITAR
  3. I-check ang lahat ng gusto mong gamitin — walang limitasyon!
     • Halimbawa: Clean Voice + Hall Reverb + Blues Warm
  4. Makikita sa itaas: Napili: 3 sa 12 preset
  5. Pindot ang APPLY — lahat ng napili ay LALABAS sa Main Screen!
  6. Kung ayaw magbago — pindot CANCEL — walang mangyayari

ANO ANG MANGYAYARI PAGKAPINDOT APPLY:
  • Bawat naka-check na preset → MAY SARILING PEDAL na lalabas sa Main Screen
  • Lahat ng knobs ay kusang mapipihit — ayon sa na-save na halaga
  • Kusang magbabago ang kulay at itsura ng bawat pedal
  • Kung magkaiba ang uri (Vocal + Guitar) → PAGHALUIN ANG TUNOG — sabay lalabas!
        """.trimIndent())

        // ==============================================
        // 📌 BAHAGI 3 — VOCAL MIXER
        // ==============================================
        addCollapsibleSection(root, "🎤 BAHAGI 3 — VOCAL MIXER", """
Dito mo makikita at makokontrol ang DETALYADONG settings ng boses.
Maraming knobs dito — lahat pwedeng i-adjust nang isa-isa!

ANO ANG MAKIKITA MO:
  • Reverb — Mix, Decay, Type — espasyo at lalim ng boses
  • Delay — Time, Repeats, Mix — paulit-ulit na epekto
  • EQ — Low, Mid, High — pagbabalanse ng tono
  • Compressor — Threshold, Ratio — pantay na lakas ng boses
  • Master Volume — kabuuang lakas ng output
  • Noise Gate — Threshold — pag-alis ng ugong

PAANO MAG-SAVE MULA DITO:
  1. I-adjust ang lahat ng knobs ayon sa gusto mo — kahit ilan!
  2. Pindot ang SAVE PRESET
  3. Ilagay ang pangalan — awtomatikong naka-marka bilang VOCAL
  4. LAHAT ng ginalaw na knobs — na-sa-save lahat! Hindi lang tatlo!
  5. Lalabas bilang isang pedal sa Main Screen — may 3 knobs sa harap,
     pero ang LAHAT ng detalyadong settings ay nakatala sa loob!

KONEKSYON SA MAIN SCREEN:
  • Kapag pinihit ang EFFECTS knob sa Vocal Pedal → KUSANG GUMAGALAW
    ang Reverb, Delay, EQ, at Compressor knobs dito — sabay-sabay!
  • Kapag pinihit ang VOLUME → kusang gumagalaw ang Master Volume
  • Kapag pinihit ang NOISE GATE → nagbabago ang halaga — hindi makikita
    dito pero gumagana sa tunog
        """.trimIndent())

        // ==============================================
        // 📌 BAHAGI 4 — GUITAR EFFECTS
        // ==============================================
        addCollapsibleSection(root, "🎸 BAHAGI 4 — GUITAR EFFECTS", """
Dito mo makikita at makokontrol ang DETALYADONG settings ng gitara.
Maraming knobs dito — iba-iba sa Vocal Mixer!

ANO ANG MAKIKITA MO:
  • Overdrive — Gain, Level, Tone — malambot na dagdag na tunog
  • Distortion — Gain, Level, Tone — mabigat at matalas na tunog
  • Fuzz — Amount, Volume — lumabo at malakas na epekto
  • EQ — Low, Mid, High, Presence — pagbabalanse ng tono ng gitara
  • Reverb — Mix, Decay, Type — espasyo at lalim
  • Delay — Time, Repeats, Mix — echo at pag-uulit
  • Chorus — Speed, Depth, Mix — lapad at paggalaw ng tunog
  • Wah Wah — Position, Type — parang taong nagsasalita
  • Flanger / Phaser — kakaibang paggalaw ng tunog
  • Tremolo / Vibrato — pagtaas-pagbaba ng lakas at tono
  • Master Volume — kabuuang lakas ng output
  • Noise Gate — Threshold — pag-alis ng ugong ng amplifier

PAANO MAG-SAVE MULA DITO:
  1. I-adjust ang lahat ng knobs — kahit ilan — kahit lahat!
  2. Pindot ang SAVE PRESET
  3. Ilagay ang pangalan — awtomatikong naka-marka bilang GUITAR
  4. LAHAT ng ginalaw na knobs — na-sa-save lahat!
  5. Lalabas bilang isang pedal sa Main Screen — may 3 knobs sa harap,
     pero ang LAHAT ng detalyadong settings ay nakatala sa loob!

KONEKSYON SA MAIN SCREEN:
  • Kapag pinihit ang EFFECTS knob sa Guitar Pedal → KUSANG GUMAGALAW
    ang Distortion, Chorus, Reverb, Wah, EQ, at iba pa — sabay-sabay!
  • Kapag pinihit ang VOLUME → kusang gumagalaw ang Master Volume
  • Kapag pinihit ang NOISE GATE → nagbabago ang halaga — hindi makikita
    dito pero gumagana sa tunog
        """.trimIndent())

        // ==============================================
        // 📌 BAHAGI 5 — DALAWANG PARAAN NG PAG-SAVE
        // ==============================================
        addCollapsibleSection(root, "💾 BAHAGI 5 — DALAWANG PARAAN NG PAG-SAVE", """
ITO ANG PINAKAMAHALAGA — DALAWANG PARAAN ANG PAG-SAVE!

PARAAN 1 — MAG-SAVE MULA SA CHANNEL SCREEN
  Saan: Vocal Mixer o Guitar Effects
  Ano ang ginagawa:
    • Ginalaw mo ang MARAMING knobs — isa-isa mong inayos
    • Pindot SAVE PRESET → Ilagay ang pangalan
    • LAHAT ng ginalaw — na-sa-save lahat!
    • Lalabas sa Main Screen bilang Pedal — may 3 knobs sa harap
    • Ang LAHAT ng detalyadong settings — nakatala sa loob — hindi nawawala!

PARAAN 2 — MAG-SAVE MULA SA MAIN SCREEN
  Saan: Main Screen — sa mismong Pedal
  Ano ang ginagawa:
    • Pinihit mo ang 3 knobs lang — EFFECTS, VOLUME, NOISE GATE
    • KUSANG GUMAGALAW ang LAHAT ng knobs sa loob ng Channel!
    • Pindot SAVE PRESET → Ilagay ang pangalan
    • LAHAT ng knobs — na-sa-save lahat — dahil nagbago ang lahat!

BUOD:
  • Hindi mahalaga kung saan ka nag-adjust — sa Channel man o sa Pedal —
    LAHAT ng detalye ay naaalala at na-sa-save!
  • Ang 3 knobs sa Pedal ay BUOD — isang pihit = maraming nagbabago!
  • Kapag nag-load ka ng preset — LAHAT ng knobs — sa Channel at sa Pedal —
    babalik sa tamang pwesto — kusang kusang!
        """.trimIndent())

        // ==============================================
        // 📌 BAHAGI 6 — AUDIO ENGINE
        // ==============================================
        addCollapsibleSection(root, "🔊 BAHAGI 6 — AUDIO ENGINE — LOW LATENCY", """
BAKIT NAPAKABILIS AT WALANG ANTALA ANG TUNOG?

  DIREKTANG PAGPAPROSESO — WALANG PAGKAANTALA!
     • Ang signal ay dumadaloy NANG DIREKTA mula input papuntang output
       — hindi dumadaan sa mabagal na proseso ng Android system
     • Kapag kumanta ka o tumugtog — NARIRINIG MO AGAD ANG EPEKTO —
       walang hinihintay — parang totoong pedal!

  C++ NATIVE CODE — PINAKAMABILIS NA PARAAN!
     • Ang buong audio processing ay nakasulat sa C++ — hindi sa Kotlin/Java
     • Mas mabilis ng 10-50 na beses kaysa sa karaniwang Android app
     • Direktang nakikipag-usap sa audio hardware ng telepono
     • Hindi naaantala ng ibang app o system na gawain

  MAS MABABA SA 10ms — HALOS HINDI MARIRINIG ANG ANTALA!
     • Ang kabuuang oras mula pagpasok ng tunog hanggang paglabas:
       — MAS MABABA SA 10 MILLISEGUNDO
     • Ang tao ay nakaririnig ng antala kapag higit sa 30ms — kaya dito,
       HALOS WALANG ANTALA — parang walang digital na pumagitna!

  ZERO-LATENCY MODE — KAPAG KAILANGAN NG PINAKAMABILIS!
     • Sa Settings → Audio → Zero Latency Mode = ON
     • Tinatanggal ang lahat ng dagdag na buffer — pinakamaikling daanan
     • PERPEKTO PARA SA LIVE PERFORMANCE — walang pagkaantala!
     • Tandaan: sa ibang lumang telepono — maaaring maging maingay
       kung masyadong mababa ang buffer — i-adjust sa Settings

PAGKAKAIBA SA IBA PANG APP:

  • Wika ng Pagproseso — Ibang App: Kotlin/Java — Ito: C++ NATIVE
  • Bilis ng Pagproseso — Ibang App: Mabagal — Ito: PINAKAMABILIS
  • Antala — Ibang App: 50-200ms — Ito: < 10ms — HALOS WALA!
  • Daanan ng Signal — Ibang App: Dumadaan sa OS — Ito: DIREKTA SA HARDWARE
  • Angkop sa Live — Ibang App: HINDI — Ito: OO!

MGA TIP PARA SA PINAKAMABILIS NA TUNOG:

  • GUMAMIT NG EARPHONE O EXTERNAL SPEAKER — hindi built-in speaker!
  • I-ON ANG ZERO LATENCY MODE — Settings → Audio → Zero Latency = ON
  • ISARA ANG IBANG APP — bago magbukas ng Martodosko
  • GUMAMIT NG BAGONG TELEPONO — Android 10+
        """.trimIndent())

        // ==============================================
        // 📌 BAHAGI 7 — SETTINGS
        // ==============================================
        addCollapsibleSection(root, "⚙️ BAHAGI 7 — SETTINGS", """
Dito mo mababago ang pangkalahatang pagkilos ng app:
  • Audio Input — Pumili ng Mic, Line-in, o Bluetooth Mic
  • Sample Rate — 44.1kHz / 48kHz — mas mataas = mas malinaw
  • Buffer Size — Maliit = mas mabilis na tugon, Malaki = mas matatag
  • Zero Latency Mode — ON/OFF — pinakamabilis na daanan ng tunog
  • Dark/Light Mode — Pagbabago ng itsura ng app
  • Auto-save — Awtomatikong i-save ang huling ginamit na preset
  • Clear All — Burahin ang lahat ng preset — HINDI NA MABABAWI!
        """.trimIndent())

        // ==============================================
        // 📌 BAHAGI 8 — CHECK UPDATE
        // ==============================================
        addCollapsibleSection(root, "🔄 BAHAGI 8 — CHECK UPDATE", """
  • Awtomatikong tinitingnan kung may bagong bersyon na available
  • Kung meron — lalabas ang mensahe na may opsyon na i-download
  • Kailangan ng internet para gumana
  • Makikita ang kasalukuyang bersyon sa itaas ng Side Menu
        """.trimIndent())

        // ==============================================
        // 📌 BAHAGI 9 — FAQ
        // ==============================================
        addCollapsibleSection(root, "❓ BAHAGI 9 — MGA MADALAS NA TANONG (FAQ)", """
Bakit hindi gumagana ang tunog?
  • Siguraduhing pinayagan ang RECORD_AUDIO permission. Pumunta sa
    Settings → Apps → Martodosko → Permissions → Microphone → Payagan.

Bakit may pagka-antala ang tunog?
  • Pumunta sa Settings → I-ON ang Zero Latency Mode. Bawasan ang Buffer Size
    — mas maliit = mas mabilis. Kung nagka-crash — dagdagan nang kaunti.

Bakit hindi lumalabas ang na-save kong preset?
  • Pumunta sa Presets — hanapin sa listahan — i-check — pindot APPLY.
    Tandaan: naka-grupo ito — hanapin sa ilalim ng VOCAL o GUITAR.

Pwede ba sabay ang Vocal at Guitar?
  • OO! Pumili ng isa sa bawat uri — i-check pareho — pindot APPLY —
    dalawang pedal ang lalabas — dalawang tunog ang maghahalo!

Nawawala ba ang settings kapag isinara ang app?
  • HINDI — lahat ng preset ay nakasave sa telepono. Babalik ang huling
    ginamit na preset pagbukas ng app.

Bakit hindi makita ang pagbabago ng Noise Gate sa Channel?
  • Iyon ang disenyo — gumagana ito sa tunog pero hiwalay ang kontrol.
    Hindi ipinapakita ang halaga sa Channel pero gumagana pa rin.
        """.trimIndent())

        // ==============================================
        // 📌 BAHAGI 10 — TIPS
        // ==============================================
        addCollapsibleSection(root, "💡 MGA TIP PARA SA MAGANDANG RESULTA", """
  PARA SA BOSES:
    • Simulan sa Reverb = 30-50% — hindi masyadong malaki
    • EQ — bawasan ang Low kung masyadong malalim, dagdagan ang High para malinaw
    • Compressor — 2:1 hanggang 4:1 — pantay ang lakas ng malakas at mahinang parte
    • Noise Gate — i-on kung may ugong — itaas hanggang mawala ang ingay

  PARA SA GITARA:
    • Distortion — simulan sa mababa — dagdagan kung kailangan
    • Wah Wah — i-adjust ang posisyon habang tumutugtog
    • Reverb — mas maliit kung mabilis ang tugtog — mas malaki kung mabagal
    • Delay — 1/4 o 1/8 ng tempo — mas maganda ang pag-uulit

  PANGKALAHATAN:
    • Gumamit ng magandang earphone o speaker — mas malinaw ang pagkakaiba
    • I-off ang ibang app habang gumagamit — mas malakas ang pagproseso
    • Subukan ang iba't ibang kumbinasyon — walang mali sa eksperimento!
        """.trimIndent())

        // ==============================================
        // 📌 BAHAGI 11 — KAILANGAN NG TULONG
        // ==============================================
        addCollapsibleSection(root, "📞 KAILANGAN NG TULONG?", """
  • Join Us — Sumali sa aming komunidad — tanungin ang iba pang user!
  • About — Tingnan ang bersyon at impormasyon ng developer
  • I-report ang problema — Settings → Report Issue — ilarawan ang nangyari

                  SALAMAT SA PAGGAMIT NG MARTODOSKO!
           Ang musika — para sa lahat, kahit saan.
                  Version $versionName — 2026-09-19
        """.trimIndent())

        scrollView.addView(root)
        return scrollView
    }

    // ==============================================
    // ✅ TUNAY NA ROUNDED PANEL — WALANG GUHIT NA TEXT!
    // ==============================================
    private fun addCollapsibleSection(
        root: LinearLayout,
        title: String,
        content: String
    ) {
        val context = root.context

        // ✅ PANEL CONTAINER — TUNAY NA ROUNDED BACKGROUND! WALANG TEXT GUHIT!
        val panel = LinearLayout(context)
        panel.orientation = LinearLayout.VERTICAL

        // ✅ ROUNDED PANEL BACKGROUND — GINAGAWA NG CODE, HINDI TEXT!
        val panelBg = GradientDrawable()
        panelBg.setColor(0xFF14252F.toInt())
        panelBg.cornerRadius = 24f // ✅ ROUNDED KANTO
        panelBg.setStroke(2, 0xFF23404F.toInt()) // ✅ BORDER
        panel.background = panelBg

        // ✅ HEADER — PINDUTIN PARA BUKAS/TIKLOP
        val header = TextView(context)
        header.text = "▼  $title"
        header.textSize = 16f
        header.setTextColor(0xFF40E0D0.toInt())
        header.setPadding(20, 18, 20, 18)
        header.setBackgroundColor(0xFF1A2F3A.toInt())

        // ✅ CONTENT AREA — NAKATIKLOP NANG UNA
        val contentView = TextView(context)
        contentView.text = content
        contentView.setTextColor(0xFFD0D0D0.toInt())
        contentView.textSize = 13f
        contentView.setLineSpacing(6f, 1f)
        contentView.setPadding(20, 16, 20, 20)
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

        panel.addView(header)
        panel.addView(contentView)

        val panelLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        panelLayoutParams.setMargins(0, 0, 0, 16)
        panel.layoutParams = panelLayoutParams

        root.addView(panel)
    }
}
