// ==================================================
// FILE: NativeKnob.kt
// VERSION: v2.3.0-stateless-bridge
// PURPOSE: Pure Bridge for Legacy KnobView driven by External State
// NOTE: Removed internal load/save logic reliance for synced knobs. 
//       It now strictly follows the 'value' passed from Compose.
// ==================================================
package com.martodosko.studio.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.martodosko.studio.KnobView 

@Composable
fun NativeKnob(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    unit: String = "%",
    minValue: Float = 0f,
    maxValue: Float = 100f,
    uniqueKey: String = "" // Kept for optional standalone use, but ignored if syncing globally
) {
    AndroidView(
        factory = { context ->
            KnobView(context).apply {
                labelText = label
                unitText = unit
                this.minValue = minValue
                this.maxValue = maxValue
                
                // ✅ DISABLE LOCAL PERSISTENCE FOR SYNCED KNOBS
                // We set preferenceKey to null or empty so it doesn't auto-load old values
                // that might conflict with the incoming 'value' parameter.
                preferenceKey = null 

                this.onValueChange = { newVal ->
                    onValueChange(newVal)
                }

                // Set initial value directly from Compose State
                this.value = value
            }
        },
        update = { knobView ->
            // Update Config
            if (knobView.labelText != label) knobView.labelText = label
            if (knobView.unitText != unit) knobView.unitText = unit
            if (knobView.minValue != minValue) knobView.minValue = minValue
            if (knobView.maxValue != maxValue) knobView.maxValue = maxValue
            
            // ✅ STRICT SYNC: Always follow the external state
            // This ensures Board and Mixer always show the same number
            if (Math.abs(knobView.value - value) > 0.01f) {
                knobView.value = value
            }
        },
        modifier = modifier
    )
}
