// ==================================================
// FILE: NativeKnob.kt
// VERSION: v2.3.0-final-volume-behavior-match
// PURPOSE: Wraps Legacy KnobView with EXACT Same Persistence Behavior as Volume Knob
// NOTE: Removed premature value assignment in factory. Relies on loadSavedValue().
//       Only syncs from Compose if the loaded value differs significantly.
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
                // 1. Configure Identity & Limits FIRST
                labelText = label
                unitText = unit
                this.minValue = minValue
                this.maxValue = maxValue
                
                // 2. Set Unique Key IMMEDIATELY so save/load targets correct slot
                if (uniqueKey.isNotEmpty()) {
                    preferenceKey = uniqueKey
                } else {
                    preferenceKey = label.ifEmpty { null } 
                }

                // 3. Attach Listener EARLY
                this.onValueChange = { newVal ->
                    onValueChange(newVal)
                }

                // 4. LOAD SAVED STATE FROM DISK
                // This sets 'field' internally and calls invalidate().
                // It does NOT trigger onValueChange unless we force it, but usually
                // we want the UI to reflect what was saved.
                loadSavedValue()

                // 5. SYNC WITH COMPOSE STATE (The Critical Fix)
                // We compare the LOADED value against the INCOMING Compose value.
                // If they are different, we assume Compose has the "truth" (e.g., from a preset load)
                // OR we simply ensure consistency. 
                // BUT, to mimic Volume's stability, we prioritize the Saved Value initially.
                // However, if Compose passes a specific initial value that hasn't been saved yet,
                // we apply it. To prevent overwriting valid saves, we only update if difference is large.
                
                val savedVal = this.value
                if (Math.abs(savedVal - value) > 0.1f) {
                    // Force update if Compose state is drastically different (e.g. app restart vs fresh launch)
                    // Note: Setting .value triggers saveValue() again, which is fine for syncing.
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
            
            // External State Change Sync (e.g., Loading a Preset)
            // Only update if the incoming Compose value is different from current View value
            if (Math.abs(knobView.value - value) > 0.1f) {
                knobView.value = value
            }
        },
        modifier = modifier
    )
}
