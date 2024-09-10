package net.frozendevelopment.mailshare.usecase

import androidx.compose.ui.graphics.Color
import net.frozendevelopment.mailshare.data.sqldelight.LetterInfo
import net.frozendevelopment.mailshare.data.sqldelight.LetterQueries
import net.frozendevelopment.mailshare.data.sqldelight.models.LetterId

data class MetaLetter(
    val id: LetterId,
    val sender: String?,
    val recipient: String?,
    val body: String?,
    val created: Long,
    val lastModified: Long,
    val categoryColors: List<Color>,
)

class MetaLetterUseCase(
    private val queries: LetterQueries
) {
    fun load(id: LetterId): MetaLetter? {
        val letterInfo = queries.letterInfo(id)
            .executeAsOneOrNull() ?: return null

        return MetaLetter(
            id = letterInfo.id,
            sender = letterInfo.sender,
            recipient = letterInfo.recipient,
            body = letterInfo.body,
            created = letterInfo.created,
            lastModified = letterInfo.lastModified,
            categoryColors = letterInfo.categoryColors
                ?.split(",")
                ?.map { Color(it.toLong()).copy(alpha = 1f) } ?: emptyList()
        )
    }
}
