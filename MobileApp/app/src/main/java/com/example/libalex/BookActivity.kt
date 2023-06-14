package com.example.libalex
import BooksApiClient
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BookActivity : AppCompatActivity() {
    private lateinit var booksListView: ListView

    private val booksApiClient = BooksApiClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book)

        booksListView = findViewById(R.id.booksListView)

        fetchBooks()
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
                                val bookId = response[position].id
                                showDeleteConfirmationDialog(bookId)
                            }
                    } else {
                        // Handle empty response
                    }
                }
            } catch (e: Exception) {
                // Handle network failure
                Log.e("BookActivity", "Error: ${e.message}", e)
            }
        }
    }

    private fun showDeleteConfirmationDialog(bookId: Int) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Supprimer le livre")
            .setMessage("Êtes-vous sûr de vouloir supprimer ce livre?")
            .setPositiveButton("Oui") { _, _ ->
                deleteBook(bookId)
            }
            .setNegativeButton("Non") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }

    private fun deleteBook(bookId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = booksApiClient.deleteBook(bookId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        // Book deleted successfully
                        Log.d("BookActivity", "Book deleted successfully.")
                        fetchBooks() // Refresh the list view
                    } else {
                        // Handle error
                        Log.e("BookActivity", "Delete Book API Error: ${response.errorBody()}")
                    }
                }
            } catch (e: Exception) {
                // Handle network failure
                Log.e("BookActivity", "Error: ${e.message}", e)
            }
        }
    }

}
