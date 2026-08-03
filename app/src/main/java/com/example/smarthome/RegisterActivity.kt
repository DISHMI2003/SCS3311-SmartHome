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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

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

    private fun toggleConfirmPasswordVisibility() {

        isConfirmPasswordVisible = !isConfirmPasswordVisible

        etConfirmPassword.inputType =
            if (isConfirmPasswordVisible) {
                InputType.TYPE_CLASS_TEXT or
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                InputType.TYPE_CLASS_TEXT or
                        InputType.TYPE_TEXT_VARIATION_PASSWORD
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
                etEmail.error = "Email is required"
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
                etPassword.error = "Password must be at least 6 characters"
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
                    "Please accept Terms and Conditions",
                    Toast.LENGTH_LONG
                ).show()
            }

            else -> {

                registerUser(
                    fullName,
                    email,
                    password
                )
            }
        }
    }

    private fun clearErrors() {

        etFullName.error = null
        etEmail.error = null
        etPassword.error = null
        etConfirmPassword.error = null
    }

    private fun registerUser(
        fullName: String,
        email: String,
        password: String
    ) {

        btnCreateAccount.isEnabled = false
        btnCreateAccount.text = "Creating..."

        auth.createUserWithEmailAndPassword(email, password)

            .addOnSuccessListener {

                val user = auth.currentUser

                if (user == null) {

                    restoreButton()

                    Toast.makeText(
                        this,
                        "Registration failed.",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@addOnSuccessListener
                }

                val userMap = hashMapOf(

                    "uid" to user.uid,
                    "fullName" to fullName,
                    "email" to email,
                    "role" to "user",
                    "createdAt" to System.currentTimeMillis()

                )

                firestore
                    .collection("users")
                    .document(user.uid)
                    .set(userMap)

                    .addOnSuccessListener {

                        restoreButton()

                        Toast.makeText(
                            this,
                            "Account Created Successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        auth.signOut()

                        openLoginScreen()
                    }

                    .addOnFailureListener { exception ->

                        user.delete()

                        restoreButton()

                        Toast.makeText(
                            this,
                            exception.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }

            }

            .addOnFailureListener { exception ->

                restoreButton()

                Toast.makeText(
                    this,
                    exception.message,
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun restoreButton() {

        btnCreateAccount.isEnabled = true
        btnCreateAccount.text = "Create Account"
    }

    private fun openLoginScreen() {

        val intent = Intent(
            this,
            LoginActivity::class.java
        )

        startActivity(intent)
        finish()
    }
}