package com.baseer.baseer.presentation.components.fileupload

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import baseer.composeapp.generated.resources.*
import com.baseer.baseer.presentation.utils.PickerType
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private object UploadColors {
    val Primary = Color(0xFF1B8354)
    val Surface = Color.White
    val OnSurface = Color(0xFF1F2937)
    val OnSurfaceVariant = Color(0xFF6B7280)

    val FileBackground = Color.White
    val FileBorder = Color.Transparent

    val Error = Color(0xFFB91C1C)
    val ErrorBorder = Color(0xFFEF4444)
    val ErrorBackground = Color(0xFFFEF2F2)
    val ErrorDivider = Color(0xFFFECACA)

    val Success = Color(0xFF10B981)
    val BorderDashed = Color(0xFFD2D6DB)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileUploadSection(
    files: List<UploadFile>,
    canAddMore: Boolean,
    maxFileSizeMb: Int,
    onBrowseClick: (PickerType) -> Unit,
    onRemoveFile: (String) -> Unit,
    modifier: Modifier = Modifier,
    isOptional: Boolean = true,
    allowedTypes: List<PickerType> = listOf(PickerType.PHOTOS, PickerType.FILES)
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    Column(modifier = modifier.fillMaxWidth()) {
        // Title Section
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isOptional.not()) {
                Text(
                    text = "*",
                    color = Color(0xFFD92D20),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Text(
                text = stringResource(Res.string.file_upload_title),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF161616)
            )
            if (isOptional) {
                Text(
                    text = stringResource(Res.string.file_upload_optional),
                    fontSize = 12.sp,
                    color = UploadColors.OnSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (canAddMore) {
            DropZone(
                onClick = {
                    if (allowedTypes.size == 1) {
                        onBrowseClick(allowedTypes.first())
                    } else {
                        showBottomSheet = true
                    }
                },
                enabled = true
            )
        }

        if (files.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                files.forEach { file ->
                    FileItemRow(
                        file = file,
                        maxFileSizeMb = maxFileSizeMb,
                        onRemove = { onRemoveFile(file.id) }
                    )
                }
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = UploadColors.Surface,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ) {
            PickerOptionsContent(
                showPhotos = allowedTypes.contains(PickerType.PHOTOS),
                showFiles = allowedTypes.contains(PickerType.FILES),
                onPhotoClick = {
                    showBottomSheet = false
                    onBrowseClick(PickerType.PHOTOS)
                },
                onFileClick = {
                    showBottomSheet = false
                    onBrowseClick(PickerType.FILES)
                }
            )
        }
    }
}

@Composable
private fun FileItemRow(
    file: UploadFile,
    maxFileSizeMb: Int,
    onRemove: () -> Unit
) {
    val isError = file.state == UploadState.Error
    val backgroundColor = if (isError) UploadColors.ErrorBackground else UploadColors.FileBackground
    val borderColor = if (isError) UploadColors.ErrorBorder else UploadColors.FileBorder
    val borderWidth = if (isError) 1.dp else 0.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .border(borderWidth, borderColor, RoundedCornerShape(6.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            AnimatedVisibility(file.state != UploadState.Pending) {
                Row {
                    StatusIndicator(state = file.state)
                    Spacer(modifier = Modifier.width(12.dp))
                }
            }

            // 2. File Name
            Text(
                text = truncateFileName(file.name),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = UploadColors.OnSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Image(
                painter = painterResource(Res.drawable.ic_cancel),
                contentDescription = "Remove",
                modifier = Modifier
                    .size(16.dp)
                    .clickable(onClick = onRemove)
            )
        }

        if (isError) {
            HorizontalDivider(color = UploadColors.ErrorDivider, thickness = 1.dp)

            val errorMessage = when (file.errorKey) {
                FileUploadErrorKeys.FILE_TYPE_NOT_SUPPORTED -> stringResource(Res.string.file_type_not_supported)
                FileUploadErrorKeys.FILE_TOO_LARGE -> stringResource(Res.string.file_max_size_error, maxFileSizeMb)
                else -> stringResource(Res.string.file_upload_failed)
            }

            Text(
                text = errorMessage,
                fontSize = 12.sp,
                color = UploadColors.Error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }
    }
}

private fun truncateFileName(name: String, maxLength: Int = 20): String {
    if (name.length <= maxLength) return name

    val extension = name.substringAfterLast('.', "")
    val nameWithoutExt = name.substringBeforeLast('.')
    if (extension.isEmpty() || nameWithoutExt.length < 5) {
        return name.take(maxLength) + "..."
    }
    val extLength = extension.length + 1 // +1 for dot
    val charsToShow = maxLength - extLength - 3 // 3 for "..."

    if (charsToShow <= 0) return name.take(maxLength) + "..."

    val firstPart = nameWithoutExt.take((charsToShow / 2) + 2)
    val lastPart = nameWithoutExt.takeLast(charsToShow / 2)

    return "$firstPart...$lastPart.$extension"
}

@Composable
private fun StatusIndicator(state: UploadState) {
    Box(modifier = Modifier.size(20.dp), contentAlignment = Alignment.Center) {
        when (state) {
            UploadState.Success -> {
                Icon(
                    painter = painterResource(Res.drawable.ic_check_circle),
                    contentDescription = "Success",
                    tint = UploadColors.Success,
                    modifier = Modifier.fillMaxSize()
                )
            }

            UploadState.Error -> {
                Icon(
                    painter = painterResource(Res.drawable.ic_error_circle),
                    contentDescription = "Error",
                    tint = UploadColors.Error,
                    modifier = Modifier.fillMaxSize()
                )
            }

            UploadState.Uploading -> {
                CircularProgressIndicator(
                    modifier = Modifier.padding(2.dp),
                    color = UploadColors.Primary,
                    strokeWidth = 2.5.dp,
                    trackColor = Color(0xFF161616)
                )
            }

            UploadState.Pending -> {
//                Box(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .border(1.dp, UploadColors.OnSurfaceVariant.copy(alpha = 0.5f), CircleShape)
//                )
            }
        }
    }
}

@Composable
private fun DropZone(onClick: () -> Unit, enabled: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .dashedBorder(UploadColors.BorderDashed, 8.dp)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 32.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(Res.drawable.ic_upload_cloud),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(Res.string.file_upload_drag_drop),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = UploadColors.OnSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(Res.string.file_upload_formats),
                fontSize = 12.sp,
                color = UploadColors.OnSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(Res.string.file_upload_browse),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
private fun PickerOptionsContent(
    showPhotos: Boolean,
    showFiles: Boolean,
    onPhotoClick: () -> Unit,
    onFileClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
        Text(
            text = stringResource(Res.string.file_picker_title),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = UploadColors.OnSurface,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        if (showPhotos) {
            PickerOptionItem(
                icon = { PhotoIcon(size = 24.dp, color = UploadColors.Primary) },
                title = stringResource(Res.string.file_picker_photos),
                subtitle = stringResource(Res.string.file_picker_photos_desc),
                onClick = onPhotoClick
            )
        }

        if (showPhotos && showFiles) {
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (showFiles) {
            PickerOptionItem(
                icon = { FileIcon(size = 24.dp, color = UploadColors.Primary) },
                title = stringResource(Res.string.file_picker_files),
                subtitle = stringResource(Res.string.file_picker_files_desc),
                onClick = onFileClick
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun PickerOptionItem(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF3F4F6))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = UploadColors.OnSurface
            )
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = UploadColors.OnSurfaceVariant
            )
        }
    }
}

@Composable
private fun PhotoIcon(modifier: Modifier = Modifier, size: Dp, color: Color) {
    Canvas(modifier.size(size)) {
        val s = size.toPx()
        val stroke = Stroke(2.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        // Rounded rectangle
        drawRoundRect(
            color, style = stroke, cornerRadius = CornerRadius(s * 0.15f),
            topLeft = Offset(s * 0.1f, s * 0.15f), size = Size(s * 0.8f, s * 0.7f)
        )
        // Mountain
        drawPath(Path().apply {
            moveTo(s * 0.15f, s * 0.7f)
            lineTo(s * 0.35f, s * 0.5f)
            lineTo(s * 0.5f, s * 0.6f)
            lineTo(s * 0.7f, s * 0.4f)
            lineTo(s * 0.85f, s * 0.7f)
        }, color, style = stroke)
        // Sun
        drawCircle(color, radius = s * 0.08f, center = Offset(s * 0.7f, s * 0.35f))
    }
}

@Composable
private fun FileIcon(modifier: Modifier = Modifier, size: Dp, color: Color) {
    Canvas(modifier.size(size)) {
        val s = size.toPx()
        val stroke = Stroke(2.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        // Document
        drawPath(Path().apply {
            moveTo(s * 0.25f, s * 0.1f)
            lineTo(s * 0.55f, s * 0.1f)
            lineTo(s * 0.75f, s * 0.3f)
            lineTo(s * 0.75f, s * 0.9f)
            lineTo(s * 0.25f, s * 0.9f)
            close()
        }, color, style = stroke)
        // Fold
        drawPath(Path().apply {
            moveTo(s * 0.55f, s * 0.1f)
            lineTo(s * 0.55f, s * 0.3f)
            lineTo(s * 0.75f, s * 0.3f)
        }, color, style = stroke)
    }
}

private fun Modifier.dashedBorder(color: Color, radius: Dp) = drawBehind {
    drawRoundRect(
        color = color,
        style = Stroke(
            width = 3f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f))
        ),
        cornerRadius = CornerRadius(radius.toPx())
    )
}