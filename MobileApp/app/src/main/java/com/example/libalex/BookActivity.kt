package com.example.libalex

import BooksApiClient
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.Toast


class BookActivity : AppCompatActivity() {
    private lateinit var booksListView: ListView

    private val booksApiClient = BooksApiClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book)

        booksListView = findViewById(R.id.booksListView)

        fetchBooks()

        val fabQRCode: FloatingActionButton = findViewById(R.id.fabQRCode)
        fabQRCode.setOnClickListener {
            val intent = Intent(this, QrActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchBooks() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = booksApiClient.getSavedBooks()
                withContext(Dispatchers.Main) {
                    if (response.isNotEmpty()) {
                        val bookTitles = response.mapNotNull { it.book_title }
                        val adapter = ArrayAdapter(
                            this@BookActivity,
                            android.R.layout.simple_list_item_1,
                            bookTitles
                        )
                        booksListView.adapter = adapter

                        booksListView.onItemClickListener =
                            AdapterView.OnItemClickListener { _, _, position, _ ->
                                val book = response[position]
                                val bookId = book.id
                                val bookTitle = book.book_title
                                showDeleteConfirmationDialog(bookId)


                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        val summary = booksApiClient.getBookSummary(bookTitle)
                                        Log.d("BookActivity", "Book Summary: $summary")
                                    } catch (e: Exception) {
                                        // Handle error
                                        Log.e("BookActivity", "Error getting book summary: ${e.message}", e)
                                    }
                                }
                            }
                    } else {
                        // Handling empty response (Just in case)
                    }
                }
            } catch (e: Exception) {

                Log.e("BookActivity", "Error: ${e.message}", e)
            }
        }
    }

    private fun showDeleteConfirmationDialog(bookId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = booksApiClient.getSavedBooks()
                withContext(Dispatchers.Main) {
                    if (response.isNotEmpty()) {
                        val book = response.find { it.id == bookId }
                        if (book != null) {
                            val bookTitle = book.book_title

                            // Retrieve the book summary in the background
                            val summary = withContext(Dispatchers.IO) {
                                booksApiClient.getBookSummary(bookTitle)
                            }

                            val dialog = AlertDialog.Builder(this@BookActivity)
                                .setTitle(" \uD83E\uDD16 Supprimer le livre : ")
                                .setMessage("Êtes-vous sûr de vouloir supprimer ce livre?")
                                .setPositiveButton("Oui") { _, _ ->
                                    deleteBook(bookId)
                                }
                                .setNegativeButton("Non") { dialog, _ ->
                                    dialog.dismiss()
                                }
                                .create()

                            dialog.setOnShowListener {
                                dialog.findViewById<TextView>(android.R.id.message)?.text = summary
                            }

                            dialog.show()
                        }
                    } else {
                        // Handling empty response (Just in case)
                    }
                }
            } catch (e: Exception) {
                // Error (to log)
                Log.e("BookActivity", "Error: ${e.message}", e)
            }
        }
    }



    private fun deleteBook(bookId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = booksApiClient.deleteBook(bookId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Log.d("BookActivity", "Book deleted successfully.")
                        fetchBooks() // Refresh the list view
                    } else {
                        Log.e("BookActivity", "Delete Book API Error: ${response.errorBody()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("BookActivity", "Error: ${e.message}", e)
            }
        }
    }
}
