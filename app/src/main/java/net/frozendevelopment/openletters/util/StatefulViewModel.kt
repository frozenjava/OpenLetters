package net.frozendevelopment.openletters.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

open class StatefulViewModel<TState>(
    initialState: TState
): ViewModel() {
    private val _stateFlow = MutableStateFlow(initialState)
    val stateFlow: StateFlow<TState> = _stateFlow
        .onStart { load() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            initialState
        )

    val state: TState
        get() = _stateFlow.value

    open fun load() {}

    protected fun update(newState: TState.() -> TState) {
        _stateFlow.update(newState)
    }

}
