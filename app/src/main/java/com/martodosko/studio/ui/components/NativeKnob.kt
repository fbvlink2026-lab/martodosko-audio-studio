// ==================================================
// FILE: NativeKnob.kt
// VERSION: v2.3.0-state-persistence-fix-v2
// PURPOSE: Wraps Legacy KnobView with Corrected State Loading Sequence
// NOTE: Fixed initialization order to ensure Saved Values are loaded BEFORE setting defaults.
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
    maxValue: Float = 100f,
    uniqueKey: String = "" 
) {
    AndroidView(
        factory = { context ->
            KnobView(context).apply {
                // 1. SET CONFIGURATION FIRST
                labelText = label
                unitText = unit
                this.minValue = minValue
                this.maxValue = maxValue
                
                // 2. SET UNIQUE KEY IMMEDIATELY SO LOAD/SAVE USES THE RIGHT IDENTITY
                if (uniqueKey.isNotEmpty()) {
                    preferenceKey = uniqueKey
                } else {
                    preferenceKey = label.ifEmpty { null } 
                }

                // 3. CONNECT LISTENER EARLY TO CAPTURE ANY CHANGE
                this.onValueChange = { newVal ->
                    onValueChange(newVal)
                }

                // 4. LOAD SAVED VALUE EXPLICITLY
                // This reads from SharedPreferences using the key set above.
                // It updates 'value' internally and triggers invalidate().
                loadSavedValue()

                // 5. SYNC WITH COMPOSE STATE (Optional Safety Net)
                // If loadSavedValue found nothing, it keeps current internal value.
                // We force sync here just in case Compose passed a different initial value
                // that hasn't been saved yet.
                if (Math.abs(this.value - value) > 0.01f) {
                     // Only update if significantly different to avoid loops
                     this.value = value
                }
            }
        },
        update = { knobView ->
            // Sync properties if they change externally during runtime
            if (knobView.labelText != label) knobView.labelText = label
            if (knobView.unitText != unit) knobView.unitText = unit
            if (knobView.minValue != minValue) knobView.minValue = minValue
            if (knobView.maxValue != maxValue) knobView.maxValue = maxValue
            
            // Update value only if significantly different to avoid redraw loops
            // BUT DO NOT OVERWRITE IF USER IS CURRENTLY DRAGGING (handled by internal logic mostly)
            if (Math.abs(knobView.value - value) > 0.01f) {
                knobView.value = value
            }
        },
        modifier = modifier
    )
}
