package com.baseer.baseer.presentation.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.baseer.baseer.presentation.components.fileupload.UploadFile
import java.util.*

@Composable
actual fun rememberFilePickerLauncher(
    onFilesSelected: (List<UploadFile>) -> Unit
): FilePickerState {
    val context = LocalContext.current

    // Files Picker
    val filesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        val files = uris.mapNotNull { it.toUploadFile(context) }
        if (files.isNotEmpty()) {
            onFilesSelected(files)
        }
    }

    // Photos Picker
    val photosLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        val files = uris.mapNotNull { it.toUploadFile(context) }
        if (files.isNotEmpty()) {
            onFilesSelected(files)
        }
    }

    return remember {
        FilePickerState(
            launchFiles = {
                filesLauncher.launch(arrayOf("application/pdf", "text/csv", "application/csv"))
            },
            launchPhotos = {
                photosLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        )
    }
}

private fun Uri.toUploadFile(context: Context): UploadFile? {
    return try {
        context.contentResolver.query(this, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val name = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    .takeIf { it >= 0 }?.let { cursor.getString(it) } ?: "unknown"
                val size = cursor.getColumnIndex(OpenableColumns.SIZE)
                    .takeIf { it >= 0 }?.let { cursor.getLong(it) } ?: 0L
                val mimeType = context.contentResolver.getType(this) ?: "application/octet-stream"

                UploadFile(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    size = size,
                    mimeType = mimeType,
                    path = this.toString()
                )
            } else null
        }
    } catch (e: Exception) {
        null
    }
}