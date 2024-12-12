package com.example.capstone.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.capstone.model.ClassificationResult
import com.example.capstone.model.ImageClassifierHelper
import com.example.capstone.R
import com.example.capstone.databinding.FragmentScannerBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ScannerFragment : Fragment(), ImageClassifierHelper.ClassifierListener {

    private var _binding: FragmentScannerBinding? = null
    private val binding get() = _binding!!

    private lateinit var imageClassifierHelper: ImageClassifierHelper
    private var selectedImageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the TensorFlow Lite Classifier
        imageClassifierHelper = ImageClassifierHelper(requireContext())

        // Button to open gallery
        binding.btnOpenGallery.setOnClickListener { openGallery() }

        // Button to start analysis
        binding.btnAnalyze.setOnClickListener { analyzeImage() }

        // Initially disable the analyze button until an image is selected
        binding.btnAnalyze.isEnabled = false
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            if (selectedImageUri != null) {
                try {
                    // Copy URI to internal storage
                    val safeUri = copyUriToInternalStorage(selectedImageUri!!) ?: selectedImageUri

                    // Display the image in ImageView
                    val bitmap = MediaStore.Images.Media.getBitmap(
                        requireContext().contentResolver,
                        safeUri
                    )
                    binding.ivScanImage.setImageBitmap(bitmap)

                    // Enable analyze button
                    binding.btnAnalyze.isEnabled = true
                } catch (e: IOException) {
                    Log.e("ScannerFragment", "Error loading image: ${e.message}")
                    Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun analyzeImage() {
        if (selectedImageUri == null) {
            Toast.makeText(requireContext(), "Please choose an image first", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            // Copy URI to internal storage
            val safeUri = copyUriToInternalStorage(selectedImageUri!!) ?: selectedImageUri

            // Convert URI to Bitmap
            val bitmap = MediaStore.Images.Media.getBitmap(
                requireContext().contentResolver,
                safeUri
            )

            // Send the image to TensorFlow Lite for analysis
            imageClassifierHelper.classifyBitmap(bitmap, this)
        } catch (e: IOException) {
            Log.e("ScannerFragment", "Error analyzing image: ${e.message}")
            Toast.makeText(requireContext(), "Failed to analyze image", Toast.LENGTH_SHORT).show()
        }
    }

    // Implementation of onResult from ClassifierListener
    override fun onResult(result: ClassificationResult) {
        val bundle = Bundle().apply {
            putString("RESULT_TITLE", result.title)
            putString("RESULT_TREATMENT", result.treatment)
            putString("DIAGNOSIS", result.diagnosis)
            putFloat("CONFIDENCE", result.confidence)
            putString("IMAGE_URI", selectedImageUri.toString())
        }
        findNavController().navigate(R.id.action_scannerFragment_to_resultFragment, bundle)
    }

    // Implementation of onError from ClassifierListener
    override fun onError(error: String) {
        Log.e("ScannerFragment", "Classification error: $error")
        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
    }

    private fun copyUriToInternalStorage(uri: Uri): Uri? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val file = File(requireContext().cacheDir, "temp_image.jpg")
            val outputStream = FileOutputStream(file)

            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
