package net.frozendevelopment.openletters.usecase

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    suspend operator fun invoke(id: LetterId): LetterCellModel? = withContext(ioDispatcher) {
        val letterInfo = queries
            .letterInfo(id)
            .executeAsOneOrNull() ?: return@withContext null

        val colors = queries
            .categoryColorsForLetter(letterInfo.id)
            .executeAsList()

        return@withContext LetterCellModel(
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
