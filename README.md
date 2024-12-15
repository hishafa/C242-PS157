# AgriLens

AgriLens is an application designed to detect plant diseases through images using machine learning models. It helps farmers and plant enthusiasts identify diseases and provides tailored solutions for treatment.

## APK Download
You can download the latest version of the AgriLens application [here](https://bit.ly/Agrilens).

If you wanna to see the details about the App you can visit this repository [here](https://github.com/upiwiwiw/Agrilens-by-Luthfi).

## Informasi pentiung

jika server pada API down maka gunakan kodingan MainActivity berikut, lalu run pada MainActivty, untuk sekedar mengakses fitur utama.

    package com.example.capstone

    import android.os.Bundle
    import android.view.View
    import android.widget.Toast
    import androidx.appcompat.app.AppCompatActivity
    import androidx.navigation.NavController
    import androidx.navigation.fragment.NavHostFragment
    import androidx.navigation.ui.setupWithNavController
    import com.example.capstone.databinding.ActivityMainBinding
    import timber.log.Timber
    
    class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
    
            // Inisialisasi binding
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
    
            // Setup Navigation
            setupNavigation()
    
            // Handle BottomNavigation actions
            handleBottomNavigation()
            showLoading(false)
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
                Timber.tag("MainActivity").e("NavHostFragment not found.")
                Toast.makeText(this, "Navigation error occurred.", Toast.LENGTH_SHORT).show()
            }
        }
    
        private fun handleBottomNavigation() {
            binding.bottomNavigation.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.homeFragment -> {
                        if (navController.currentDestination?.id != R.id.homeFragment) {
                            // Navigate to HomeFragment only if not already on it
                            showLoading(true)
                            navController.navigate(R.id.homeFragment)
                            showLoading(false)
                        }
                        true
                    }
                    R.id.scannerFragment -> {
                        if (navController.currentDestination?.id != R.id.scannerFragment) {
                            // Navigate to ScannerFragment only if not already on it
                            showLoading(true)
                            navController.navigate(R.id.scannerFragment)
                            showLoading(false)
                        }
                        true
                    }
                    else -> false
                }
            }
        }
    
        private fun showLoading(isLoading: Boolean) {
            if (::binding.isInitialized) {
                if (isLoading) {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.loadingOverlay.visibility = View.VISIBLE
                } else {
                    binding.progressBar.visibility = View.GONE
                    binding.loadingOverlay.visibility = View.GONE
                }
            } else {
                Timber.e("Binding not initialized")
            }
        }
    }


## Features
- Upload or capture images of plants.
- Get instant disease detection results.
- Receive treatment suggestions for identified diseases.

## How It Works
1. **Image Analysis**: Users can upload an image of their plant.
2. **Disease Detection**: AgriLens processes the image using an integrated machine learning model.
3. **Treatment Recommendations**: The application provides treatment options based on the detected disease.

## Development Team
Below are the developers who contributed to the creation of AgriLens:

| **Student ID**      | **Name**                         | **Email**                          | **Learning Path**       | **University**                     |
|----------------------|----------------------------------|------------------------------------|-------------------------|-------------------------------------|
| M284B4KX0584        | Annisa Shafa Brilianty Lebeharia | M284B4KX0584@bangkit.academy      | Machine Learning        | Universitas Negeri Surabaya        |
| M284B4KY1400        | Farell Hafidz Irkhami           | M284B4KY1400@bangkit.academy      | Machine Learning        | Universitas Negeri Surabaya        |
| A284B4KY2935        | Muhammad Luthfi                 | A284B4KY2935@bangkit.academy      | Mobile Development      | Universitas Negeri Surabaya        |
| C284B4KX3190        | Nabilah Salwa Salsabila         | C284B4KX3190@bangkit.academy      | Cloud Computing         | Universitas Negeri Surabaya        |
| C284B4KY3409        | Nur Ahmad Siroj Rohmatillah     | C284B4KY3409@bangkit.academy      | Cloud Computing         | Universitas Negeri Surabaya        |
| M284B4KX4158        | Sinta Ayu Dwi Ardita           | M284B4KX4158@bangkit.academy      | Machine Learning        | Universitas Negeri Surabaya        |
| A284B4KY4582        | Yusuf Kelvin Siregar            | A284B4KY4582@bangkit.academy      | Mobile Development      | Universitas Negeri Surabaya        |

## Why AgriLens?
AgriLens aims to improve productivity and sustainability in agriculture by:
- Helping farmers detect plant diseases early.
- Reducing losses due to untreated diseases.
- Providing actionable insights to maintain plant health.
