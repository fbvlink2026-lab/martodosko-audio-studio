// ==================================================
// FILE: knob.js
// VERSION: v2.4.0-proto-knob-widget
// PURPOSE: High-Fidelity Rotary Control for Web Prototype
// NOTE: Mimics KnobView.kt visuals (Panel, Gradient Body, Neon Arc, Pointer).
//       Handles Mouse/Touch events for value adjustment.
// ==================================================

class RotaryKnob {
    /**
     * @param {string} elementId - ID of the canvas element.
     * @param {number} min - Minimum value.
     * @param {number} max - Maximum value.
     * @param {number} initialVal - Starting value.
     * @param {function} onChangeCallback - Function called when value changes.
     * @param {string} accentColor - Hex color for glow/pointer (default Cyan).
     */
    constructor(elementId, min, max, initialVal, onChangeCallback, accentColor = '#40E0D0') {
        this.canvas = document.getElementById(elementId);
        if (!this.canvas) {
            console.error(`Canvas not found: ${elementId}`);
            return;
        }

        this.ctx = this.canvas.getContext('2d');
        this.min = min;
        this.max = max;
        this.value = initialVal;
        this.onChange = onChangeCallback;
        this.accentColor = accentColor;
        
        // Interaction State
        this.isDragging = false;
        this.lastY = 0;
        this.sensitivity = 0.5; // Pixels per unit change

        // Bind Events
        this.bindEvents();
        
        // Initial Draw
        this.draw();
    }

    bindEvents() {
        // Mouse Events
        this.canvas.addEventListener('mousedown', (e) => this.handleStart(e.clientY));
        window.addEventListener('mousemove', (e) => this.handleMove(e.clientY));
        window.addEventListener('mouseup', () => this.handleEnd());

        // Touch Events (Mobile Support)
        this.canvas.addEventListener('touchstart', (e) => {
            e.preventDefault(); // Prevent scroll
            this.handleStart(e.touches[0].clientY);
        }, { passive: false });
        
        window.addEventListener('touchmove', (e) => {
            if (this.isDragging) {
                e.preventDefault();
                this.handleMove(e.touches[0].clientY);
            }
        }, { passive: false });
        
        window.addEventListener('touchend', () => this.handleEnd());
    }

    handleStart(y) {
        this.isDragging = true;
        this.lastY = y;
        this.canvas.style.cursor = 'grabbing';
    }

    handleMove(y) {
        if (!this.isDragging) return;

        const deltaY = this.lastY - y; // Up is positive movement
        
        // Calculate new value based on delta and sensitivity
        let newValue = this.value + (deltaY * this.sensitivity);
        
        // Clamp to range
        newValue = Math.max(this.min, Math.min(this.max, newValue));

        // Only update if changed significantly to avoid jitter
        if (Math.abs(newValue - this.value) > 0.1) {
            this.setValue(newValue, false); // False = don't trigger callback yet (manual set)
            this.draw();
            this.onChange(newValue); // Trigger Global State Update
        }

        this.lastY = y;
    }

    handleEnd() {
        this.isDragging = false;
        this.canvas.style.cursor = 'grab';
    }

    /**
     * Public method to set value externally (e.g., from Mixer sync or Preset Load)
     * @param {number} val 
     * @param {boolean} triggerCallback - Whether to call onChange (usually false for external sync)
     */
    setValue(val, triggerCallback = true) {
        this.value = Math.max(this.min, Math.min(this.max, val));
        this.draw();
        if (triggerCallback && this.onChange) {
            this.onChange(this.value);
        }
    }

