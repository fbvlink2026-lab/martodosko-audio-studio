// ==================================================
// FILE: ui-controller.js
// VERSION: v2.4.0-proto-controller
// PURPOSE: Initializes UI Components and Establishes Bidirectional Sync
// NOTE: Subscribes to AudioEngine state changes to update Knobs/Sliders.
// ==================================================

document.addEventListener('DOMContentLoaded', () => {
    console.log("🚀 Initializing UI Controller...");

    // --- 1. REGISTER KNOB INSTANCES ---
    // We store references so we can update them later without re-querying DOM
    
    // BOARD KNOBS
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
    }, '#FF9800'); // Orange for Guitar

    const boardGuitarNg = new RotaryKnob('knob-board-guitar-ng', 0, 100, AudioEngine.state.guitar.noiseGate, (val) => {
        AudioEngine.updateParam('guitar', 'noiseGate', val);
    }, '#FF9800');

    const boardGuitarLevel = new RotaryKnob('knob-board-guitar-level', 0, 100, AudioEngine.state.guitar.level, (val) => {
        AudioEngine.updateParam('guitar', 'level', val);
    }, '#FF9800');

    // MIXER CONTROLS (Using standard inputs for simplicity in prototype, 
    // but styled to match theme via CSS)
    const mixerVocalGainInput = document.getElementById('input-mixer-vocal-gain');
    const mixerVocalVolSlider = document.getElementById('slider-mixer-vocal-vol');
    
    const mixerGuitarDriveInput = document.getElementById('input-mixer-guitar-drive');
    const mixerGuitarLevelSlider = document.getElementById('slider-mixer-guitar-level');

    // --- 2. SETUP EVENT LISTENERS FOR MIXER INPUTS ---
    
    if (mixerVocalGainInput) {
        mixerVocalGainInput.addEventListener('input', (e) => {
            AudioEngine.updateParam('vocal', 'fxIntensity', e.target.value);
        });
    }
    if (mixerVocalVolSlider) {
        mixerVocalVolSlider.addEventListener('input', (e) => {
            AudioEngine.updateParam('vocal', 'volume', e.target.value);
        });
    }
    if (mixerGuitarDriveInput) {
        mixerGuitarDriveInput.addEventListener('input', (e) => {
            AudioEngine.updateParam('guitar', 'driveAmount', e.target.value);
        });
    }
    if (mixerGuitarLevelSlider) {
        mixerGuitarLevelSlider.addEventListener('input', (e) => {
            AudioEngine.updateParam('guitar', 'level', e.target.value);
        });
    }

    // --- 3. POWER SWITCHES LOGIC ---
    const switchVocal = document.getElementById('switch-power-vocal');
    const switchGuitar = document.getElementById('switch-power-guitar');

    if (switchVocal) {
        switchVocal.onclick = () => AudioEngine.togglePower('vocal');
    }
    if (switchGuitar) {
        switchGuitar.onclick = () => AudioEngine.togglePower('guitar');
    }

    // --- 4. NAVIGATION LOGIC ---
    const navItems = document.querySelectorAll('.nav-item');
    const screens = document.querySelectorAll('.screen-section');

    navItems.forEach(item => {
        item.addEventListener('click', () => {
            // Remove active class from all
            navItems.forEach(n => n.classList.remove('active'));
            screens.forEach(s => s.classList.add('hidden'));
            screens.forEach(s => s.classList.remove('visible'));

            // Add active to clicked
            item.classList.add('active');
            const targetId = item.getAttribute('data-target');
            const targetScreen = document.getElementById(targetId);
            if (targetScreen) {
                targetScreen.classList.remove('hidden');
                targetScreen.classList.add('visible');
            }
        });
    });

    // --- 5. SUBSCRIBE TO STATE CHANGES (THE SYNC ENGINE) ---
    // Whenever AudioEngine.notify() is called, this function runs.
    // It updates ALL UI elements to reflect the new state.
    
    AudioEngine.subscribe((state) => {
        // Update Board Knobs Visually
        boardVocalFx.setValue(state.vocal.fxIntensity, false);
        boardVocalNg.setValue(state.vocal.noiseGate, false);
        boardVocalVol.setValue(state.vocal.volume, false);
        
        boardGuitarDrive.setValue(state.guitar.driveAmount, false);
        boardGuitarNg.setValue(state.guitar.noiseGate, false);
        boardGuitarLevel.setValue(state.guitar.level, false);

        // Update Mixer Inputs/Sliders Visually
        if (mixerVocalGainInput) mixerVocalGainInput.value = state.vocal.fxIntensity;
        if (mixerVocalVolSlider) mixerVocalVolSlider.value = state.vocal.volume;
        
        if (mixerGuitarDriveInput) mixerGuitarDriveInput.value = state.guitar.driveAmount;
        if (mixerGuitarLevelSlider) mixerGuitarLevelSlider.value = state.guitar.level;

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
        
        // Re-render Library List if needed (handled in ui-library.js)
    });

    // --- 6. INITIAL RENDER ---
    // Force one update to ensure UI matches loaded state
    AudioEngine.notify(); 
    
    console.log("✅ UI Controller Ready. Sync Active.");
});
