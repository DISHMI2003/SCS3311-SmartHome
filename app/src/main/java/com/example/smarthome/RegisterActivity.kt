package com.example.smarthome

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var etFullName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText

    private lateinit var btnShowPassword: ImageButton
    private lateinit var btnShowConfirmPassword: ImageButton
    private lateinit var btnCreateAccount: Button


    private lateinit var cbTerms: CheckBox
    private lateinit var tvBackToLogin: TextView

    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        setContentView(R.layout.activity_register)

        initializeViews()
        setupClickListeners()
    }

    private fun initializeViews() {
        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etRegisterEmail)
        etPassword = findViewById(R.id.etRegisterPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)

        btnShowPassword = findViewById(R.id.btnShowRegisterPassword)
        btnShowConfirmPassword = findViewById(R.id.btnShowConfirmPassword)
        btnCreateAccount = findViewById(R.id.btnCreateAccount)


        cbTerms = findViewById(R.id.cbTerms)
        tvBackToLogin = findViewById(R.id.tvBackToLogin)
    }

    private fun setupClickListeners() {
        btnShowPassword.setOnClickListener {
            togglePasswordVisibility()
        }

        btnShowConfirmPassword.setOnClickListener {
            toggleConfirmPasswordVisibility()
        }

        btnCreateAccount.setOnClickListener {
            validateRegistration()
        }

        tvBackToLogin.setOnClickListener {
            openLoginScreen()
        }


    }

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible

        etPassword.inputType = if (isPasswordVisible) {
            InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        btnShowPassword.contentDescription =
            if (isPasswordVisible) "Hide password" else "Show password"

        etPassword.setSelection(etPassword.text.length)
    }

    private fun toggleConfirmPasswordVisibility() {
        isConfirmPasswordVisible = !isConfirmPasswordVisible

        etConfirmPassword.inputType = if (isConfirmPasswordVisible) {
            InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        btnShowConfirmPassword.contentDescription =
            if (isConfirmPasswordVisible) {
                "Hide confirm password"
            } else {
                "Show confirm password"
            }

        etConfirmPassword.setSelection(etConfirmPassword.text.length)
    }

    private fun validateRegistration() {
        val fullName = etFullName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()

        clearErrors()

        when {
            fullName.isEmpty() -> {
                etFullName.error = "Full name is required"
                etFullName.requestFocus()
            }

            fullName.length < 3 -> {
                etFullName.error = "Enter a valid full name"
                etFullName.requestFocus()
            }

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
                etPassword.error =
                    "Password must contain at least 6 characters"
                etPassword.requestFocus()
            }

            confirmPassword.isEmpty() -> {
                etConfirmPassword.error = "Confirm your password"
                etConfirmPassword.requestFocus()
            }

            password != confirmPassword -> {
                etConfirmPassword.error = "Passwords do not match"
                etConfirmPassword.requestFocus()
            }

            !cbTerms.isChecked -> {
                Toast.makeText(
                    this,
                    "Please accept the Terms of Service and Privacy Policy",
                    Toast.LENGTH_LONG
                ).show()
            }

            else -> {
                performTemporaryRegistration(fullName)
            }
        }
    }

    private fun clearErrors() {
        etFullName.error = null
        etEmail.error = null
        etPassword.error = null
        etConfirmPassword.error = null
    }

    private fun performTemporaryRegistration(fullName: String) {
        Toast.makeText(
            this,
            "Account created successfully for $fullName",
            Toast.LENGTH_SHORT
        ).show()

        openLoginScreen()
    }

    private fun openLoginScreen() {
        val intent = Intent(this, LoginActivity::class.java)

        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP

        startActivity(intent)
        finish()
    }
}