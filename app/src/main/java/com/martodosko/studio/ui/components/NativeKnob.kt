// ==================================================
// FILE: NativeKnob.kt
// VERSION: v2.3.0-final-sync-guard
// PURPOSE: Wraps Legacy KnobView with Robust State Sync
// NOTE: Prevents overwriting local user changes during rapid recompositions.
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
                labelText = label
                unitText = unit
                this.minValue = minValue
                this.maxValue = maxValue
                
                if (uniqueKey.isNotEmpty()) {
                    preferenceKey = uniqueKey
                } else {
                    preferenceKey = label.ifEmpty { null } 
                }

                this.onValueChange = { newVal ->
                    onValueChange(newVal)
                }

                // LOAD FROM LOCAL STORAGE FIRST
                loadSavedValue()
                
                // ONLY SYNC IF COMPOSE VALUE IS SIGNIFICANTLY DIFFERENT THAN LOADED ONE
                // This prevents resetting a user-adjusted knob back to 0 just because 
                // the ViewModel hasn't caught up yet or returned a stale default.
                if (Math.abs(this.value - value) > 0.5f) {
                     this.value = value
                }
            }
        },
        update = { knobView ->
            // Update Config
            if (knobView.labelText != label) knobView.labelText = label
            if (knobView.unitText != unit) knobView.unitText = unit
            if (knobView.minValue != minValue) knobView.minValue = minValue
            if (knobView.maxValue != maxValue) knobView.maxValue = maxValue
            
            // CRITICAL SYNC LOGIC:
            // Only push Compose value to View if they differ significantly.
            // This handles cases where external events (like Preset Load) change the state.
            // It avoids fighting with the user's finger drag if there's slight latency.
            if (Math.abs(knobView.value - value) > 0.5f) {
                knobView.value = value
            }
        },
        modifier = modifier
    )
}
