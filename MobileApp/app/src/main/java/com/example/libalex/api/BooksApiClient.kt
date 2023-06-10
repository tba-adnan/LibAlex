import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BooksApiClient {
    private val googleRetrofit: Retrofit
    private val localRetrofit: Retrofit
    private val googleApiService: GoogleBooksApiService
    private val localApiService: LocalApiService

    init {
        val gson: Gson = GsonBuilder()
            .setLenient()
            .create()

        googleRetrofit = Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/books/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        localRetrofit = Retrofit.Builder()
            .baseUrl("http://192.168.100.32:8000/api/v1/") // Replace with your local API base URL
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        googleApiService = googleRetrofit.create(GoogleBooksApiService::class.java)
        localApiService = localRetrofit.create(LocalApiService::class.java)
    }

    fun searchBooks(query: String, maxResults: Int): List<Book> {
        val response = googleApiService.searchBooks(query, maxResults).execute()
        if (response.isSuccessful) {
            val booksResponse = response.body()
            Log.d("BooksApiClient", "Google API Response: $booksResponse")
            return booksResponse?.items?.map { bookResponse ->
                Book(bookResponse.volumeInfo)
            } ?: emptyList()
        } else {
            Log.e("BooksApiClient", "Google API Error: ${response.errorBody()}")
            return emptyList()
        }
    }

    fun getSavedBooks(): List<LocalBook> {
        val response = localApiService.getSavedBooks().execute()
        if (response.isSuccessful) {
            val apiResponse = response.body()
            Log.d("BooksApiClient", "Local API Response: $apiResponse")
            return apiResponse?.savings?.map { localBookResponse ->
                LocalBook(
                    id = localBookResponse.id,
                    book_title = localBookResponse.book_title, // Assign the book_title field
                    author = localBookResponse.author,
                    page_count = localBookResponse.page_count,
                    language = localBookResponse.language,
                    release_date = localBookResponse.release_date,
                    uuid = localBookResponse.uuid,
                    created_at = localBookResponse.created_at,
                    updated_at = localBookResponse.updated_at
                )
            } ?: emptyList()
        } else {
            Log.e("BooksApiClient", "Local API Error: ${response.errorBody()}")
            return emptyList()
        }
    }

}
