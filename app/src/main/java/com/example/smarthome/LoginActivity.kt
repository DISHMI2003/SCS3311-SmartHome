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
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnSignIn: Button
    private lateinit var btnShowPassword: ImageButton
    private lateinit var tvForgotPassword: TextView
    private lateinit var tvRegister: TextView

    private lateinit var auth: FirebaseAuth

    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        // User already logged in
        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

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

            val email = etEmail.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(
                    this,
                    "Enter your email first",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            auth.sendPasswordResetEmail(email)
                .addOnSuccessListener {

                    Toast.makeText(
                        this,
                        "Password reset email sent",
                        Toast.LENGTH_LONG
                    ).show()

                }
                .addOnFailureListener {

                    Toast.makeText(
                        this,
                        it.message,
                        Toast.LENGTH_LONG
                    ).show()

                }
        }

        tvRegister.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    RegisterActivity::class.java
                )
            )

        }
    }

    private fun togglePasswordVisibility() {

        isPasswordVisible = !isPasswordVisible

        etPassword.inputType =
            if (isPasswordVisible) {

                InputType.TYPE_CLASS_TEXT or
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD

            } else {

                InputType.TYPE_CLASS_TEXT or
                        InputType.TYPE_TEXT_VARIATION_PASSWORD

            }

        etPassword.setSelection(etPassword.text.length)
    }

    private fun validateLogin() {

        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()

        etEmail.error = null
        etPassword.error = null

        when {

            email.isEmpty() -> {

                etEmail.error = "Email is required"
                etEmail.requestFocus()

            }

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {

                etEmail.error = "Enter a valid email"
                etEmail.requestFocus()

            }

            password.isEmpty() -> {

                etPassword.error = "Password is required"
                etPassword.requestFocus()

            }

            password.length < 6 -> {

                etPassword.error = "Minimum 6 characters"
                etPassword.requestFocus()

            }

            else -> {

                loginUser(email, password)

            }
        }
    }

    private fun loginUser(
        email: String,
        password: String
    ) {

        btnSignIn.isEnabled = false
        btnSignIn.text = "Signing In..."

        auth.signInWithEmailAndPassword(
            email,
            password
        )

            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    "Login Successful",
                    Toast.LENGTH_SHORT
                ).show()

                startActivity(
                    Intent(
                        this,
                        MainActivity::class.java
                    )
                )

                finish()

            }

            .addOnFailureListener {

                btnSignIn.isEnabled = true
                btnSignIn.text = "Sign In"

                Toast.makeText(
                    this,
                    it.message,
                    Toast.LENGTH_LONG
                ).show()

            }
    }
}