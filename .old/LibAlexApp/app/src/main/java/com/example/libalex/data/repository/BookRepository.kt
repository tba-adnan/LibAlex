package com.example.libalex.data.repository


import com.example.libalex.data.model.Book
import com.example.libalex.data.network.api.BookApiService
import com.example.libalex.data.network.retrofit.RetrofitClient

class BookRepository {
    private val apiService: BookApiService = RetrofitClient.create().create(BookApiService::class.java)

    suspend fun searchBooks(query: String): List<Book> {
        return apiService.searchBooks(query)
    }
}
