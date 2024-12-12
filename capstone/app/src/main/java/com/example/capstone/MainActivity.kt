package com.example.capstone

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.capstone.databinding.ActivityMainBinding
import com.example.capstone.ui.auth.LoginActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Cek status login sebelum melanjutkan
        checkLoginStatus()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Navigation
        setupNavigation()

        // Handle BottomNavigation actions
        handleBottomNavigation()
    }

    private fun checkLoginStatus() {
        val sharedPref = getSharedPreferences("USER_PREF", MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("IS_LOGGED_IN", false)
        if (!isLoggedIn) {
            // Jika belum login, arahkan ke LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun setupNavigation() {
        // Initialize NavHostFragment and NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
        if (navHostFragment != null) {
            navController = navHostFragment.navController

            // Link BottomNavigationView with NavController
            binding.bottomNavigation.setupWithNavController(navController)
        } else {
            // Log error if NavHostFragment is not found
            throw IllegalStateException("NavHostFragment not found in MainActivity")
        }
    }

    private fun handleBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    if (navController.currentDestination?.id != R.id.homeFragment) {
                        // Navigate to HomeFragment only if not already on it
                        navController.navigate(R.id.homeFragment)
                    }
                    true
                }
                R.id.scannerFragment -> {
                    if (navController.currentDestination?.id != R.id.scannerFragment) {
                        // Navigate to ScannerFragment only if not already on it
                        navController.navigate(R.id.scannerFragment)
                    }
                    true
                }
                R.id.menu_logout -> {
                    // Confirm before logout
                    logoutWithConfirmation()
                    true
                }
                else -> false
            }
        }
    }

    private fun logoutWithConfirmation() {
        // Show confirmation dialog
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ -> performLogout() }
            .setNegativeButton("No", null)
            .create()
        dialog.show()
    }

    private fun performLogout() {
        // Clear SharedPreferences
        val sharedPref = getSharedPreferences("USER_PREF", MODE_PRIVATE)
        sharedPref.edit().clear().apply()

        // Show a logout confirmation toast
        Toast.makeText(this, "You have successfully logged out.", Toast.LENGTH_SHORT).show()

        // Navigate to LoginActivity
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
