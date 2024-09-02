package net.frozendevelopment.mailshare.feature.list

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.frozendevelopment.mailshare.data.sqldelight.LetterQueries
import net.frozendevelopment.mailshare.data.sqldelight.models.LetterId
import net.frozendevelopment.mailshare.util.StatefulViewModel

@Immutable
data class LetterListState(
    val isBusy: Boolean = false,
    val letters: List<LetterId> = emptyList(),
)

class LetterListViewModel(
    private val queries: LetterQueries,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : StatefulViewModel<LetterListState>(LetterListState()) {

    init {
        viewModelScope.launch(ioDispatcher) {
            queries.allLetters()
                .asFlow()
                .mapToList(ioDispatcher)
                .collect {
                    update { copy(letters = it) }
                }
        }
    }
}
