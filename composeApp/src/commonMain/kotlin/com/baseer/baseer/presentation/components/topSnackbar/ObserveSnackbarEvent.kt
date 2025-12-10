package com.baseer.baseer.presentation.components.topSnackbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State

@Composable
fun ObserveSnackbarEvent(
    state: State<SnackbarEvent>,
    content: @Composable (SnackbarEvent) -> Unit
) {
    LaunchedEffect(state.value) {
        if (state.value is SnackbarEvent.Show) {
            SnackbarController.dismissSnackbar()
        }
    }
    content(state.value)
}