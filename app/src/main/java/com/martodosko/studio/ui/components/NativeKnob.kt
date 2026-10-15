// ==================================================
// FILE: NativeKnob.kt
// VERSION: v2.3.0-volume-mirror-fix
// PURPOSE: Mirrors Volume Knob Behavior Exactly for All Knobs
// NOTE: Ensures loadSavedValue() runs before any external sync to preserve history.
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
    uniqueKey: String = "" 
) {
    AndroidView(
        factory = { context ->
            KnobView(context).apply {
                // 1. Configure Identity First
                labelText = label
                unitText = unit
                this.minValue = minValue
                this.maxValue = maxValue
                
                // 2. Set Unique Key for Persistence
                if (uniqueKey.isNotEmpty()) {
                    preferenceKey = uniqueKey
                } else {
                    preferenceKey = label.ifEmpty { null } 
                }

                // 3. LOAD SAVED VALUE IMMEDIATELY
                // This mimics the 'init' block but ensures we have the key set first.
                loadSavedValue()

                // 4. Connect Listener AFTER Loading
                // This prevents the initial load from triggering an unnecessary save loop
                this.onValueChange = { newVal ->
                    onValueChange(newVal)
                }
                
                // 5. Initial Sync with Compose State
                // Only update if the loaded value differs significantly from what Compose expects
                // Usually, loadSavedValue sets the correct historical value.
                if (Math.abs(this.value - value) > 0.01f) {
                     this.value = value
                }
            }
        },
        update = { knobView ->
            // Runtime Updates
            if (knobView.labelText != label) knobView.labelText = label
            if (knobView.unitText != unit) knobView.unitText = unit
            if (knobView.minValue != minValue) knobView.minValue = minValue
            if (knobView.maxValue != maxValue) knobView.maxValue = maxValue
            
            // Sync Value from Compose (e.g., Preset Change)
            // We check difference to avoid infinite recomposition loops
            if (Math.abs(knobView.value - value) > 0.01f) {
                knobView.value = value
            }
        },
        modifier = modifier
    )
}
