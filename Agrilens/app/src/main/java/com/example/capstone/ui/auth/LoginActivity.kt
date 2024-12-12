package com.example.capstone.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.capstone.MainActivity
import com.example.capstone.data.repository.AuthRepository
import com.example.capstone.data.retrofit.ApiClient
import com.example.capstone.databinding.ActivityLoginBinding
import com.example.capstone.response.LoginResponse

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
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

        // Cek status login
        checkLoginStatus()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        observeViewModel()
    }

    private fun checkLoginStatus() {
        val sharedPref = getSharedPreferences("USER_PREF", MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("IS_LOGGED_IN", false)
        if (isLoggedIn) {
            // Jika sudah login, langsung navigasi ke MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setupListeners() {
        binding.capstoneLoginButton.setOnClickListener {
            val email = binding.capstoneEmailInput.text.toString()
            val password = binding.capstonePasswordInput.text.toString()

            if (!isValidEmail(email)) {
                binding.capstoneEmailInput.error = "Invalid email format"
                return@setOnClickListener
            } else {
                binding.capstoneEmailInput.error = null
            }

            if (password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            authViewModel.login(email, password)
        }

        binding.registerNow.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        authViewModel.loginResult.observe(this) { response ->
            handleLoginResponse(response)
        }

        authViewModel.error.observe(this) { errorMessage ->
            handleError(errorMessage)
        }
    }

    private fun handleLoginResponse(response: LoginResponse) {
        if (response.error == true) {
            Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Welcome, hope your plants healthy!", Toast.LENGTH_SHORT).show()

            // Simpan status login di SharedPreferences
            val sharedPref = getSharedPreferences("USER_PREF", MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putBoolean("IS_LOGGED_IN", true)
            editor.apply()

            // Navigasi ke MainActivity
            val intent = Intent(this, MainActivity::class.java)
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
