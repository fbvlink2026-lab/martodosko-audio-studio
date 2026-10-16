// ==================================================
// FILE: app.js
// VERSION: v2.4.0-proto-app-boot
// PURPOSE: Application Bootstrap & Global Utilities
// NOTE: Handles Settings interactions and final initialization checks.
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
    // In a full app, this might hide/show specific strips. 
    // Here we just toggle active classes for demonstration.
    const channelTabs = document.querySelectorAll('.channel-tab');
    channelTabs.forEach(tab => {
        tab.addEventListener('click', () => {
            // Remove active from all tabs
            channelTabs.forEach(t => t.classList.remove('active'));
            // Add active to clicked
            tab.classList.add('active');
            
            // Optional: Scroll to relevant strip if needed
            const targetChannel = tab.getAttribute('data-channel');
            const strip = document.querySelector(`.mixer-strip h3:contains('${targetChannel}')`)?.closest('.mixer-strip');
            if(strip) strip.scrollIntoView({ behavior: 'smooth' });
        });
    });

    // --- FINAL SANITY CHECK ---
    setTimeout(() => {
        if (window.AudioEngine && window.RotaryKnob) {
            console.log("🎉 MARTODOSKO PROTOTYPE v2.4 IS LIVE!");
            console.log("   - State Management: Active");
            console.log("   - Bidirectional Sync: Enabled");
            console.log("   - Persistence: LocalStorage");
        } else {
            console.error("❌ Critical Components Missing. Check Console for errors.");
        }
    }, 500);
});
