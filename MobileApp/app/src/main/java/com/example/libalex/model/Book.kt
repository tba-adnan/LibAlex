data class Book(
    val volumeInfo: VolumeInfo
)

data class VolumeInfo(
    val title: String,
    val publishedDate: String
)

data class LocalBook(
    val id: Int,
    val book_title: String,
    val author: String?,
    val page_count: Int?,
    val language: String?,
    val release_date: String?,
    val uuid: String?,
    val created_at: String?,
    val updated_at: String?
)

