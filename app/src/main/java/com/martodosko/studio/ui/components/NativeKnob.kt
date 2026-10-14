// ==================================================
// FILE: NativeKnob.kt
// VERSION: v2.3.0-final-bridge
// PURPOSE: Wraps the working Legacy KnobView into Compose
// NOTE: Uses AndroidView to ensure pixel-perfect behavior.
// ==================================================
package com.martodosko.studio.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.martodosko.studio.KnobView // Import your existing legacy class

@Composable
fun NativeKnob(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    unit: String = "%",
    minValue: Float = 0f,
    maxValue: Float = 100f
) {
    AndroidView(
        factory = { context ->
            KnobView(context).apply {
                labelText = label
                unitText = unit
                this.minValue = minValue
                this.maxValue = maxValue
                this.value = value
                
                // Connect listener
                this.onValueChange = { newVal ->
                    onValueChange(newVal)
                }
            }
        },
        update = { knobView ->
            // Sync properties if they change externally
            if (knobView.labelText != label) knobView.labelText = label
            if (knobView.unitText != unit) knobView.unitText = unit
            if (knobView.minValue != minValue) knobView.minValue = minValue
            if (knobView.maxValue != maxValue) knobView.maxValue = maxValue
            
            // Update value only if significantly different to avoid redraw loops
            if (Math.abs(knobView.value - value) > 0.01f) {
                knobView.value = value
            }
        },
        modifier = modifier
    )
}
