package net.frozendevelopment.openletters.usecase

import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.coroutineScope
import net.frozendevelopment.openletters.data.sqldelight.LetterInfo
import net.frozendevelopment.openletters.data.sqldelight.LetterQueries
import net.frozendevelopment.openletters.data.sqldelight.models.LetterId
import java.time.LocalDateTime

data class MetaLetter(
    val id: LetterId,
    val sender: String?,
    val recipient: String?,
    val body: String?,
    val created: LocalDateTime,
    val lastModified: LocalDateTime,
    val categoryColors: List<Color>,
)

class MetaLetterUseCase(
    private val queries: LetterQueries
) {
    operator fun invoke(id: LetterId): MetaLetter? {
        val letterInfo = queries.letterInfo(id)
            .executeAsOneOrNull() ?: return null

        val colors = queries
            .categoryColorsForLetter(letterInfo.id)
            .executeAsList()

        return MetaLetter(
            id = letterInfo.id,
            sender = letterInfo.sender,
            recipient = letterInfo.recipient,
            body = letterInfo.body,
            created = letterInfo.created,
            lastModified = letterInfo.lastModified,
            categoryColors = colors
        )
    }
}
