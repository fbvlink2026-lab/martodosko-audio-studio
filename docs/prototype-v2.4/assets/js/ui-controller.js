// ==================================================
// FILE: ui-controller.js
// VERSION: v2.4.0-DEBUG-CONTROLLER
// PURPOSE: Initializes UI Components with Verbose Logging
// NOTE: Logs every step of the initialization process.
// ==================================================

document.addEventListener('DOMContentLoaded', () => {
    console.log("🚀 [UI CTRL] DOM Content Loaded.");

    // DELAY INITIALIZATION
    setTimeout(() => {
        console.log("⏳ [UI CTRL] Timeout fired. Starting Initialization...");
        initializeKnobsAndSync();
    }, 100);
});

function initializeKnobsAndSync() {
    try {
        console.log("✅ [UI CTRL] Entering initializeKnobsAndSync()");

        // --- 1. REGISTER BOARD KNOBS ---
        console.log("--- Creating Board Knobs ---");
        
        const boardVocalFx = new RotaryKnob('knob-board-vocal-fx', 0, 100, AudioEngine.state.vocal.fxIntensity, (val) => {
            AudioEngine.updateParam('vocal', 'fxIntensity', val);
        }, '#40E0D0');

        const boardVocalNg = new RotaryKnob('knob-board-vocal-ng', 0, 100, AudioEngine.state.vocal.noiseGate, (val) => {
            AudioEngine.updateParam('vocal', 'noiseGate', val);
        }, '#40E0D0');

        const boardVocalVol = new RotaryKnob('knob-board-vocal-vol', 0, 100, AudioEngine.state.vocal.volume, (val) => {
            AudioEngine.updateParam('vocal', 'volume', val);
        }, '#40E0D0');

        const boardGuitarDrive = new RotaryKnob('knob-board-guitar-drive', 0, 100, AudioEngine.state.guitar.driveAmount, (val) => {
            AudioEngine.updateParam('guitar', 'driveAmount', val);
        }, '#FF9800'); 

        const boardGuitarNg = new RotaryKnob('knob-board-guitar-ng', 0, 100, AudioEngine.state.guitar.noiseGate, (val) => {
            AudioEngine.updateParam('guitar', 'noiseGate', val);
        }, '#FF9800');

        const boardGuitarLevel = new RotaryKnob('knob-board-guitar-level', 0, 100, AudioEngine.state.guitar.level, (val) => {
            AudioEngine.updateParam('guitar', 'level', val);
        }, '#FF9800');


        // --- 2. REGISTER MIXER KNOBS ---
        console.log("--- Creating Mixer Knobs ---");

        const mixerVocalGain = new RotaryKnob('knob-mixer-vocal-gain', 0, 100, AudioEngine.state.vocal.fxIntensity, (val) => {
            AudioEngine.updateParam('vocal', 'fxIntensity', val);
        }, '#40E0D0');

        const mixerVocalVol = new RotaryKnob('knob-mixer-vocal-vol', 0, 100, AudioEngine.state.vocal.volume, (val) => {
            AudioEngine.updateParam('vocal', 'volume', val);
        }, '#40E0D0');

        const mixerGuitarDrive = new RotaryKnob('knob-mixer-guitar-drive', 0, 100, AudioEngine.state.guitar.driveAmount, (val) => {
            AudioEngine.updateParam('guitar', 'driveAmount', val);
        }, '#FF9800');

        const mixerGuitarLevel = new RotaryKnob('knob-mixer-guitar-level', 0, 100, AudioEngine.state.guitar.level, (val) => {
            AudioEngine.updateParam('guitar', 'level', val);
        }, '#FF9800');


        // --- 3. POWER SWITCHES LOGIC ---
        console.log("--- Binding Power Switches ---");
        const switchVocal = document.getElementById('switch-power-vocal');
        const switchGuitar = document.getElementById('switch-power-guitar');

        if (switchVocal) {
            switchVocal.onclick = () => AudioEngine.togglePower('vocal');
        } else {
            console.warn("⚠️ [WARN] Switch Vocal not found!");
        }
        if (switchGuitar) {
            switchGuitar.onclick = () => AudioEngine.togglePower('guitar');
        } else {
            console.warn("⚠️ [WARN] Switch Guitar not found!");
        }


        // --- 4. SUBSCRIBE TO STATE CHANGES ---
        console.log("--- Subscribing to State Changes ---");
        
        AudioEngine.subscribe((state) => {
            // Update Board Visuals
            boardVocalFx.setValue(state.vocal.fxIntensity, false); 
            boardVocalNg.setValue(state.vocal.noiseGate, false);
            boardVocalVol.setValue(state.vocal.volume, false);
            
            boardGuitarDrive.setValue(state.guitar.driveAmount, false);
            boardGuitarNg.setValue(state.guitar.noiseGate, false);
            boardGuitarLevel.setValue(state.guitar.level, false);

            // Update Mixer Visuals
            mixerVocalGain.setValue(state.vocal.fxIntensity, false);
            mixerVocalVol.setValue(state.vocal.volume, false);
            
            mixerGuitarDrive.setValue(state.guitar.driveAmount, false);
            mixerGuitarLevel.setValue(state.guitar.level, false);

            // Update Text Displays
            const dispVGain = document.getElementById('display-mixer-vocal-gain');
            const dispVVol = document.getElementById('display-mixer-vocal-vol');
            const dispGGain = document.getElementById('display-mixer-guitar-drive');
            const dispGVol = document.getElementById('display-mixer-guitar-level');

            if(dispVGain) dispVGain.textContent = Math.round(state.vocal.fxIntensity) + '%';
            if(dispVVol) dispVVol.textContent = Math.round(state.vocal.volume) + '%';
            if(dispGGain) dispGGain.textContent = Math.round(state.guitar.driveAmount) + '%';
            if(dispGVol) dispGVol.textContent = Math.round(state.guitar.level) + '%';

            // Update Power Switches Visuals
            if (switchVocal) {
                if (state.vocal.power) switchVocal.classList.add('on');
                else switchVocal.classList.remove('on');
            }
            if (switchGuitar) {
                if (state.guitar.power) switchGuitar.classList.add('on');
                else switchGuitar.classList.remove('on');
            }

            // Update Role Badge
            const badge = document.getElementById('user-role-badge');
            if (badge) {
                badge.textContent = state.userRole.toUpperCase() + " MODE";
                badge.className = `badge ${state.userRole}`;
            }
        });

        // --- 5. INITIAL RENDER ---
        console.log("🔥 [UI CTRL] Forcing Initial Notify...");
        AudioEngine.notify(); 
        
        console.log("✅ [UI CTRL] Initialization Complete.");

    } catch (error) {
        console.error("💥 [CRITICAL ERROR] Exception during initialization:", error);
        alert("Error initializing knobs. Check Console (F12). Details:\n" + error.message);
    }
}
