package net.frozendevelopment.openletters.usecase

import android.net.Uri
import net.frozendevelopment.openletters.data.sqldelight.OpenLettersDB
import net.frozendevelopment.openletters.data.sqldelight.migrations.Category
import net.frozendevelopment.openletters.data.sqldelight.migrations.Document
import net.frozendevelopment.openletters.data.sqldelight.migrations.Letter
import net.frozendevelopment.openletters.data.sqldelight.migrations.Thread
import net.frozendevelopment.openletters.data.sqldelight.models.CategoryId
import net.frozendevelopment.openletters.data.sqldelight.models.DocumentId
import net.frozendevelopment.openletters.data.sqldelight.models.LetterId
import net.frozendevelopment.openletters.data.sqldelight.models.ThreadId
import net.frozendevelopment.openletters.util.DocumentManagerType

data class LetterWithDetails(
    val letter: Letter,
    val documents: Map<DocumentId, Uri?>,
    val categories: List<Category>,
    val threads: List<Thread>
)

class LetterWithDetailsUseCase(
    private val documentManager: DocumentManagerType,
    private val database: OpenLettersDB,
) {
    operator fun invoke(id: LetterId): LetterWithDetails? {
        val letterDetail = database.letterQueries.letterDetail(id).executeAsOneOrNull() ?: return null

        val categoryIds = letterDetail.categoryIds?.split(",")
            ?.map { CategoryId(it.trim()) }
            ?: emptyList()

        val categories = database.categoryQueries.categoriesByIds(categoryIds).executeAsList()

        val threadIds = letterDetail.threadIds?.split(",")
            ?.map { ThreadId(it.trim()) }
            ?: emptyList()

        val threads = database.threadQueries.threadsByIds(threadIds).executeAsList()

        val documents = database.documentQueries.documentsForLetter(id).executeAsList()
            .map { it.id }
            .associateWith { documentManager.get(it) }

        return LetterWithDetails(
            letter = Letter(
                id = letterDetail.id,
                sender = letterDetail.sender,
                recipient = letterDetail.recipient,
                body = letterDetail.body,
                created = letterDetail.created,
                lastModified = letterDetail.lastModified
            ),
            documents = documents,
            categories = categories,
            threads = threads
        )
    }
}
