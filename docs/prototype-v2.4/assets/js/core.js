// ==================================================
// FILE: core.js
// VERSION: v2.4.0-FIX-RACE-CONDITION
// PURPOSE: Centralized Reactive State Management (AudioEngine)
// NOTE: Ensures instance is created immediately upon script load.
// ==================================================

class AudioEngine {
    constructor() {
        console.log("🏗️ [CORE] Constructing AudioEngine...");
        
        // --- INITIAL STATE DEFINITION ---
        this.state = {
            activeTab: 'board', 
            userRole: 'guest',  // 'guest' or 'member'
            
            // VOCAL CHANNEL PARAMETERS
            vocal: {
                power: true,
                fxIntensity: 50,   // Maps to Board FX Knob & Mixer Gain
                noiseGate: 20,     // Maps to Board NG Knob (Independent)
                volume: 80         // Maps to Board Vol Knob & Mixer Fader
            },

            // GUITAR CHANNEL PARAMETERS
            guitar: {
                power: true,
                driveAmount: 65,   // Maps to Board FX Knob & Mixer Gain
                noiseGate: 10,     // Maps to Board NG Knob
                level: 75          // Maps to Board Vol Knob & Mixer Fader
            },

            // PRESET LIBRARY STORAGE
            presets: [] 
        };

        this.listeners = [];
        
        // Initialize persistence on load
        this.loadSession();
        console.log("✅ [CORE] AudioEngine Instance Ready.");
    }

    /**
     * SUBSCRIBE: Allows UI components to listen for state changes.
     */
    subscribe(callback) {
        if (typeof callback === 'function') {
            this.listeners.push(callback);
        }
    }

    /**
     * NOTIFY: Triggers all listeners and saves current session.
     */
    notify() {
        this.saveSession(); 
        this.listeners.forEach(listener => listener(this.state));
    }

    /**
     * UPDATE PARAMETER: Generic setter for any channel parameter.
     */
    updateParam(channel, param, value) {
        // SAFETY CHECK: Ensure channel exists before updating
        if (!this.state[channel]) {
            console.error(`❌ [CORE ERROR] Channel '${channel}' does not exist in state!`);
            return;
        }
        
        if (typeof this.state[channel][param] !== 'undefined') {
            if (typeof this.state[channel][param] === 'number') {
                this.state[channel][param] = parseFloat(value);
            } else {
                this.state[channel][param] = value;
            }
            
            console.log(`🔄 [CORE] Updated: ${channel}.${param} -> ${value}`);
            this.notify();
        }
    }

    /**
     * TOGGLE POWER
     */
    togglePower(channel) {
        if (!this.state[channel]) return;
        this.state[channel].power = !this.state[channel].power;
        console.log(`⚡ [CORE] Power Toggle: ${channel} -> ${this.state[channel].power ? 'ON' : 'OFF'}`);
        this.notify();
    }

    /**
     * SAVE CURRENT STATE AS PRESET
     */
    saveCurrentAsPreset(name) {
        const timestamp = Date.now();
        const newPreset = {
            id: `preset_${timestamp}`,
            name: name || `Custom Preset ${this.state.presets.length + 1}`,
            createdAt: new Date().toISOString(),
            author: 'Local User',
            visibility: this.state.userRole === 'member' ? 'MEMBERS_ONLY' : 'PUBLIC_FREE',
            config: {
                vocal: { ...this.state.vocal },
                guitar: { ...this.state.guitar }
            }
        };
        
        this.state.presets.unshift(newPreset);
        this.savePresetsToDisk();
        this.notify();
        alert(`💾 Saved: "${newPreset.name}"\nVisible as: ${newPreset.visibility}`);
    }

    /**
     * LOAD PRESET INTO ACTIVE STATE
     */
    loadPreset(presetId) {
        const preset = this.state.presets.find(p => p.id === presetId);
        if (!preset) {
            console.warn("❌ [CORE WARN] Preset not found:", presetId);
            return;
        }

        this.state.vocal = { ...preset.config.vocal };
        this.state.guitar = { ...preset.config.guitar };
        
        console.log(`📂 [CORE] Loaded Preset: ${preset.name}`);
        this.notify();
        alert(`✨ Applied: "${preset.name}"\nBoard & Mixer synced.`);
    }

    /**
     * DELETE PRESET
     */
    deletePreset(presetId) {
        const index = this.state.presets.findIndex(p => p.id === presetId);
        if (index > -1) {
            this.state.presets.splice(index, 1);
            this.savePresetsToDisk();
            this.notify();
        }
    }

    /**
     * SWITCH USER ROLE
     */
    toggleUserRole() {
        this.state.userRole = this.state.userRole === 'guest' ? 'member' : 'guest';
        console.log(`👤 [CORE] Role Changed: ${this.state.userRole.toUpperCase()}`);
        this.notify();
    }

    // --- PERSISTENCE HELPERS ---

    saveSession() {
        try {
            const sessionData = {
                vocal: this.state.vocal,
                guitar: this.state.guitar,
                userRole: this.state.userRole
            };
            localStorage.setItem('martodosko_session', JSON.stringify(sessionData));
        } catch (e) {
            console.error("[CORE] Failed to save session:", e);
        }
    }

    loadSession() {
        try {
            const saved = localStorage.getItem('martodosko_session');
            if (saved) {
                const data = JSON.parse(saved);
                // Merge carefully to avoid losing structure if keys are missing
                if(data.vocal) this.state.vocal = { ...this.state.vocal, ...data.vocal };
                if(data.guitar) this.state.guitar = { ...this.state.guitar, ...data.guitar };
                if(data.userRole) this.state.userRole = data.userRole;
            }
            
            const savedPresets = localStorage.getItem('martodosko_presets');
            if (savedPresets) {
                this.state.presets = JSON.parse(savedPresets);
            }
        } catch (e) {
            console.error("[CORE] Failed to load session:", e);
        }
    }

    savePresetsToDisk() {
        try {
            localStorage.setItem('martodosko_presets', JSON.stringify(this.state.presets));
        } catch (e) {
            console.error("[CORE] Failed to save presets:", e);
        }
    }
}

// EXPORT SINGLETON IMMEDIATELY
window.AudioEngine = new AudioEngine();
console.log("🚀 [BOOTSTRAP] Window.AudioEngine assigned.");
