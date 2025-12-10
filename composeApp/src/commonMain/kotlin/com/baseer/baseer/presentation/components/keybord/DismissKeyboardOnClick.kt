package com.baseer.baseer.presentation.components.keybord

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

@Composable
fun DismissKeyboardOnClick(
    interactionSource: MutableInteractionSource,
    content: @Composable () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = interactionSource
            ) {
                focusManager.clearFocus()
                keyboardController?.hide()
            }
    ) {
        content()
    }
}