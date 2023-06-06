import com.example.libalex.model.Book
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BooksApiService {
    @GET("volumes")
    fun searchBooks(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int
    ): Call<BooksApiResponse>
}

data class BooksApiResponse(
    val items: List<Book>
)
