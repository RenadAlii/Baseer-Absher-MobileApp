package com.baseer.baseer.presentation.components.textField

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.baseer.baseer.presentation.components.inlineHelper.HelperMessageType
import com.baseer.baseer.presentation.components.inlineHelper.InlineHelperMessage
import org.jetbrains.compose.ui.tooling.preview.Preview

private val FieldBackgroundDefault = Color.White
private val FieldBorderDefault = Color(0xFF9DA4AE)
private val FieldBorderError = Color(0xFFD92D20)
private val FieldTextLabel = Color(0xFF161616)
private val FieldTextPlaceholder = Color(0xFF9CA3AF)
private val FieldTextFilled = Color(0xFF111827)

private val LabelTextStyle = TextStyle(
    fontSize = 12.sp,
    fontWeight = FontWeight.SemiBold,
    color = FieldTextLabel
)

private val FieldTextStyle = TextStyle(
    fontSize = 14.sp,
    fontWeight = FontWeight.Normal,
    color = FieldTextFilled,
    textAlign = TextAlign.Start
)


@Composable
fun LabeledOutlinedTextFieldInput(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    isError: Boolean = false,
    required: Boolean = false,
    singleLine: Boolean = true,
    minLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    autoAlignByLanguage: Boolean = false,
    helperMessage: String = "",
    helperType: HelperMessageType = HelperMessageType.NONE,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val showPlaceHolder = (!placeholder.isNullOrEmpty() && value.isEmpty())
        val baseText = if (showPlaceHolder) placeholder else value

        val hasArabicChars = baseText.any { ch ->
            (ch in '\u0600'..'\u06FF') ||
                    (ch in '\u0750'..'\u077F') ||
                    (ch in '\u08A0'..'\u08FF')
        }

        val textDirection = when {
            !autoAlignByLanguage -> TextDirection.ContentOrLtr
            hasArabicChars -> TextDirection.Rtl
            else -> TextDirection.Ltr
        }

        val textColor =
            if (showPlaceHolder) {
                FieldTextPlaceholder
            } else {
                FieldTextFilled
            }

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            if (required) {
                Text(
                    text = "*",
                    style = LabelTextStyle.copy(color = FieldBorderError)
                )
            }
            Text(
                text = title,
                style = LabelTextStyle
            )
        }

        OutlinedTextField(
            modifier = Modifier
                .padding(top = 4.dp)
                .fillMaxWidth()
                .background(FieldBackgroundDefault),
            value = if (showPlaceHolder) "" else value,
            onValueChange = onValueChange,
            singleLine = singleLine,
            minLines = minLines,
            textStyle = FieldTextStyle.copy(
                color = textColor,
                textDirection = textDirection
            ),
            shape = RoundedCornerShape(8.dp),
            placeholder = {
                if (!placeholder.isNullOrEmpty()) {
                    Text(
                        text = placeholder,
                        style = FieldTextStyle.copy(
                            color = FieldTextPlaceholder,
                            textDirection = textDirection
                        )
                    )
                }
            },
            isError = isError || helperType == HelperMessageType.ERROR,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (isError || helperType == HelperMessageType.ERROR) {
                    FieldBorderError
                } else {
                    FieldBorderDefault
                },
                unfocusedBorderColor = if (isError || helperType == HelperMessageType.ERROR) {
                    FieldBorderError
                } else {
                    FieldBorderDefault
                },
                cursorColor = FieldTextFilled
            )
        )

        InlineHelperMessage(
            message = helperMessage,
            type = helperType,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview()
@Composable
private fun LabeledOutlinedTextFieldInputPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(FieldBackgroundDefault)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        LabeledOutlinedTextFieldInput(
            title = "الاسم الاول",
            value = "",
            onValueChange = {},
            placeholder = "ادخل الاسم الاول",
            required = true,
            autoAlignByLanguage = true,
            helperMessage = "نص مساعد",
            helperType = HelperMessageType.INFO
        )

        LabeledOutlinedTextFieldInput(
            title = "الاسم الاول",
            value = "Renad",
            onValueChange = {},
            placeholder = "Enter first name",
            autoAlignByLanguage = true,
            helperMessage = "هنا تكتب رسالة الخطأ",
            helperType = HelperMessageType.ERROR
        )
    }
}