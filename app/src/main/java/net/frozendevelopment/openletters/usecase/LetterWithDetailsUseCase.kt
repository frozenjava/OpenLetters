package net.frozendevelopment.openletters.usecase

import android.net.Uri
import net.frozendevelopment.openletters.data.sqldelight.OpenLettersDB
import net.frozendevelopment.openletters.data.sqldelight.migrations.Category
import net.frozendevelopment.openletters.data.sqldelight.migrations.Letter
import net.frozendevelopment.openletters.data.sqldelight.models.CategoryId
import net.frozendevelopment.openletters.data.sqldelight.models.DocumentId
import net.frozendevelopment.openletters.data.sqldelight.models.LetterId
import net.frozendevelopment.openletters.data.sqldelight.models.ReminderId
import net.frozendevelopment.openletters.util.DocumentManagerType

data class LetterWithDetails(
    val letter: Letter,
    val documents: Map<DocumentId, Uri>,
    val categories: List<Category>,
    val reminders: List<ReminderId>,
)

class LetterWithDetailsUseCase(
    private val documentManager: DocumentManagerType,
    private val database: OpenLettersDB,
) {
    operator fun invoke(id: LetterId): LetterWithDetails? {
        val letterDetail = database.letterQueries.letterDetail(id).executeAsOneOrNull() ?: return null

        val categoryIds =
            letterDetail.categoryIds
                ?.split(",")
                ?.map { CategoryId(it.trim()) }
                ?: emptyList()

        val categories = database.categoryQueries.categoriesByIds(categoryIds).executeAsList()

        val documents =
            database.documentQueries
                .documentsForLetter(id)
                .executeAsList()
                .map { it.id }
                .associateWith {
                    documentManager.get(it) ?: Uri.EMPTY
                }

        val reminders = database.reminderQueries.remindersForLetter(id).executeAsList()

        return LetterWithDetails(
            letter =
                Letter(
                    id = letterDetail.id,
                    sender = letterDetail.sender,
                    recipient = letterDetail.recipient,
                    body = letterDetail.body,
                    created = letterDetail.created,
                    lastModified = letterDetail.lastModified,
                ),
            documents = documents,
            categories = categories,
            reminders = reminders,
        )
    }
}
