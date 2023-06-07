package com.example.libalex

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BookService {
    @GET("volumes")
    fun searchBooks(@Query("q") query: String): Call<BookSearchResponse>
}
