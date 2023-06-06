package com.example.libalex
import BooksApiClient
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.libalex.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
        val books = booksApiClient.searchBooks(query, maxResults)

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            books.map { it.title }
        )
        booksListView.adapter = adapter

        booksListView.setOnItemClickListener { _, _, position, _ ->
            val book = books[position]
            // Handle book selection
        }
    }
}
