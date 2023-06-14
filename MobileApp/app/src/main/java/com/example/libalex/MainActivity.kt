import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.libalex.BookActivity
import com.example.libalex.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    private lateinit var booksListView: ListView

    private val booksApiClient = BooksApiClient()

    companion object {
        private const val BOOK_ACTIVITY_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        booksListView = findViewById(R.id.booksListView)

        fetchBooks()

        booksListView.onItemClickListener = AdapterView.OnItemClickListener { _, _, _, _ ->
            startActivityForResult(Intent(this, BookActivity::class.java), BOOK_ACTIVITY_REQUEST_CODE)
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
                            this@MainActivity,
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
                Log.e("MainActivity", "Error: ${e.message}", e)
            }
        }
    }

    private fun displayBookToast(bookTitle: String) {
        runOnUiThread {
            Toast.makeText(this, "Book Saved: $bookTitle", Toast.LENGTH_LONG).show()
        }
    }

    private fun sendPostRequest(bookTitle: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val url = URL("http://192.168.1.134:8000/api/v1/save?book_name=$bookTitle")
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == BOOK_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Book deleted, update the book list
            fetchBooks()
        }
    }
}
