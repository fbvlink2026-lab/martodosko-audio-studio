// ==================================================
// FILE: app.js
// VERSION: v2.4.0-proto-app-boot-fixed
// PURPOSE: Application Bootstrap, Settings, and Mixer Tabs
// NOTE: Removed duplicate navigation logic. Fixed channel tab switching.
// ==================================================

document.addEventListener('DOMContentLoaded', () => {
    console.log("🏁 App Bootstrapping Complete.");

    // --- SETTINGS: TOGGLE USER ROLE (For Testing Access Control) ---
    const btnToggleRole = document.getElementById('btn-toggle-role');
    if (btnToggleRole) {
        btnToggleRole.addEventListener('click', () => {
            AudioEngine.toggleUserRole();
            
            // Update button text based on new role
            const currentRole = AudioEngine.state.userRole;
            if (currentRole === 'member') {
                btnToggleRole.textContent = "SWITCH TO GUEST MODE (TEST)";
                btnToggleRole.classList.remove('secondary');
                btnToggleRole.classList.add('primary');
            } else {
                btnToggleRole.textContent = "SWITCH TO MEMBER MODE (TEST)";
                btnToggleRole.classList.remove('primary');
                btnToggleRole.classList.add('secondary');
            }
        });
    }

    // --- MIXER TAB SWITCHING LOGIC (Visual Only for Prototype) ---
    const channelTabs = document.querySelectorAll('.channel-tab');
    
    channelTabs.forEach(tab => {
        tab.addEventListener('click', () => {
            // Remove active class from ALL channel tabs
            channelTabs.forEach(t => t.classList.remove('active'));
            
            // Add active class to CLICKED tab
            tab.classList.add('active');
            
            // Get the target channel name (e.g., 'vocal' or 'guitar')
            const targetChannel = tab.getAttribute('data-channel');
            
            // Find the corresponding mixer strip and scroll to it
            // We use a more robust way than :contains()
            const strips = document.querySelectorAll('.mixer-strip h3');
            strips.forEach(h3 => {
                if (h3.innerText.toLowerCase().includes(targetChannel)) {
                    const parentStrip = h3.closest('.mixer-strip');
                    if (parentStrip) {
                        parentStrip.scrollIntoView({ behavior: 'smooth', block: 'start' });
                        
                        // Highlight effect
                        parentStrip.style.borderColor = '#40E0D0';
                        setTimeout(() => {
                            parentStrip.style.borderColor = ''; // Reset border color after highlight
                        }, 500);
                    }
                }
            });
        });
    });

    // --- FINAL SANITY CHECK ---
    setTimeout(() => {
        if (window.AudioEngine && window.RotaryKnob) {
            console.log("🎉 MARTODOSKO PROTOTYPE v2.4 IS LIVE!");
            console.log("   - State Management: Active");
            console.log("   - Bidirectional Sync: Enabled");
            console.log("   - Persistence: LocalStorage");
            console.log("   - Navigation: Functional");
        } else {
            console.error("❌ Critical Components Missing. Check Console for errors.");
        }
    }, 500);
});
