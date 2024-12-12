package com.example.capstone.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.capstone.data.repository.AuthRepository
import com.example.capstone.data.retrofit.ApiClient
import com.example.capstone.databinding.ActivityRegisterBinding
import com.example.capstone.response.RegisterResponse

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(
            AuthRepository(
                ApiClient.getRegisterService(applicationContext),
                ApiClient.getLoginService(applicationContext)
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.capstoneRegisterButton.setOnClickListener {
            val name = binding.capstoneNameInput.text.toString()
            val email = binding.capstoneEmailInput.text.toString()
            val password = binding.capstonePasswordInput.text.toString()
            val confirmPassword = binding.capstoneConfirmPasswordInput.text.toString()

            if (name.isBlank()) {
                binding.capstoneNameInput.error = "Name cannot be empty"
                return@setOnClickListener
            }

            if (!isValidEmail(email)) {
                binding.capstoneEmailInput.error = "Invalid email format"
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            authViewModel.register(name, email, password, confirmPassword)
        }
    }

    private fun observeViewModel() {
        authViewModel.registerResult.observe(this) { response ->
            handleRegisterResponse(response)
        }

        authViewModel.error.observe(this) { errorMessage ->
            handleError(errorMessage)
        }
    }

    private fun handleRegisterResponse(response: RegisterResponse) {
        if (response.error == true) {
            Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Registration successful! Please login to continue.", Toast.LENGTH_SHORT).show()

            // Navigasi ke LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun handleError(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
