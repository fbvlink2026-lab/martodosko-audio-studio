// ==================================================
// FILE: NativeKnob.kt
// VERSION: v2.3.0-state-persistence-fix-v2
// PURPOSE: Wraps Legacy KnobView with Correct State Loading Priority
// NOTE: Prioritizes Saved Preferences over Compose Initial Value to fix Gain Memory Issue.
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
    uniqueKey: String = "" // ✅ NEW PARAMETER FOR STATE PERSISTENCE
) {
    AndroidView(
        factory = { context ->
            KnobView(context).apply {
                labelText = label
                unitText = unit
                this.minValue = minValue
                this.maxValue = maxValue
                
                // ✅ SET UNIQUE KEY FIRST SO LOADSAVEDVALUE CAN FIND IT
                if (uniqueKey.isNotEmpty()) {
                    preferenceKey = uniqueKey
                } else {
                    preferenceKey = label.ifEmpty { null } 
                }

                // ✅ CRITICAL FIX: Do NOT set 'this.value = value' here immediately.
                // The 'init' block of KnobView already called loadSavedValue().
                // If we overwrite it now with the Compose 'value' param, we lose the memory.
                // Instead, let the KnobView initialize itself from Preferences.
                
                // Connect listener AFTER initialization so it doesn't trigger during setup
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
            // AND ensure we don't reset a loaded value back to default 0 unnecessarily
            if (Math.abs(knobView.value - value) > 0.01f) {
                knobView.value = value
            }
        },
        modifier = modifier
    )
}
