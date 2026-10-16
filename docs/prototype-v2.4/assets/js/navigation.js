// ==================================================
// FILE: navigation.js
// VERSION: v2.4.0-proto-nav-fix
// PURPOSE: Handles Bottom Tab Switching and Screen Visibility
// NOTE: Ensures clean separation of concerns for UI routing.
// ==================================================

document.addEventListener('DOMContentLoaded', () => {
    console.log("🧭 Initializing Navigation...");

    const navItems = document.querySelectorAll('.nav-item');
    const screens = document.querySelectorAll('.screen-section');

    // Function to switch active screen
    function showScreen(targetId) {
        // 1. Hide all screens
        screens.forEach(screen => {
            screen.classList.add('hidden');
            screen.classList.remove('visible');
        });

        // 2. Show target screen
        const targetScreen = document.getElementById(targetId);
        if (targetScreen) {
            targetScreen.classList.remove('hidden');
            targetScreen.classList.add('visible');
        }

        // 3. Update Nav Item Active State
        navItems.forEach(item => {
            item.classList.remove('active');
            if (item.getAttribute('data-target') === targetId) {
                item.classList.add('active');
            }
        });
        
        // Optional: Scroll to top when switching screens
        window.scrollTo(0, 0);
    }

    // Attach Click Events to Nav Buttons
    navItems.forEach(item => {
        item.addEventListener('click', (e) => {
            e.preventDefault(); // Prevent default anchor behavior if any
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
