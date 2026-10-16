// ==================================================
// FILE: ui-library.js
// VERSION: v2.4.0-proto-library-ui
// PURPOSE: Manages the Preset Library Interface
// NOTE: Connects DOM elements to AudioEngine preset methods.
// ==================================================

document.addEventListener('DOMContentLoaded', () => {
    console.log("📚 Initializing Library UI...");

    const listContainer = document.getElementById('preset-list-container');
    const btnSaveCurrent = document.getElementById('btn-save-current');
    const searchInput = document.getElementById('preset-search');

    // --- RENDER PRESETS LIST ---
    function renderPresets(filterText = '') {
        if (!listContainer) return;
        
        listContainer.innerHTML = ''; // Clear current list
        
        let presets = StorageManager.getAllPresets();
        
        // Apply Search Filter
        if (filterText.trim() !== '') {
            const lowerFilter = filterText.toLowerCase();
            presets = presets.filter(p => 
                p.name.toLowerCase().includes(lowerFilter) || 
                p.category?.toLowerCase().includes(lowerFilter)
            );
        }

        if (presets.length === 0) {
            listContainer.innerHTML = '<p class="text-muted" style="text-align:center; padding:20px;">No presets found.</p>';
            return;
        }

        presets.forEach(preset => {
            const itemDiv = document.createElement('div');
            itemDiv.className = 'preset-item';
            
            // Visibility Badge Logic
            const isMemberOnly = preset.visibility === 'MEMBERS_ONLY';
            const userRole = AudioEngine.state.userRole;
            const canAccess = !isMemberOnly || userRole === 'member';
            const lockIcon = isMemberOnly && !canAccess ? ' 🔒' : '';
            const actionClass = canAccess ? '' : 'disabled';

            itemDiv.innerHTML = `
                <div class="preset-info">
                    <h4>${preset.name}${lockIcon}</h4>
                    <span class="preset-meta">${preset.category} • ${new Date(preset.createdAt).toLocaleDateString()}</span>
                </div>
                <button class="btn-load ${actionClass}" data-id="${preset.id}" ${!canAccess ? 'disabled' : ''}>
                    LOAD
                </button>
            `;

            // Attach Load Event
            const loadBtn = itemDiv.querySelector('.btn-load');
            if (loadBtn && canAccess) {
                loadBtn.addEventListener('click', (e) => {
                    e.stopPropagation();
                    AudioEngine.loadPreset(preset.id);
                });
            } else if (loadBtn && !canAccess) {
                 loadBtn.innerText = "LOCKED";
                 loadBtn.style.opacity = "0.5";
            }

            listContainer.appendChild(itemDiv);
        });
    }

    // --- SAVE CURRENT STATE BUTTON ---
    if (btnSaveCurrent) {
        btnSaveCurrent.addEventListener('click', () => {
            const name = prompt("Enter Preset Name:", "My Custom Sound");
            if (name && name.trim() !== '') {
                AudioEngine.saveCurrentAsPreset(name.trim());
                renderPresets(searchInput ? searchInput.value : ''); // Refresh list
            }
        });
    }

    // --- SEARCH FILTER ---
    if (searchInput) {
        searchInput.addEventListener('input', (e) => {
            renderPresets(e.target.value);
        });
    }

    // --- INITIAL RENDER ---
    renderPresets();

    // --- SUBSCRIBE TO ROLE CHANGES (To update Lock Icons) ---
    AudioEngine.subscribe(() => {
        // Re-render list whenever state changes (including role toggle)
        // This ensures locked/unlocked status updates immediately
        if(listContainer) {
             renderPresets(searchInput ? searchInput.value : '');
        }
    });

    console.log("✅ Library UI Ready.");
});
