// ==================================================
// FILE: knob.js
// VERSION: v2.4.0-proto-knob-auto-redraw
// PURPOSE: High-Fidelity Rotary Control with Auto-Redraw on Tab Change
// NOTE: Listens to global 'tabChanged' event to ensure visibility.
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
            // Small delay to ensure CSS transitions finished
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
        // Safety check: If canvas has 0 width/height due to hidden state, skip or retry
        if (this.canvas.width === 0 || this.canvas.height === 0) {
             // Try to resize based on CSS attributes if possible, otherwise wait for next tick
             const rect = this.canvas.getBoundingClientRect();
             if(rect.width > 0) {
                 this.canvas.width = rect.width;
                 this.canvas.height = rect.height;
             } else {
                 return; // Skip drawing if truly invisible
             }
        }

        const w = this.canvas.width;
        const h = this.canvas.height;
        const cx = w / 2;
        const cy = h / 2;
        const radius = Math.min(w, h) * 0.35;
        const arcRadius = radius + 8;

        this.ctx.clearRect(0, 0, w, h);

        // 1. Panel Background
        this.ctx.fillStyle = '#12232E';
        this.ctx.beginPath();
        this.ctx.arc(cx, cy, radius + 15, 0, Math.PI * 2);
        this.ctx.fill();

        // 2. Angles
        const startAngle = Math.PI * 0.75; 
        const endAngle = Math.PI * 2.25;   
        const totalSweep = endAngle - startAngle;
        const percent = (this.value - this.min) / (this.max - this.min);
        const currentAngle = startAngle + (percent * totalSweep);

        // 3. Neon Arc
        this.ctx.strokeStyle = this.accentColor;
        this.ctx.lineWidth = 4;
        this.ctx.lineCap = 'round';
        this.ctx.shadowBlur = 10;
        this.ctx.shadowColor = this.accentColor;
        
        this.ctx.beginPath();
        this.ctx.arc(cx, cy, arcRadius, startAngle, currentAngle);
        this.ctx.stroke();
        this.ctx.shadowBlur = 0;

        // 4. Ticks
        const tickCount = 11;
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
            this.ctx.strokeStyle = '#3A4A54';
            this.ctx.lineWidth = 2;
            this.ctx.stroke();
        }

        // 5. Knob Body
        const gradient = this.ctx.createRadialGradient(cx - 5, cy - 5, 5, cx, cy, radius);
        gradient.addColorStop(0, '#2A5F7A');
        gradient.addColorStop(1, '#081218');
        
        this.ctx.fillStyle = gradient;
        this.ctx.beginPath();
        this.ctx.arc(cx, cy, radius, 0, Math.PI * 2);
        this.ctx.fill();
        this.ctx.strokeStyle = '#000';
        this.ctx.lineWidth = 2;
        this.ctx.stroke();

        // 6. Pointer
        this.ctx.save();
        this.ctx.translate(cx, cy);
        this.ctx.rotate(currentAngle);
        this.ctx.strokeStyle = '#FFFFFF';
        this.ctx.lineWidth = 3;
        this.ctx.lineCap = 'round';
        this.ctx.beginPath();
        this.ctx.moveTo(0, -radius * 0.3);
        this.ctx.lineTo(0, -radius * 0.9);
        this.ctx.stroke();
        this.ctx.restore();
        
        // 7. Value Text
        this.ctx.fillStyle = this.accentColor;
        this.ctx.font = 'bold 10px sans-serif';
        this.ctx.textAlign = 'center';
        this.ctx.fillText(Math.round(this.value), cx, cy + radius + 20);
    }
}

window.RotaryKnob = RotaryKnob;
