package com.example.libalex.data.network.api

import com.example.libalex.data.model.Book
import retrofit2.http.GET
import retrofit2.http.Query

interface BookApiService {
    @GET("books")
    suspend fun searchBooks(@Query("query") query: String): List<Book>
}
