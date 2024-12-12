package com.example.capstone.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstone.R
import com.example.capstone.databinding.FragmentHomeBinding
import com.example.capstone.ui.auth.LoginActivity
import com.example.capstone.view.bookmark.Bookmark
import com.example.capstone.view.bookmark.BookmarkAdapter

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: BookmarkAdapter
    private var bookmarks = mutableListOf<Bookmark>() // MutableList untuk mempermudah penghapusan

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true) // Enable menu options
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ambil bookmark dari SharedPreferences
        bookmarks = loadBookmarks().toMutableList()

        // Inisialisasi RecyclerView
        setupRecyclerView()

        // Navigasi ke ScannerFragment
        binding.capstoneBtnScanNow.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_scannerFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh data setiap kali fragment kembali aktif
        bookmarks.clear()
        bookmarks.addAll(loadBookmarks())
        adapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.bottom_nav_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_logout -> {
                performLogout() // Handle logout
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun performLogout() {
        // Clear SharedPreferences
        val sharedPref = requireContext().getSharedPreferences("USER_PREF", Context.MODE_PRIVATE)
        sharedPref.edit().putBoolean("IS_LOGGED_IN", false).apply()

        // Navigate to LoginActivity
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    private fun setupRecyclerView() {
        adapter = BookmarkAdapter(bookmarks)
        binding.capstoneRvBookmarks.layoutManager = LinearLayoutManager(requireContext())
        binding.capstoneRvBookmarks.adapter = adapter

        adapter.setOnItemClickListener { bookmark ->
            openResultFragment(bookmark)
        }

        adapter.setOnDeleteClickListener { bookmark ->
            removeBookmark(bookmark)
        }
    }

    private fun loadBookmarks(): List<Bookmark> {
        val sharedPreferences = requireContext().getSharedPreferences("bookmarks", Context.MODE_PRIVATE)
        val bookmarkSet = sharedPreferences.getStringSet("bookmark_list", mutableSetOf()) ?: mutableSetOf()

        return bookmarkSet.mapNotNull {
            val parts = it.split("|")
            if (parts.size == 4) { // Pastikan bookmark memiliki semua data termasuk treatment
                Bookmark(parts[0], parts[1], parts[2], parts[3]) // Title, Diagnosis, Thumbnail, Treatment
            } else {
                null // Abaikan data yang tidak valid
            }
        }
    }

    private fun saveBookmarks(bookmarks: List<Bookmark>) {
        val sharedPreferences = requireContext().getSharedPreferences("bookmarks", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val bookmarkSet = bookmarks.map { "${it.title}|${it.diagnose}|${it.thumbnail}|${it.treatment}" }.toSet()

        editor.putStringSet("bookmark_list", bookmarkSet)
        editor.apply()
    }

    private fun removeBookmark(bookmark: Bookmark) {
        bookmarks.remove(bookmark)
        adapter.notifyDataSetChanged()
        saveBookmarks(bookmarks)
    }

    private fun openResultFragment(bookmark: Bookmark) {
        val bundle = Bundle().apply {
            putString("RESULT_TITLE", bookmark.title)
            putString("DIAGNOSIS", bookmark.diagnose)
            putString("IMAGE_URI", bookmark.thumbnail)
            putString("RESULT_TREATMENT", bookmark.treatment) // Tambahkan treatment
        }
        findNavController().navigate(R.id.action_homeFragment_to_resultFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
