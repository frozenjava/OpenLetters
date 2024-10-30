package net.frozendevelopment.openletters.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

open class StatefulViewModel<TState>(
    initialState: TState,
    loadStateStrategy: SharingStarted = SharingStarted.WhileSubscribed(5000),
) : ViewModel() {
    private val _stateFlow: MutableStateFlow<TState> = MutableStateFlow(initialState)

    val stateFlow: StateFlow<TState> by lazy {
        _stateFlow
            .onStart { load() }
            .stateIn(
                viewModelScope,
                loadStateStrategy,
                initialState
            )
    }

    val state: TState
        get() = _stateFlow.value

    protected open fun load() {}

    protected fun update(newState: TState.() -> TState) {
        _stateFlow.update(newState)
    }
}