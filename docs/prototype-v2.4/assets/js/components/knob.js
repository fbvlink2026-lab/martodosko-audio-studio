// ==================================================
// FILE: knob.js
// VERSION: v2.4.0-proto-knob-high-contrast
// PURPOSE: Rotary Control with Enhanced Visibility & Auto-Redraw
// NOTE: Increased brightness of knob body and added distinct borders to separate from panel.
// ==================================================

class RotaryKnob {
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
        
        this.isDragging = false;
        this.lastY = 0;
        this.sensitivity = 0.5;

        // Bind Interaction Events
        this.bindEvents();
        
        // Listen for Tab Changes to Force Redraw
        window.addEventListener('tabChanged', () => {
            setTimeout(() => this.draw(), 50);
        });

        // Initial Draw
        this.draw();
    }

    bindEvents() {
        // Mouse
        this.canvas.addEventListener('mousedown', (e) => this.handleStart(e.clientY));
        window.addEventListener('mousemove', (e) => this.handleMove(e.clientY));
        window.addEventListener('mouseup', () => this.handleEnd());

        // Touch
        this.canvas.addEventListener('touchstart', (e) => {
            e.preventDefault();
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

        const deltaY = this.lastY - y;
        let newValue = this.value + (deltaY * this.sensitivity);
        newValue = Math.max(this.min, Math.min(this.max, newValue));

        if (Math.abs(newValue - this.value) > 0.1) {
            this.setValue(newValue, false);
            this.draw();
            this.onChange(newValue);
        }

        this.lastY = y;
    }

    handleEnd() {
        this.isDragging = false;
        this.canvas.style.cursor = 'grab';
    }

    setValue(val, triggerCallback = true) {
        this.value = Math.max(this.min, Math.min(this.max, val));
        this.draw();
        if (triggerCallback && this.onChange) {
            this.onChange(this.value);
        }
    }

    draw() {
        // Safety check for canvas dimensions
        if (this.canvas.width === 0 || this.canvas.height === 0) {
             const rect = this.canvas.getBoundingClientRect();
             if(rect.width > 0) {
                 this.canvas.width = rect.width;
                 this.canvas.height = rect.height;
             } else {
                 return; 
             }
        }

        const w = this.canvas.width;
        const h = this.canvas.height;
        const cx = w / 2;
        const cy = h / 2;
        
        // Adjusted Radii for better proportion
        const radius = Math.min(w, h) * 0.32; 
        const arcRadius = radius + 10; 

        this.ctx.clearRect(0, 0, w, h);

        // 1. DRAW OUTER RING / TICK AREA BACKGROUND (Distinct from Panel)
        // Using a slightly lighter dark grey to create depth
        this.ctx.fillStyle = '#1A2B36'; 
        this.ctx.beginPath();
        this.ctx.arc(cx, cy, arcRadius + 5, 0, Math.PI * 2);
        this.ctx.fill();

        // 2. CALCULATE ANGLES
        const startAngle = Math.PI * 0.75; // 135 deg
        const endAngle = Math.PI * 2.25;   // 405 deg
        const totalSweep = endAngle - startAngle;
        const percent = (this.value - this.min) / (this.max - this.min);
        const currentAngle = startAngle + (percent * totalSweep);

        // 3. DRAW NEON ARC (Active Indicator)
        this.ctx.strokeStyle = this.accentColor;
        this.ctx.lineWidth = 5; // Thicker line
        this.ctx.lineCap = 'round';
        this.ctx.shadowBlur = 15; // Stronger glow
        this.ctx.shadowColor = this.accentColor;
        
        this.ctx.beginPath();
        this.ctx.arc(cx, cy, arcRadius, startAngle, currentAngle);
        this.ctx.stroke();
        this.ctx.shadowBlur = 0; // Reset shadow

        // 4. DRAW TICK MARKS (White/Grey for visibility against dark bg)
        const tickCount = 11;
        for (let i = 0; i <= tickCount; i++) {
            const tPercent = i / tickCount;
            const tAngle = startAngle + (tPercent * totalSweep);
