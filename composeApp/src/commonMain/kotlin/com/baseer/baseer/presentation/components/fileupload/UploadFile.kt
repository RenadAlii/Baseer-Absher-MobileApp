package com.baseer.baseer.presentation.components.fileupload

data class UploadFile(
    val id: String,
    val name: String,
    val size: Long,
    val mimeType: String,
    val path: String,
    val state: UploadState = UploadState.Pending,
    val progress: Float = 0f,
    val errorKey: String? = null,
    val bytes: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        return id == (other as UploadFile).id
    }

    override fun hashCode(): Int = id.hashCode()
}

enum class UploadState {
    Pending, Uploading, Success, Error
}

val SUPPORTED_MIME_TYPES = listOf(
    "text/csv", "application/csv", "application/pdf",
    "image/png", "image/jpeg", "image/jpg"
)

fun isSupportedFileType(mimeType: String) = SUPPORTED_MIME_TYPES.contains(mimeType.lowercase())

// Error Keys for String Resources
object FileUploadErrorKeys {
    const val FILE_TYPE_NOT_SUPPORTED = "file_type_not_supported"
    const val FILE_TOO_LARGE = "file_too_large"
    const val UPLOAD_FAILED = "upload_failed"
}