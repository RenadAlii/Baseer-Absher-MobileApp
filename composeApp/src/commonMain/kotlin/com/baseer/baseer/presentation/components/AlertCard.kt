package com.baseer.baseer.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import baseer.composeapp.generated.resources.Res
import baseer.composeapp.generated.resources.ic_close
import baseer.composeapp.generated.resources.ic_info
import com.baseer.baseer.presentation.utils.extensions.noRippleEffect
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

enum class AlertType {
    INFO,
    DANGER
}

@Composable
fun AlertCard(
    title: String,
    description: String,
    type: AlertType = AlertType.INFO,
    modifier: Modifier = Modifier,
    actions: List<String> = emptyList(),
    onActionClicked: ((String) -> Unit)? = null,
    showClose: Boolean = true,
    onClose: (() -> Unit)? = null,
    icon: DrawableResource = Res.drawable.ic_info
) {
    val borderColor = Color(0xFFE5E7EB)
    val backgroundColor = Color.White

    val sideColor = when (type) {
        AlertType.INFO -> Color(0xD7247BFF)
        AlertType.DANGER -> Color(0xD0DC2626)
    }

    val circleBg = when (type) {
        AlertType.INFO -> Color(0x7EE8F0FF)
        AlertType.DANGER -> Color(0x8FFFE8E5)
    }

    val titleTextStyle = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF111827)
    )

    val descriptionStyle = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        color = Color(0xFF4B5563)
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .height(IntrinsicSize.Min)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {

            // Colored Side Bar
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .fillMaxHeight()
                    .background(sideColor)
            )

            // Icon Circle
            Box(
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp)
                    .size(36.dp)
                    .background(circleBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = sideColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    text = title,
                    style = titleTextStyle,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(8.dp))

                AnimatedVisibility(description.isNotBlank()) {
                    Text(
                        text = description,
                        style = descriptionStyle,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                }

                // Actions Row
                if (actions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        actions.forEach { action ->
                            Text(
                                text = action,
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF374151)
                                ),
                                modifier = Modifier
                                    .padding(start = 16.dp)
                                    .noRippleEffect {
                                        onActionClicked?.invoke(action)
                                    }
                            )
                        }
                    }
                }
            }

            // Close Button (Optional)
            if (showClose && onClose != null) {
                Box(
                    modifier = Modifier
                        .padding(end = 16.dp, top = 16.dp)
                        .size(32.dp)
                        .background(Color(0xFFF3F4F6), CircleShape)
                        .noRippleEffect { onClose() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_close),
                        contentDescription = null,
                        tint = Color(0xFF6B7280),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
fun AlertCardInfoPreview() {
    Column(Modifier.padding(16.dp)) {
        AlertCard(
            title = "عنوان رسالة الإشعار أو التنبيه",
            description = "يكتب المحتوى الإضافي هنا في حال أن رسالة الإشعار أو التنبيه تحتاج إلى شرح أو تفصيل.",
            type = AlertType.INFO,
            actions = listOf("إجراء", "إجراء"),
            onActionClicked = {},
            showClose = true
        )
        Spacer(modifier = Modifier.height(24.dp))
        AlertCard(
            title = "عنوان رسالة الإشعار أو التنبيه",
            description = "يكتب المحتوى الإضافي هنا في حال أن رسالة الإشعار أو التنبيه تحتاج إلى شرح أو تفصيل.",
            type = AlertType.DANGER,
            actions = listOf("إجراء"),
            onActionClicked = {},
            showClose = false
        )
    }
}