// ==================================================
// FILE: storage.js
// VERSION: v2.4.0-proto-storage
// PURPOSE: Data Seeding and Abstraction Layer
// NOTE: Currently uses LocalStorage via Core, but structured for Room/Supabase swap later.
// ==================================================

const StorageManager = {
    
    /**
     * SEED DEFAULT PRESETS
     * Call this once if no presets exist in LocalStorage.
     */
    seedDefaultPresets() {
        const existing = localStorage.getItem('martodosko_presets');
        if (existing && JSON.parse(existing).length > 0) {
            return; // Already seeded
        }

        const defaults = [
            {
                id: 'preset_clean_vocal',
                name: 'Studio Clean Vocals',
                category: 'VOCAL',
                visibility: 'PUBLIC_FREE',
                createdAt: new Date().toISOString(),
                author: 'Martodosko Team',
                config: {
                    vocal: { power: true, fxIntensity: 30, noiseGate: 15, volume: 85 },
                    guitar: { power: false, driveAmount: 0, noiseGate: 0, level: 0 }
                }
            },
            {
                id: 'preset_heavy_guitar',
                name: 'Heavy Metal Lead',
                category: 'GUITAR',
                visibility: 'MEMBERS_ONLY',
                createdAt: new Date().toISOString(),
                author: 'ProUser99',
                config: {
                    vocal: { power: false, fxIntensity: 0, noiseGate: 0, volume: 0 },
                    guitar: { power: true, driveAmount: 95, noiseGate: 40, level: 70 }
                }
            },
            {
                id: 'preset_balanced_mix',
                name: 'Balanced Live Mix',
                category: 'MASTER',
                visibility: 'PUBLIC_FREE',
                createdAt: new Date().toISOString(),
                author: 'SoundEng',
                config: {
                    vocal: { power: true, fxIntensity: 50, noiseGate: 20, volume: 80 },
                    guitar: { power: true, driveAmount: 60, noiseGate: 10, level: 75 }
                }
            }
        ];

        localStorage.setItem('martodosko_presets', JSON.stringify(defaults));
        console.log("🌱 Seeded default presets.");
    },

    /**
     * GET ALL PRESETS
     * Returns array of preset objects sorted by date.
     */
    getAllPresets() {
        const raw = localStorage.getItem('martodosko_presets');
        if (!raw) return [];
        const presets = JSON.parse(raw);
        // Sort newest first
        return presets.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
    }
};

// Run seeding immediately on script load
StorageManager.seedDefaultPresets();

// Export to global scope
window.StorageManager = StorageManager;
