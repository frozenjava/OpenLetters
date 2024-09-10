package net.frozendevelopment.openletters.util

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

open class StatefulViewModel<TState>(
    initialState: TState
): ViewModel() {
    private val _stateFlow = MutableStateFlow(initialState)
    val stateFlow: StateFlow<TState>
        get() = _stateFlow

    val state: TState
        get() = _stateFlow.value

    protected suspend fun update(newState: TState.() -> TState) {
        _stateFlow.update(newState)
    }

}
