import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BooksApiClient {
    private val retrofit: Retrofit
    private val apiService: BooksApiService

    init {
        val gson: Gson = GsonBuilder()
            .setLenient()
            .create()

        retrofit = Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/books/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        apiService = retrofit.create(BooksApiService::class.java)
    }

    fun searchBooks(query: String, maxResults: Int): List<Book> {
        val response = apiService.searchBooks(query, maxResults).execute()
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


}
