package com.baseer.baseer.presentation.components.topSnackbar

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object SnackbarController {

    private const val DEFAULT_SNACKBAR_DELAY = 3_000L

    private val _event = mutableStateOf<SnackbarEvent>(SnackbarEvent.None)
    val event: State<SnackbarEvent> = _event

    fun CoroutineScope.sendSnackbarEvent(event: SnackbarEvent.Show) {
        launch {
            _event.value = event
        }
    }

    suspend fun dismissSnackbar(delayDuration: Long = DEFAULT_SNACKBAR_DELAY) {
        if (!hasEvent()) return
        delay(delayDuration)
        _event.value = SnackbarEvent.None
    }

    fun hasEvent(): Boolean = _event.value !is SnackbarEvent.None

    fun clear() {
        _event.value = SnackbarEvent.None
    }
}