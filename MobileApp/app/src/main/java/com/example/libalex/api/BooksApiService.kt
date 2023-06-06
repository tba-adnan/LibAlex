import com.example.libalex.model.Book
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BooksApiService {
    @GET("v1/volumes")
    fun searchBooks(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int
    ): Call<BooksResponse>
}

data class BooksResponse(
    val items: List<Book>
)
