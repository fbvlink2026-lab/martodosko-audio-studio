package com.martodosko.studio

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ❌ WALANG setContentView — WALANG LAYOUT!
        Log.d("MARTODOSKO", "✅ BUMUKAS!")
        Toast.makeText(this, "Gumagana!", Toast.LENGTH_SHORT).show()
    }
}
