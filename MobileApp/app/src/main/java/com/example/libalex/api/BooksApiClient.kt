// BooksApiClient.kt

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

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
            .baseUrl("http://f5bf-41-140-95-93.ngrok-free.app/api/v1/")
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

    suspend fun getBookSummary(bookTitle: String): String {
        val client = OkHttpClient()

        val jsonObject = JSONObject().apply {
            put("model", "gpt-3.5-turbo")
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", "Summarize the book in 50 word or less and in french: $bookTitle")
                })
            })
            put("temperature", 0.7)
        }

        val requestBody = RequestBody.create(
            "application/json".toMediaTypeOrNull(),
            jsonObject.toString()
        )

        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .header("Authorization", "Bearer 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")
            .post(requestBody)
            .build()

        val response = client.newCall(request).execute()

        if (response.isSuccessful) {
            val responseBody = response.body?.string()
            // Parse the response and extract the book summary
            return extractSummaryFromResponse(responseBody)
        } else {
            throw IOException("API request failed: ${response.code} ${response.message}")
        }
    }


    private fun extractSummaryFromResponse(responseBody: String?): String {
        val jsonObject = JSONObject(responseBody)
        val choices = jsonObject.getJSONArray("choices")
        if (choices.length() > 0) {
            val message = choices.getJSONObject(0).getJSONObject("message")
            return message.getString("content")
        }
        return ""
    }
}
