package net.frozendevelopment.mailshare.feature.list

import androidx.compose.runtime.Immutable
import net.frozendevelopment.mailshare.data.sqldelight.LetterQueries
import net.frozendevelopment.mailshare.util.StatefulViewModel

@Immutable
data class LetterListState(
    val isBusy: Boolean = false,
    val letters: List<String> = emptyList(),
)

class LetterListViewModel(
    private val queries: LetterQueries
) : StatefulViewModel<LetterListState>(LetterListState()) {

    init {
        println(queries.allLetters().executeAsList())
    }

}
