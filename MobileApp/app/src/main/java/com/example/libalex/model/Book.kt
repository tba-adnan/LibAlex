data class Book(
    val volumeInfo: VolumeInfo
)

data class VolumeInfo(
    val title: String,
    val publishedDate: String
)

data class LocalBook(
    val id: Int,
    val book_title: String, // Update to match the field name in the API response
    val author: String?,
    val page_count: Int?, // Update to match the field name in the API response
    val language: String?,
    val release_date: String?, // Update to match the field name in the API response
    val uuid: String?,
    val created_at: String?, // Update to match the field name in the API response
    val updated_at: String? // Update to match the field name in the API response
)

