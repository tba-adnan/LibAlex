package com.example.libalex

import BooksApiClient
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
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
                    val books = response
                    if (books.isNotEmpty()) {
                        val adapter = ArrayAdapter(
                            this@MainActivity,
                            android.R.layout.simple_list_item_1,
                            books.map { it.title }
                        )
                        booksListView.adapter = adapter

                        booksListView.setOnItemClickListener { _, _, position, _ ->
                            val book = books[position]
                            // Handle book selection
                        }
                    } else {
                        // Handle error response
                        // You can handle the error scenario here
                    }
                }
            } catch (e: Exception) {
                // Handle network failure
                // You can handle the network failure scenario here
            }
        }
    }
}
