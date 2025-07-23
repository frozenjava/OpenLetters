package net.frozendevelopment.openletters.usecase

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import net.frozendevelopment.openletters.data.sqldelight.LetterQueries
import net.frozendevelopment.openletters.data.sqldelight.models.LetterId
import java.time.LocalDateTime

@Immutable
data class LetterCellModel(
    val id: LetterId,
    val sender: String?,
    val recipient: String?,
    val body: String?,
    val created: LocalDateTime,
    val lastModified: LocalDateTime,
    val categoryColors: List<Color>,
)

class LetterCellUseCase(
    private val queries: LetterQueries,
) {
    operator fun invoke(id: LetterId): LetterCellModel? {
        val letterInfo =
            queries
                .letterInfo(id)
                .executeAsOneOrNull() ?: return null

        val colors =
            queries
                .categoryColorsForLetter(letterInfo.id)
                .executeAsList()

        return LetterCellModel(
            id = letterInfo.id,
            sender = letterInfo.sender,
            recipient = letterInfo.recipient,
            body = letterInfo.body,
            created = letterInfo.created,
            lastModified = letterInfo.lastModified,
            categoryColors = colors,
        )
    }
}
