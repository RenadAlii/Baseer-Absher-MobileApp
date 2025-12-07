package com.baseer.baseer.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class MviViewModel<State, Intent, Effect>(
    initialState: State
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()

    private val _effect = Channel<Effect>(Channel.BUFFERED)
    val effect: Flow<Effect> = _effect.receiveAsFlow()

    protected val currentState: State get() = _state.value

    fun onIntent(intent: Intent) {
        viewModelScope.launch {
            handleIntent(intent)
        }
    }

    protected abstract suspend fun handleIntent(intent: Intent)

    protected fun updateState(reducer: State.() -> State) {
        _state.update { it.reducer() }
    }

    protected fun sendEffect(effect: Effect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}