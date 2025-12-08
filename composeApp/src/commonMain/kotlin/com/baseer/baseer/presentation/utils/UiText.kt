package com.baseer.baseer.presentation.utils

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

sealed class UiText {
    data class StringRes(val res: StringResource) : UiText()
    data class Plain(val value: String) : UiText()
}

@Composable
fun UiText.asString(): String = when (this) {
    is UiText.Plain -> value
    is UiText.StringRes -> stringResource(res)
}