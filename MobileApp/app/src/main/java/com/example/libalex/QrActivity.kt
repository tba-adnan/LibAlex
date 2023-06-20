package com.example.libalex

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.google.zxing.ResultPointCallback
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

data class LocalBook(
    val id: Int,
    val bookTitle: String,
    val author: String,
    val pageCount: Int?,
    val language: String,
    val releaseDate: String,
    val uuid: String,
    val createdAt: String,
    val updatedAt: String
)

interface LocalApiService {
    @GET("similar/{uuid}")
    fun getSimilarBooks(@Path("uuid") uuid: String): Call<ResponseBody>
}

class QrActivity : AppCompatActivity(), DecoratedBarcodeView.TorchListener {
    private lateinit var scannerView: DecoratedBarcodeView
    private lateinit var booksListView: ListView
    private lateinit var bookAdapter: ArrayAdapter<String>
    private val retrofit: Retrofit
    private val localApiService: LocalApiService
    private var bookListActivityStarted = false // Flag to track if BookListActivity is already started
    private var toastDisplayed = false // Flag to track if the toast has been displayed

    init {
        retrofit = Retrofit.Builder()
            .baseUrl("http://8f55-41-142-111-6.ngrok-free.app/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        localApiService = retrofit.create(LocalApiService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr)
        scannerView = findViewById(R.id.barcode_scanner)
        booksListView = findViewById(R.id.booksListView)
        bookAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        booksListView.adapter = bookAdapter

        checkCameraPermission()
    }

    override fun onResume() {
        super.onResume()
        scannerView.resume()
        scannerView.setTorchListener(this)
        scannerView.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                result?.let {
                    if (it.barcodeFormat == BarcodeFormat.QR_CODE) {
                        val uuid = it.text
                        retrieveBooksByUuid(uuid) { responseData ->
                            if (!toastDisplayed) {
                                Toast.makeText(this@QrActivity, "QR valide ✔: $uuid", Toast.LENGTH_SHORT).show()
                                toastDisplayed = true
                            }
                            navigateToBookListActivity(uuid, responseData)
                        }
                    }
                }
            }

            override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {}
        })
    }

    override fun onPause() {
        super.onPause()
        scannerView.pause()
    }

    private fun checkCameraPermission() {
        val permission = Manifest.permission.CAMERA
        val grant = PackageManager.PERMISSION_GRANTED
        val permissions = arrayOf(permission)

        if (ContextCompat.checkSelfPermission(this, permission) != grant) {
            ActivityCompat.requestPermissions(this, permissions, CAMERA_PERMISSION_REQUEST_CODE)
        }
    }

    override fun onTorchOn() {
        Toast.makeText(this@QrActivity, "I <3 \uD83E\uDDC1", Toast.LENGTH_SHORT).show()
    }

    override fun onTorchOff() {
        // Torch is turned off
    }

    private fun retrieveBooksByUuid(uuid: String, callback: (String) -> Unit) {
        val call = localApiService.getSimilarBooks(uuid)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseData = response.body()?.string()
                    responseData?.let {
                        callback(it) // Pass the response data to the callback
                    }
                } else {
                    // Handle API error
                    Log.e("QrActivity", "API Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Handle network failure
                Log.e("QrActivity", "Error: ${t.message}", t)
            }
        })
    }

    private fun parseBooksResponse(response: String): List<LocalBook> {
        val books = mutableListOf<LocalBook>()
        try {
            val jsonObject = JSONObject(response)
            val success = jsonObject.getBoolean("success")
            if (success) {
                val data = jsonObject.getJSONArray("data")
                for (i in 0 until data.length()) {
                    val bookObject = data.getJSONObject(i)
                    val id = bookObject.getInt("id")
                    val bookTitle = bookObject.getString("book_title")
                    val author = bookObject.getString("author")
                    val pageCount = bookObject.getString("page_count").toIntOrNull()
                    val language = bookObject.getString("language")
                    val releaseDate = bookObject.getString("release_date")
                    val uuid = bookObject.getString("uuid")
                    val createdAt = bookObject.getString("created_at")
                    val updatedAt = bookObject.getString("updated_at")

                    val book = LocalBook(
                        id,
                        bookTitle,
                        author,
                        pageCount,
                        language,
                        releaseDate,
                        uuid,
                        createdAt,
                        updatedAt
                    )
                    books.add(book)
                }
            } else {
                // Handle unsuccessful response
                Log.e("QrActivity", "Unsuccessful response: $response")
            }
        } catch (e: Exception) {
            // Handle parsing exception
            Log.e("QrActivity", "Error parsing response: ${e.message}", e)
        }
        return books
    }

    private fun displayBooks(books: List<LocalBook>) {
        val bookTitles = books.map { it.bookTitle }
        runOnUiThread {
            bookAdapter.clear()
            bookAdapter.addAll(bookTitles)
            bookAdapter.notifyDataSetChanged()
        }
    }

    private fun navigateToBookListActivity(uuid: String, responseData: String) {
        if (!bookListActivityStarted) {
            // Create and start BookListActivity only if it's not already started
            bookListActivityStarted = true

            val intent = Intent(this, BookListActivity::class.java)
            intent.putExtra("uuid", uuid)
            intent.putExtra("responseData", responseData)
            startActivity(intent)
        }
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 123
    }
}


