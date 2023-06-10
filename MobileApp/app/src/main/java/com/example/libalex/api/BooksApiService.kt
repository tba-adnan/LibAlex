import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleBooksApiService {
    @GET("v1/volumes?")
    fun searchBooks(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int
    ): Call<GoogleBooksApiResponse>
}

interface LocalApiService {
    @GET("display")
    fun getSavedBooks(): Call<LocalApiApiResponse>
}

data class GoogleBooksApiResponse(
    val items: List<Book>
)

data class LocalApiApiResponse(
    val savings: List<LocalBook>
)
