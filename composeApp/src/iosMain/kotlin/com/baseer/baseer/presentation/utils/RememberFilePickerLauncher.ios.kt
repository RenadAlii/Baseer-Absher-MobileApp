package com.baseer.baseer.presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.baseer.baseer.presentation.components.fileupload.UploadFile
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.*
import platform.Photos.PHPhotoLibrary
import platform.PhotosUI.*
import platform.UIKit.*
import platform.UniformTypeIdentifiers.UTTypeCommaSeparatedText
import platform.UniformTypeIdentifiers.UTTypeImage
import platform.UniformTypeIdentifiers.UTTypePDF
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import platform.posix.memcpy

@Composable
actual fun rememberFilePickerLauncher(
    onFilesSelected: (List<UploadFile>) -> Unit
): FilePickerState {
    val launcher = remember { FilePickerLauncher(onFilesSelected) }

    return FilePickerState(
        launchFiles = { launcher.launchDocumentPicker() },
        launchPhotos = { launcher.launchPhotoPicker() }
    )
}

@OptIn(ExperimentalForeignApi::class)
private class FilePickerLauncher(
    private val onFilesSelected: (List<UploadFile>) -> Unit
) : NSObject(), UIDocumentPickerDelegateProtocol, PHPickerViewControllerDelegateProtocol {

    fun launchDocumentPicker() {
        val types = listOf(UTTypePDF, UTTypeCommaSeparatedText, UTTypeImage)
        val picker = UIDocumentPickerViewController(forOpeningContentTypes = types, asCopy = true)
        picker.allowsMultipleSelection = true
        picker.delegate = this

        getRootViewController()?.presentViewController(picker, animated = true, completion = null)
    }

    override fun documentPicker(
        controller: UIDocumentPickerViewController,
        didPickDocumentsAtURLs: List<*>
    ) {
        val files = (didPickDocumentsAtURLs as? List<NSURL>)?.mapNotNull { url ->
            val isAccessing = url.startAccessingSecurityScopedResource()
            val file = url.toUploadFile()
            if (isAccessing) {
                url.stopAccessingSecurityScopedResource()
            }
            file
        } ?: emptyList()

        if (files.isNotEmpty()) {
            onFilesSelected(files)
        }
    }

    override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {

    }

    fun launchPhotoPicker() {
        val configuration = PHPickerConfiguration(PHPhotoLibrary.sharedPhotoLibrary())
        configuration.filter = PHPickerFilter.imagesFilter
        configuration.selectionLimit = 10

        val picker = PHPickerViewController(configuration = configuration)
        picker.delegate = this

        getRootViewController()?.presentViewController(picker, animated = true, completion = null)
    }

    override fun picker(picker: PHPickerViewController, didFinishPicking: List<*>) {
        picker.dismissViewControllerAnimated(true, completion = null)

        val results = didFinishPicking as? List<PHPickerResult> ?: return
        if (results.isEmpty()) return

        val files = mutableListOf<UploadFile>()
        var remaining = results.size

        results.forEach { result ->
            if (result.itemProvider.hasItemConformingToTypeIdentifier(UTTypeImage.identifier)) {
                result.itemProvider.loadFileRepresentationForTypeIdentifier(
                    typeIdentifier = UTTypeImage.identifier
                ) { url, error ->
                    if (url != null) {
                        copyToTempAndCreateFile(url)?.let { file ->
                            // إضافة الملف للقائمة بأمان
                            // ملاحظة: هذا الكول باك يعمل في الخلفية، لذا نجمع البيانات بحذر
                            // ولكن للإضافة للقائمة المشتركة يفضل العودة للـ Main Thread لاحقاً
                            dispatch_async(dispatch_get_main_queue()) {
                                files.add(file)
                                remaining--
                                checkIfDone(remaining, files)
                            }
                        } ?: run {
                            dispatch_async(dispatch_get_main_queue()) {
                                remaining--
                                checkIfDone(remaining, files)
                            }
                        }
                    } else {
                        dispatch_async(dispatch_get_main_queue()) {
                            remaining--
                            checkIfDone(remaining, files)
                        }
                    }
                }
            } else {
                remaining--
                checkIfDone(remaining, files)
            }
        }
    }

    private fun checkIfDone(remaining: Int, files: List<UploadFile>) {
        if (remaining == 0 && files.isNotEmpty()) {
            onFilesSelected(files.toList())
        }
    }
}

// ===== Helper Functions =====

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray {
    val length = this.length.toInt()
    if (length == 0) return ByteArray(0)

    val bytes = ByteArray(length)
    bytes.usePinned { pinned ->
        memcpy(pinned.addressOf(0), this.bytes, this.length)
    }
    return bytes
}

@OptIn(ExperimentalForeignApi::class)
private fun NSURL.toUploadFile(): UploadFile? {
    return try {
        val name = lastPathComponent ?: "unknown"
        val path = this.path ?: return null

        val fileManager = NSFileManager.defaultManager
        val attrs = fileManager.attributesOfItemAtPath(path, error = null)
        val size = (attrs?.get(NSFileSize) as? NSNumber)?.longValue ?: 0L

        val data = NSData.dataWithContentsOfFile(path)
        val bytes = data?.toByteArray()

        UploadFile(
            id = NSUUID().UUIDString,
            name = name,
            size = size,
            mimeType = getMimeType(name),
            path = path,
            bytes = bytes
        )
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun copyToTempAndCreateFile(sourceUrl: NSURL): UploadFile? {
    return try {
        val fileName = sourceUrl.lastPathComponent ?: "image_${NSUUID().UUIDString}.jpg"
        val tempDir = NSTemporaryDirectory()
        val destPath = "$tempDir$fileName"
        val destUrl = NSURL.fileURLWithPath(destPath)

        val fileManager = NSFileManager.defaultManager

        if (fileManager.fileExistsAtPath(destPath)) {
            fileManager.removeItemAtPath(destPath, error = null)
        }

        fileManager.copyItemAtURL(sourceUrl, destUrl, error = null)

        destUrl.toUploadFile()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun getMimeType(fileName: String): String {
    val ext = fileName.substringAfterLast('.', "").lowercase()
    return when (ext) {
        "csv" -> "text/csv"
        "pdf" -> "application/pdf"
        "png" -> "image/png"
        "jpg", "jpeg" -> "image/jpeg"
        "heic" -> "image/heic"
        else -> "application/octet-stream"
    }
}

private fun getRootViewController(): UIViewController? {
    val window = UIApplication.sharedApplication.windows.firstOrNull { (it as UIWindow).isKeyWindow() } as? UIWindow
        ?: UIApplication.sharedApplication.keyWindow

    return window?.rootViewController?.let { root ->
        var topController = root
        while (topController.presentedViewController != null) {
            topController = topController.presentedViewController!!
        }
        topController
    }
}