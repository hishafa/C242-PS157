package com.example.capstone.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.capstone.R
import com.example.capstone.databinding.FragmentResultBinding
import java.io.File
import java.io.FileOutputStream

class ResultFragment : Fragment() {

    private var isBookmarked = false // Bookmark status
    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)

        // Display the classification result
        displayResult()

        // Setup bookmark button
        setupBookmarkButton()

        // Back button to return
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        return binding.root
    }

    private fun displayResult() {
        val resultTitle = arguments?.getString("RESULT_TITLE") ?: "Unknown Issue"
        val diagnosis = arguments?.getString("DIAGNOSIS") ?: "Unknown Diagnosis"
        val resultTreatment = arguments?.getString("RESULT_TREATMENT") ?: "No treatment available."
        val imageUriString = arguments?.getString("IMAGE_URI")

        // Set Header Title
        binding.tvResultTitle.text = resultTitle

        // Set Diagnosis
        binding.tvDiagnosis.text = diagnosis

        // Set Treatment Description
        binding.tvResultDescription.text = resultTreatment

        // Display the scanned image
        if (!imageUriString.isNullOrEmpty()) {
            val originalUri = Uri.parse(imageUriString)
            val safeUri = copyUriToInternalStorage(originalUri) ?: originalUri

            try {
                val inputStream = requireContext().contentResolver.openInputStream(safeUri)
                val drawable = Drawable.createFromStream(inputStream, safeUri.toString())
                binding.ivScanImage.setImageDrawable(drawable)
            } catch (e: Exception) {
                e.printStackTrace()
                binding.ivScanImage.setImageResource(R.drawable.ic_placeholder_image)
                Toast.makeText(requireContext(), "Failed to load image.", Toast.LENGTH_SHORT).show()
            }
        } else {
            binding.ivScanImage.setImageResource(R.drawable.ic_placeholder_image)
        }

        // Check if this result is already bookmarked
        isBookmarked = checkIfBookmarked(resultTitle, diagnosis)
        updateBookmarkButton()
    }

    private fun setupBookmarkButton() {
        binding.btnBookmark.setOnClickListener {
            val resultTitle = binding.tvResultTitle.text.toString()
            val diagnosis = binding.tvDiagnosis.text.toString()
            val imageUri = arguments?.getString("IMAGE_URI") ?: ""
            val treatment = arguments?.getString("RESULT_TREATMENT") ?: ""

            if (isBookmarked) {
                removeFromBookmark(resultTitle, diagnosis)
            } else {
                saveToBookmark(resultTitle, diagnosis, imageUri, treatment)
            }
            isBookmarked = !isBookmarked
            updateBookmarkButton()
        }
    }

    private fun saveToBookmark(title: String, diagnose: String, imageUri: String, treatment: String) {
        val sharedPreferences = requireContext().getSharedPreferences("bookmarks", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val bookmarkSet = sharedPreferences.getStringSet("bookmark_list", mutableSetOf()) ?: mutableSetOf()

        val bookmark = "$title|$diagnose|$imageUri|$treatment"
        if (!bookmarkSet.contains(bookmark)) {
            bookmarkSet.add(bookmark)
            editor.putStringSet("bookmark_list", bookmarkSet)
            editor.apply()
            Toast.makeText(requireContext(), "Saved to bookmark", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Bookmark already exists.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun removeFromBookmark(title: String, diagnose: String) {
        val sharedPreferences = requireContext().getSharedPreferences("bookmarks", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val bookmarkSet = sharedPreferences.getStringSet("bookmark_list", mutableSetOf()) ?: mutableSetOf()

        val bookmarkToRemove = bookmarkSet.find { it.startsWith("$title|$diagnose|") }
        if (bookmarkToRemove != null) {
            bookmarkSet.remove(bookmarkToRemove)
            editor.putStringSet("bookmark_list", bookmarkSet)
            editor.apply()
            Toast.makeText(requireContext(), "Removed from bookmark", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkIfBookmarked(title: String, diagnose: String): Boolean {
        val sharedPreferences = requireContext().getSharedPreferences("bookmarks", Context.MODE_PRIVATE)
        val bookmarkSet = sharedPreferences.getStringSet("bookmark_list", mutableSetOf()) ?: mutableSetOf()
        return bookmarkSet.any { it.startsWith("$title|$diagnose|") }
    }

    private fun updateBookmarkButton() {
        if (isBookmarked) {
            binding.btnBookmark.text = "Remove from Bookmark"
            binding.btnBookmark.setBackgroundColor(Color.WHITE)
            binding.btnBookmark.setTextColor(Color.parseColor("#4CAF50"))
        } else {
            binding.btnBookmark.text = "Save to Bookmark"
            binding.btnBookmark.setBackgroundColor(Color.parseColor("#4CAF50"))
            binding.btnBookmark.setTextColor(Color.WHITE)
        }
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
