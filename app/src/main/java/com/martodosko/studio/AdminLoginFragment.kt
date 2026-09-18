// ==================================================
// FILE: AdminLoginFragment.kt — ✅ ADMIN LOGIN SCREEN!
// VERSION: 1.0.0 — SIMPLE LOGIN! WALANG IBANG PINAGBAGO!
// UPDATED: 2026-09-19
// ==================================================
package com.martodosko.studio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

class AdminLoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_admin_login, container, false)

        val etUsername = root.findViewById<EditText>(R.id.et_admin_user)
        val etPassword = root.findViewById<EditText>(R.id.et_admin_pass)
        val btnLogin = root.findViewById<Button>(R.id.btn_admin_login)

        btnLogin.setOnClickListener {
            val user = etUsername.text.toString().trim()
            val pass = etPassword.text.toString().trim()

            if (user == "admin" && pass == "admin123") {
                Toast.makeText(requireContext(), "✅ Admin Login Success!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "❌ Mali ang Username o Password!", Toast.LENGTH_SHORT).show()
            }
        }

        return root
    }
}