    draw() {
        const w = this.canvas.width;
        const h = this.canvas.height;
        const cx = w / 2;
        const cy = h / 2;
        const radius = Math.min(w, h) * 0.35; // Knob body size
        const arcRadius = radius + 8; // Outer ring for ticks/arc

        // Clear Canvas
        this.ctx.clearRect(0, 0, w, h);

        // 1. DRAW BACKGROUND PANEL (Simulated by dark circle behind knob)
        this.ctx.fillStyle = '#12232E';
        this.ctx.beginPath();
        this.ctx.arc(cx, cy, radius + 15, 0, Math.PI * 2);
        this.ctx.fill();

        // 2. CALCULATE ANGLES
        // Range: 135 deg (Bottom Left) to 45 deg (Bottom Right) -> Total 270 deg sweep
        const startAngle = Math.PI * 0.75; 
        const endAngle = Math.PI * 2.25;   
        const totalSweep = endAngle - startAngle;
        
        const percent = (this.value - this.min) / (this.max - this.min);
        const currentAngle = startAngle + (percent * totalSweep);

        // 3. DRAW NEON ARC (Active Indicator)
        this.ctx.strokeStyle = this.accentColor;
        this.ctx.lineWidth = 4;
        this.ctx.lineCap = 'round';
        this.ctx.shadowBlur = 10;
        this.ctx.shadowColor = this.accentColor;
        
        this.ctx.beginPath();
        this.ctx.arc(cx, cy, arcRadius, startAngle, currentAngle);
        this.ctx.stroke();
        
        // Reset Shadow for other elements
        this.ctx.shadowBlur = 0;

        // 4. DRAW TICK MARKS (Optional Visual Guide)
        const tickCount = 11; // 0%, 10%, ... 100%
        for (let i = 0; i <= tickCount; i++) {
            const tPercent = i / tickCount;
            const tAngle = startAngle + (tPercent * totalSweep);
            
            const innerR = arcRadius + 2;
            const outerR = arcRadius + 6;
            
            const x1 = cx + Math.cos(tAngle) * innerR;
            const y1 = cy + Math.sin(tAngle) * innerR;
            const x2 = cx + Math.cos(tAngle) * outerR;
            const y2 = cy + Math.sin(tAngle) * outerR;

            this.ctx.beginPath();
            this.ctx.moveTo(x1, y1);
            this.ctx.lineTo(x2, y2);
            this.ctx.strokeStyle = '#3A4A54'; // Dim grey
            this.ctx.lineWidth = 2;
            this.ctx.stroke();
        }

        // 5. DRAW KNOB BODY (Radial Gradient)
        const gradient = this.ctx.createRadialGradient(cx - 5, cy - 5, 5, cx, cy, radius);
        gradient.addColorStop(0, '#2A5F7A'); // Highlight
        gradient.addColorStop(1, '#081218'); // Dark Base
        
        this.ctx.fillStyle = gradient;
        this.ctx.beginPath();
        this.ctx.arc(cx, cy, radius, 0, Math.PI * 2);
        this.ctx.fill();
        
        // Border
        this.ctx.strokeStyle = '#000';
        this.ctx.lineWidth = 2;
        this.ctx.stroke();

        // 6. DRAW POINTER LINE
        this.ctx.save();
        this.ctx.translate(cx, cy);
        this.ctx.rotate(currentAngle);
        
        this.ctx.strokeStyle = '#FFFFFF';
        this.ctx.lineWidth = 3;
        this.ctx.lineCap = 'round';
        
        this.ctx.beginPath();
        this.ctx.moveTo(0, -radius * 0.3); // Start slightly inside center
        this.ctx.lineTo(0, -radius * 0.9); // End near edge
        this.ctx.stroke();
        
        this.ctx.restore();
        
        // 7. DRAW VALUE TEXT (Centered below knob if space allows, or overlay)
        // For cleaner look, we rely on external labels in HTML/CSS usually, 
        // but here's a small indicator:
        this.ctx.fillStyle = this.accentColor;
        this.ctx.font = 'bold 10px sans-serif';
        this.ctx.textAlign = 'center';
        this.ctx.fillText(Math.round(this.value), cx, cy + radius + 20);
    }
}

// Export to global scope
window.RotaryKnob = RotaryKnob;
