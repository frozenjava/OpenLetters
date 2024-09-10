package net.frozendevelopment.mailshare.feature.mail.detail

import android.net.Uri
import androidx.compose.runtime.Immutable
import net.frozendevelopment.mailshare.data.sqldelight.migrations.Category
import net.frozendevelopment.mailshare.data.sqldelight.migrations.Letter
import net.frozendevelopment.mailshare.data.sqldelight.migrations.Thread
import net.frozendevelopment.mailshare.data.sqldelight.models.DocumentId
import net.frozendevelopment.mailshare.data.sqldelight.models.LetterId
import net.frozendevelopment.mailshare.usecase.LetterWithDetailsUseCase
import net.frozendevelopment.mailshare.util.StatefulViewModel

sealed interface LetterDetailState {
    data object Loading: LetterDetailState
    data object NotFound: LetterDetailState

    @Immutable
    data class Detail(
        val letter: Letter,
        val documents: Map<DocumentId, Uri?> = emptyMap(),
        val categories: List<Category> = emptyList(),
        val threads: List<Thread> = emptyList(),
    ): LetterDetailState
}


class LetterDetailViewModel(
    private val letterWithDetails: LetterWithDetailsUseCase,
) : StatefulViewModel<LetterDetailState>(LetterDetailState.Loading) {

    suspend fun load(id: LetterId) {
        val letter = letterWithDetails(id)

        if (letter == null) {
            update { LetterDetailState.NotFound }
        } else {
            update {
                LetterDetailState.Detail(
                    letter = letter.letter,
                    documents = letter.documents,
                    categories = letter.categories,
                    threads = letter.threads
                )
            }
        }
    }
}
