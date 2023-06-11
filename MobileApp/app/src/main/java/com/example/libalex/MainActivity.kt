
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.libalex.BookActivity
import com.example.libalex.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == BOOK_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Book deleted, update the book list
            fetchBooks()
        }
    }
}
