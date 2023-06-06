import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.libalex.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = booksApiClient.searchBooks(query, maxResults)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val books = response.body()
                        if (books != null) {
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
                        }
                    } else {
                        // Handle error response
                    }
                }
            } catch (e: Exception) {
                // Handle network failure
            }
        }
    }
}
