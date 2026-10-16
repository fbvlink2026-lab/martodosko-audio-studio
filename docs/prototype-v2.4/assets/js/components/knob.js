// ==================================================
// FILE: knob.js
// VERSION: v2.4.0-DEBUG-KNOB
// PURPOSE: Rotary Knob with Debug Logging
// NOTE: Prints to console if Canvas is missing or has 0 size. Draws a RED BOX if error occurs.
// ==================================================

class RotaryKnob {
    constructor(elementId, min, max, initialVal, onChangeCallback, accentColor = '#40E0D0') {
        this.elementId = elementId; // Store ID for logging
        this.canvas = document.getElementById(elementId);
        
        // DEBUG LOG 1: Check if Element Found
        if (!this.canvas) {
            console.error(`❌ [KNOB ERROR] Canvas NOT FOUND for ID: "${elementId}". Check index.html.`);
            return;
        }

        console.log(`✅ [KNOB INIT] Found Canvas: ${elementId}`);

        this.ctx = this.canvas.getContext('2d');
        this.min = min;
        this.max = max;
        this.value = initialVal;
        this.onChange = onChangeCallback;
        this.accentColor = accentColor;
        
        this.isDragging = false;
        this.lastY = 0;
        this.sensitivity = 0.5;

        // Bind Events
        this.bindEvents();
        
        // Listen for Tab Changes
        window.addEventListener('tabChanged', () => {
            console.log(`🔄 [KNOB REDRAW] Triggered by tab change for: ${this.elementId}`);
            setTimeout(() => this.draw(), 50);
        });

        // Initial Draw Attempt
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
        console.log(`🖐️ [INTERACTION] Drag Start on ${this.elementId}`);
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
        // DEBUG LOG 2: Check Dimensions
        const rect = this.canvas.getBoundingClientRect();
        const w = this.canvas.width;
        const h = this.canvas.height;

        console.log(`📏 [DRAW CHECK] ${this.elementId} | CSS Size: ${rect.width.toFixed(1)}x${rect.height.toFixed(1)} | Canvas Attrs: ${w}x${h}`);

        // If canvas attributes are 0 but CSS says it has size, FORCE RESIZE
        if ((w === 0 || h === 0) && rect.width > 0 && rect.height > 0) {
            console.warn(`⚠️ [FIXING SIZE] Canvas attrs were 0. Setting to match CSS (${rect.width}x${rect.height}).`);
            this.canvas.width = rect.width;
            this.canvas.height = rect.height;
        }

        // Final Safety Check
        if (this.canvas.width === 0 || this.canvas.height === 0) {
            console.error(`❌ [DRAW FAIL] Canvas still has 0 dimensions after fix! Skipping draw.`);
            
            // VISUAL FALLBACK: Draw a red box so user knows something is wrong
            this.ctx.fillStyle = '#FF0000';
            this.ctx.fillRect(0, 0, 50, 50);
            this.ctx.fillStyle = '#FFFFFF';
            this.ctx.font = '10px Arial';
            this.ctx.fillText("ERR", 10, 30);
            return;
        }

        const cx = this.canvas.width / 2;
        const cy = this.canvas.height / 2;
        const radius = Math.min(cx, cy) * 0.7; // Adjusted for better fit
        
        // Clear
        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);

        // 1. Panel Background
        this.ctx.fillStyle = '#12232E';
        this.ctx.beginPath();
        this.ctx.arc(cx, cy, radius + 5, 0, Math.PI * 2);
        this.ctx.fill();

        // 2. Angles
        const startAngle = Math.PI * 0.75; 
        const endAngle = Math.PI * 2.25;   
        const totalSweep = endAngle - startAngle;
        const percent = (this.value - this.min) / (this.max - this.min);
        const currentAngle = startAngle + (percent * totalSweep);

        // 3. Neon Arc
        this.ctx.strokeStyle = this.accentColor;
        this.ctx.lineWidth = 3;
        this.ctx.lineCap = 'round';
        this.ctx.shadowBlur = 8;
        this.ctx.shadowColor = this.accentColor;
        
        this.ctx.beginPath();
        this.ctx.arc(cx, cy, radius, startAngle, currentAngle);
        this.ctx.stroke();
        this.ctx.shadowBlur = 0;

        // 4. Knob Body
        const gradient = this.ctx.createRadialGradient(cx - 5, cy - 5, 5, cx, cy, radius * 0.9);
        gradient.addColorStop(0, '#2A5F7A');
        gradient.addColorStop(1, '#081218');
        
        this.ctx.fillStyle = gradient;
        this.ctx.beginPath();
        this.ctx.arc(cx, cy, radius * 0.9, 0, Math.PI * 2);
        this.ctx.fill();
        this.ctx.strokeStyle = '#000';
        this.ctx.lineWidth = 2;
        this.ctx.stroke();

        // 5. Pointer
        this.ctx.save();
        this.ctx.translate(cx, cy);
        this.ctx.rotate(currentAngle);
        this.ctx.strokeStyle = '#FFFFFF';
        this.ctx.lineWidth = 3;
        this.ctx.lineCap = 'round';
        this.ctx.beginPath();
        this.ctx.moveTo(0, -radius * 0.2);
        this.ctx.lineTo(0, -radius * 0.85);
        this.ctx.stroke();
        this.ctx.restore();
        
        // 6. Value Text
        this.ctx.fillStyle = this.accentColor;
        this.ctx.font = 'bold 12px sans-serif';
        this.ctx.textAlign = 'center';
        this.ctx.fillText(Math.round(this.value), cx, cy + radius + 15);
        
        console.log(`🎨 [DRAW SUCCESS] Rendered ${this.elementId} at value ${Math.round(this.value)}`);
    }
}

window.RotaryKnob = RotaryKnob;
