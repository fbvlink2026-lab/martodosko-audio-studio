package com.martodosko.studio

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import kotlin.math.roundToInt

class MixerActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mixer)

        try {
            val knob = findViewById<KnobView>(R.id.knob_gain)

            knob.minValue = -50f
            knob.maxValue = 50f
            knob.value = 0f

            knob.onValueChange = {
                // Maaari mong ilagay ang value sa TV kung gusto,
                // pero nasa KnobView na mismo ang "0 dB"
            }

        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}
