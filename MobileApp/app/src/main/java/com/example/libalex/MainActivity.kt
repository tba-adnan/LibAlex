package com.example.libalex


import BooksApiClient
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL


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

        val floatingButton: FloatingActionButton = findViewById(R.id.fabBooks)
        floatingButton.setOnClickListener {
            startActivity(Intent(this, BookActivity::class.java))
        }
    }

    private fun searchBooks(query: String) {
        val maxResults = 25

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
                            val bookTitle = book.volumeInfo.title
                            displayBookToast(bookTitle)
                            sendPostRequest(bookTitle)
                        }
                    } else {
                        // Handle empty response
                    }
                }
            } catch (e: Exception) {
                // Handle network failure
                Log.e("MainActivity", "Error: ${e.message}", e)
            }
        }
    }

    private fun displayBookToast(bookTitle: String) {
        runOnUiThread {
            Toast.makeText(this, "Book Saved : $bookTitle", Toast.LENGTH_LONG).show()
        }
    }

    private fun sendPostRequest(bookTitle: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val url = URL("http://192.168.100.32:8000/api/v1/save?book_name=$bookTitle")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"

            try {
                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // POST request successful
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    Log.d("MainActivity", "API Response: $response")
                } else {
                    // POST request failed
                    val errorResponse = connection.errorStream.bufferedReader().use { it.readText() }
                    Log.e("MainActivity", "API Error: $errorResponse")
                }
            } catch (e: Exception) {
                // Handle network failure or other exceptions
                Log.e("MainActivity", "Error: ${e.message}", e)
            } finally {
                connection.disconnect()
            }
        }
    }
}
