// ==================================================
// FILE: navigation.js
// VERSION: v2.4.0-proto-nav-redraw-trigger
// PURPOSE: Handles Bottom Tab Switching and Forces UI Refresh
// NOTE: Dispatches 'tabChanged' event to allow Knobs to redraw themselves.
// ==================================================

document.addEventListener('DOMContentLoaded', () => {
    console.log("🧭 Initializing Navigation...");

    const navItems = document.querySelectorAll('.nav-item');
    const screens = document.querySelectorAll('.screen-section');

    // Function to switch active screen
    function showScreen(targetId) {
        // 1. Hide all screens (using classes defined in CSS)
        screens.forEach(screen => {
            if (screen.id === targetId) {
                screen.classList.remove('hidden');
                screen.classList.add('visible');
            } else {
                screen.classList.remove('visible');
                screen.classList.add('hidden');
            }
        });

        // 2. Update Nav Item Active State
        navItems.forEach(item => {
            item.classList.remove('active');
            if (item.getAttribute('data-target') === targetId) {
                item.classList.add('active');
            }
        });
        
        // 3. FORCE REDRAW EVENT
        // This tells the Knob widget to re-calculate its canvas size and draw again
        window.dispatchEvent(new CustomEvent('tabChanged', { detail: { screenId: targetId } }));
        
        console.log(`Switched to: ${targetId}`);
    }

    // Attach Click Events to Nav Buttons
    navItems.forEach(item => {
        item.addEventListener('click', (e) => {
            e.preventDefault();
            const targetId = item.getAttribute('data-target');
            if (targetId) {
                showScreen(targetId);
            }
        });
    });

    // Initialize with Board as default
    showScreen('screen-board');

    console.log("✅ Navigation Ready.");
});
