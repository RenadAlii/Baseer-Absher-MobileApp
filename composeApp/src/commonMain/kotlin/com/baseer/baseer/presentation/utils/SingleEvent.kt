package com.baseer.baseer.presentation.utils

data class SingleEvent<out T>(
    val value: T
) {
    private var isHandled: Boolean = false
    fun getContentIfNotHandled(): T? {
        return if (isHandled) {
            null
        } else {
            isHandled = true
            value
        }
    }

    fun handel(collector: (T) -> Unit) {
        getContentIfNotHandled()?.let(collector)
    }
}
