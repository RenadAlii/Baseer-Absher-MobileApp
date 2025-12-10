package com.baseer.baseer.presentation.utils

import androidx.compose.runtime.Composable
import com.baseer.baseer.presentation.components.fileupload.UploadFile


enum class PickerType {
    FILES,
    PHOTOS
}

data class FilePickerState(
    val launchFiles: () -> Unit,
    val launchPhotos: () -> Unit
)

@Composable
expect fun rememberFilePickerLauncher(
    onFilesSelected: (List<UploadFile>) -> Unit
): FilePickerState