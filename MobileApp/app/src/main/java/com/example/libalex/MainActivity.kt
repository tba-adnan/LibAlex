package com.example.libalex

import BooksApiClient
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var searchEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var booksListView: ListView

    private val booksApiClient = BooksApiClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchEditText = findViewById(R.id.searchEditText)
        searchButton = findViewById(R.id.searchButton)
        booksListView = findViewById(R.id.booksListView)

        searchButton.setOnClickListener {
            val query = searchEditText.text.toString()
            if (query.isNotEmpty()) {
                searchBooks(query)
            }
        }
    }

    private fun searchBooks(query: String) {
        val maxResults = 10

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = booksApiClient.searchBooks(query, maxResults)
                withContext(Dispatchers.Main) {
                    if (response.isNotEmpty()) {
                        val books = response.map { it.volumeInfo }
                        val adapter = ArrayAdapter(
                            this@MainActivity,
                            android.R.layout.simple_list_item_1,
                            books.map { it.title }
                        )
                        booksListView.adapter = adapter

                        booksListView.setOnItemClickListener { _, _, position, _ ->
                            val book = response[position]
                            displayBookToast(book.volumeInfo.title)
                        }
                    } else {
                        // Handle empty response
                    }
                }
            } catch (e: Exception) {
                // Handle network failure
            }
        }
    }

    private fun displayBookToast(bookTitle: String) {
        Toast.makeText(this, "Selected book: $bookTitle", Toast.LENGTH_SHORT).show()
    }
}
