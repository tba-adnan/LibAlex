
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BooksApiClient {
    private val retrofit: Retrofit
    private val googleApiService: GoogleBooksApiService
    private val localApiService: LocalApiService

    init {
        val gson: Gson = GsonBuilder()
            .setLenient()
            .create()

        retrofit = Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/books/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        googleApiService = retrofit.create(GoogleBooksApiService::class.java)

        val localRetrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.134:8000/api/v1/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        localApiService = localRetrofit.create(LocalApiService::class.java)
    }

    fun searchBooks(query: String, maxResults: Int): List<Book> {
        val response = googleApiService.searchBooks(query, maxResults).execute()
        if (response.isSuccessful) {
            val booksResponse = response.body()
            Log.d("BooksApiClient", "API Response: $booksResponse")
            return booksResponse?.items?.map { bookResponse ->
                Book(bookResponse.volumeInfo)
            } ?: emptyList()
        } else {
            Log.e("BooksApiClient", "API Error: ${response.errorBody()}")
            return emptyList()
        }
    }

    fun getSavedBooks(): List<LocalBook> {
        val response = localApiService.getSavedBooks().execute()
        if (response.isSuccessful) {
            val apiResponse = response.body()
            Log.d("BooksApiClient", "Local API Response: $apiResponse")
            return apiResponse?.savings ?: emptyList()
        } else {
            Log.e("BooksApiClient", "Local API Error: ${response.errorBody()}")
            return emptyList()
        }
    }

    fun deleteBook(bookId: Int): Response<Unit> {
        return localApiService.deleteBook(bookId).execute()
    }
}
