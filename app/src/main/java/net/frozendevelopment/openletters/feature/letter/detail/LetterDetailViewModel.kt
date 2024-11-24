package net.frozendevelopment.openletters.feature.letter.detail

import android.net.Uri
import androidx.compose.runtime.Immutable
import kotlinx.coroutines.flow.SharingStarted
import net.frozendevelopment.openletters.data.sqldelight.migrations.Category
import net.frozendevelopment.openletters.data.sqldelight.migrations.Letter
import net.frozendevelopment.openletters.data.sqldelight.models.DocumentId
import net.frozendevelopment.openletters.data.sqldelight.models.LetterId
import net.frozendevelopment.openletters.usecase.LetterWithDetailsUseCase
import net.frozendevelopment.openletters.util.StatefulViewModel

sealed interface LetterDetailState {
    data object Loading : LetterDetailState

    data object NotFound : LetterDetailState

    @Immutable
    data class Detail(
        val letter: Letter,
        val documents: Map<DocumentId, Uri?> = emptyMap(),
        val categories: List<Category> = emptyList(),
        val threads: List<Thread> = emptyList(),
    ) : LetterDetailState
}

class LetterDetailViewModel(
    private val letterId: LetterId,
    private val letterWithDetails: LetterWithDetailsUseCase,
) : StatefulViewModel<LetterDetailState>(
        LetterDetailState.Loading,
        loadStateStrategy = SharingStarted.WhileSubscribed(1000),
    ) {
    override fun load() {
        val letter = letterWithDetails(letterId)

        if (letter == null) {
            update { LetterDetailState.NotFound }
        } else {
            update {
                LetterDetailState.Detail(
                    letter = letter.letter,
                    documents = letter.documents,
                    categories = letter.categories,
                )
            }
        }
    }
}
