package net.frozendevelopment.openletters.feature.letter.peek

import android.net.Uri
import androidx.compose.runtime.Immutable
import net.frozendevelopment.openletters.data.sqldelight.migrations.Category
import net.frozendevelopment.openletters.data.sqldelight.models.DocumentId
import net.frozendevelopment.openletters.data.sqldelight.models.LetterId
import net.frozendevelopment.openletters.data.sqldelight.models.ReminderId
import net.frozendevelopment.openletters.usecase.LetterWithDetailsUseCase
import net.frozendevelopment.openletters.util.StatefulViewModel

@Immutable
data class LetterPeekState(
    val transcript: String? = null,
    val sender: String? = null,
    val recipient: String? = null,
    val documents: Map<DocumentId, Uri?> = emptyMap(),
    val selectedCategories: List<Category> = emptyList(),
    val reminders: List<ReminderId> = emptyList(),
) {
    val pagerCount: Int
        get() {
            var count = 0

            if (!transcript.isNullOrBlank()) {
                count++
            }

            if (
                !sender.isNullOrBlank() ||
                !recipient.isNullOrBlank() ||
                selectedCategories.isNotEmpty()
            ) {
                count++
            }

            count += documents.size

            return count
        }
}

class LetterPeekViewModel(
    private val letterId: LetterId,
    private val letterWithDetails: LetterWithDetailsUseCase
) : StatefulViewModel<LetterPeekState>(LetterPeekState()) {
    override fun load() {
        // TODO: Handle this not loading, although it should never happen in this context
        val details = letterWithDetails(letterId) ?: return

        update { copy(
            transcript = details.letter.body,
            sender = details.letter.sender,
            recipient = details.letter.recipient,
            documents = details.documents,
            selectedCategories = details.categories,
            reminders = details.reminders
        )}
    }
}
