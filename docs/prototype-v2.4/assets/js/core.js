// ==================================================
// FILE: core.js
// VERSION: v2.4.0-proto-core
// PURPOSE: Centralized Reactive State Management (AudioEngine)
// NOTE: Implements Pub/Sub pattern for Bidirectional Sync between Board & Mixer.
// ==================================================

class AudioEngine {
    constructor() {
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
        console.log("✅ AudioEngine Initialized.");
    }

    /**
     * SUBSCRIBE: Allows UI components to listen for state changes.
     * @param {Function} callback - Function to execute when state updates.
     */
    subscribe(callback) {
        if (typeof callback === 'function') {
            this.listeners.push(callback);
        }
    }

    /**
     * NOTIFY: Triggers all listeners and saves current session.
     * This is the heart of the Bidirectional Sync.
     */
    notify() {
        this.saveSession(); // Auto-persist to LocalStorage
        this.listeners.forEach(listener => listener(this.state));
    }

    /**
     * UPDATE PARAMETER: Generic setter for any channel parameter.
     * @param {string} channel - 'vocal' or 'guitar'
     * @param {string} param - Property name (e.g., 'fxIntensity', 'volume')
     * @param {number|string|boolean} value - New value
     */
    updateParam(channel, param, value) {
        if (this.state[channel] && typeof this.state[channel][param] !== 'undefined') {
            // Ensure numeric values are floats
            if (typeof this.state[channel][param] === 'number') {
                this.state[channel][param] = parseFloat(value);
            } else {
                this.state[channel][param] = value;
            }
            
            console.log(`🔄 Updated: ${channel}.${param} -> ${value}`);
            this.notify();
        }
    }

    /**
     * TOGGLE POWER: Specifically handles the On/Off switch logic.
     * @param {string} channel - 'vocal' or 'guitar'
     */
    togglePower(channel) {
        if (this.state[channel]) {
            this.state[channel].power = !this.state[channel].power;
            console.log(`⚡ Power Toggle: ${channel} -> ${this.state[channel].power ? 'ON' : 'OFF'}`);
            this.notify();
        }
    }

    /**
     * SAVE CURRENT STATE AS PRESET
     * Captures the entire vocal/guitar config into a named object.
     * @param {string} name - User-defined preset name.
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
        
        // Add to top of list
        this.state.presets.unshift(newPreset);
        
        // Persist presets separately from session
        this.savePresetsToDisk();
        
        // Notify UI to refresh Library list
        this.notify();
        
        alert(`💾 Saved: "${newPreset.name}"\nVisible as: ${newPreset.visibility}`);
    }

    /**
     * LOAD PRESET INTO ACTIVE STATE
     * Overwrites current Board/Mixer values with saved preset data.
     * @param {string} presetId - The ID of the preset to load.
     */
    loadPreset(presetId) {
        const preset = this.state.presets.find(p => p.id === presetId);
        if (!preset) {
            console.warn("❌ Preset not found:", presetId);
            return;
        }

        // Deep copy config to state
        this.state.vocal = { ...preset.config.vocal };
        this.state.guitar = { ...preset.config.guitar };
        
        console.log(`📂 Loaded Preset: ${preset.name}`);
        
        // Trigger full UI refresh
        this.notify();
        
        alert(`✨ Applied: "${preset.name}"\nBoard & Mixer synced.`);
    }

    /**
     * DELETE PRESET
     * @param {string} presetId 
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
     * SWITCH USER ROLE (For Testing Access Control)
     */
    toggleUserRole() {
        this.state.userRole = this.state.userRole === 'guest' ? 'member' : 'guest';
        console.log(`👤 Role Changed: ${this.state.userRole.toUpperCase()}`);
        this.notify();
    }

    // --- PERSISTENCE HELPERS (LocalStorage) ---

    saveSession() {
        try {
            const sessionData = {
                vocal: this.state.vocal,
                guitar: this.state.guitar,
                userRole: this.state.userRole
            };
            localStorage.setItem('martodosko_session', JSON.stringify(sessionData));
        } catch (e) {
            console.error("Failed to save session:", e);
        }
    }

    loadSession() {
        try {
            const saved = localStorage.getItem('martodosko_session');
            if (saved) {
                const data = JSON.parse(saved);
                this.state.vocal = data.vocal || this.state.vocal;
                this.state.guitar = data.guitar || this.state.guitar;
                this.state.userRole = data.userRole || 'guest';
            }
            
            const savedPresets = localStorage.getItem('martodosko_presets');
            if (savedPresets) {
                this.state.presets = JSON.parse(savedPresets);
            }
        } catch (e) {
            console.error("Failed to load session:", e);
        }
    }

    savePresetsToDisk() {
        try {
            localStorage.setItem('martodosko_presets', JSON.stringify(this.state.presets));
        } catch (e) {
            console.error("Failed to save presets:", e);
        }
    }
}

// Export Singleton Instance to Global Scope
window.AudioEngine = new AudioEngine();
