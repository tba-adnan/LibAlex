package com.example.libalex

import BooksApiClient
import android.os.Bundle
import android.util.Log
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

}
