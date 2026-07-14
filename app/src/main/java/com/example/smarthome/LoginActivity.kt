package com.example.smarthome

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnSignIn: Button
    private lateinit var btnShowPassword: ImageButton
    private lateinit var tvForgotPassword: TextView
    private lateinit var tvRegister: TextView


    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        setContentView(R.layout.activity_login)

        initializeViews()
        setupClickListeners()
    }

    private fun initializeViews() {
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnSignIn = findViewById(R.id.btnSignIn)
        btnShowPassword = findViewById(R.id.btnShowPassword)
        tvForgotPassword = findViewById(R.id.tvForgotPassword)
        tvRegister = findViewById(R.id.tvRegister)

    }

    private fun setupClickListeners() {
        btnSignIn.setOnClickListener {
            validateLogin()
        }

        btnShowPassword.setOnClickListener {
            togglePasswordVisibility()
        }

        tvForgotPassword.setOnClickListener {
            Toast.makeText(
                this,
                "Password reset will be connected later",
                Toast.LENGTH_SHORT
            ).show()
        }

        tvRegister.setOnClickListener {
            Toast.makeText(
                this,
                "Registration screen will be created next",
                Toast.LENGTH_SHORT
            ).show()

            /*
            After creating RegisterActivity, replace the Toast with:

            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            */
        }



    }

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible

        if (isPasswordVisible) {
            etPassword.inputType =
                InputType.TYPE_CLASS_TEXT or
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD

            btnShowPassword.contentDescription = "Hide password"
        } else {
            etPassword.inputType =
                InputType.TYPE_CLASS_TEXT or
                        InputType.TYPE_TEXT_VARIATION_PASSWORD

            btnShowPassword.contentDescription = "Show password"
        }

        // Keep the cursor at the end of the password
        etPassword.setSelection(etPassword.text.length)
    }

    private fun validateLogin() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()

        etEmail.error = null
        etPassword.error = null

        when {
            email.isEmpty() -> {
                etEmail.error = "Email address is required"
                etEmail.requestFocus()
            }

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                etEmail.error = "Enter a valid email address"
                etEmail.requestFocus()
            }

            password.isEmpty() -> {
                etPassword.error = "Password is required"
                etPassword.requestFocus()
            }

            password.length < 6 -> {
                etPassword.error = "Password must contain at least 6 characters"
                etPassword.requestFocus()
            }

            else -> {
                performTemporaryLogin()
            }
        }
    }

    private fun performTemporaryLogin() {
        Toast.makeText(
            this,
            "Login successful",
            Toast.LENGTH_SHORT
        ).show()

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}