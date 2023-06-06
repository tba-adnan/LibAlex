import com.example.libalex.model.Book
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BooksApiClient {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://www.googleapis.com/books/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService: BooksApiService = retrofit.create(BooksApiService::class.java)

    fun searchBooks(query: String, maxResults: Int): List<Book> {
        val response = apiService.searchBooks(query, maxResults).execute()
        return if (response.isSuccessful) {
            response.body()?.items ?: emptyList()
        } else {
            emptyList()
        }
    }
}
