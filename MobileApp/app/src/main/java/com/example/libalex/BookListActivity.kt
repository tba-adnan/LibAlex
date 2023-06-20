package com.example.libalex

import LocalBook
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONObject

class BookListActivity : AppCompatActivity() {
    private lateinit var booksListView: ListView
    private lateinit var bookAdapter: ArrayAdapter<String>
    private lateinit var books: List<LocalBook>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_list)

        val uuid = intent.getStringExtra("uuid") ?: ""
        val responseData = intent.getStringExtra("responseData") ?: ""

        books = parseBooksResponse(responseData)
        val bookTitles = books.map { it.book_title }

        booksListView = findViewById(R.id.booksListView)
        bookAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, bookTitles)
        booksListView.adapter = bookAdapter

        booksListView.setOnItemClickListener { _, _, position, _ ->
            val bookName = books[position].book_title
            val searchQuery = Uri.encode(bookName)
            val searchUrl = "https://www.google.com/search?tbm=bks&q=$searchQuery"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(searchUrl))
            startActivity(intent)
        }

        booksListView.setOnItemLongClickListener { _, _, position, _ ->
            val bookName = books[position].book_title
            displayToast("I ❤ cupcake \uD83E\uDDC1")
            true
        }
    }

    private fun parseBooksResponse(response: String): List<LocalBook> {
        val books = mutableListOf<LocalBook>()
        try {
            val jsonObject = JSONObject(response)
            val data = jsonObject.getJSONArray("data")

            for (i in 0 until data.length()) {
                val bookObject = data.getJSONObject(i)
                val id = bookObject.getInt("id")
                val bookTitle = bookObject.getString("book_title")
                val author = bookObject.getString("author")
                val pageCount = bookObject.getString("page_count").toIntOrNull()
                val language = bookObject.getString("language")
                val releaseDate = bookObject.getString("release_date")
                val uuid = bookObject.getString("uuid")
                val createdAt = bookObject.getString("created_at")
                val updatedAt = bookObject.getString("updated_at")

                val book = LocalBook(
                    id,
                    bookTitle,
                    author,
                    pageCount,
                    language,
                    releaseDate,
                    uuid,
                    createdAt,
                    updatedAt
                )
                books.add(book)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return books
    }

    private fun displayToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}



