package net.frozendevelopment.openletters.usecase

import android.net.Uri
import net.frozendevelopment.openletters.data.sqldelight.OpenLettersDB
import net.frozendevelopment.openletters.data.sqldelight.models.CategoryId
import net.frozendevelopment.openletters.data.sqldelight.models.DocumentId
import net.frozendevelopment.openletters.data.sqldelight.models.LetterId
import net.frozendevelopment.openletters.util.DocumentManagerType
import java.time.LocalDateTime

class CreateLetterUseCase(
    private val documentManager: DocumentManagerType,
    private val database: OpenLettersDB,
    private val now: () -> LocalDateTime = { LocalDateTime.now() },
) {
    operator fun invoke(
        sender: String?,
        recipient: String?,
        transcript: String?,
        documents: Map<DocumentId, Uri>,
        categories: List<CategoryId>,
        letterId: LetterId = LetterId.random(),
    ) {
        val currentTime = now()

        database.transaction {
            // insert the letter into the database
            database.letterQueries.upsertLetter(
                id = letterId,
                sender = sender,
                recipient = recipient,
                body = transcript,
                created = currentTime,
                lastModified = currentTime,
            )

            // find currently existing documents for this letter
            val existingDocuments =
                database.documentQueries.documentsForLetter(letterId)
                    .executeAsList()
                    .map { it.id }

            // remove all documents that are not in the new `documents` map
            for (documentId in existingDocuments) {
                if (documentId !in documents) {
                    database.documentQueries.deleteDocument(documentId)
                    documentManager.delete(documentId)
                }
            }

            // persist any new documents
            for ((documentId, documentUri) in documents) {
                // skip any documents that have already been persisted
                if (documentId in existingDocuments) {
                    continue
                }

                documentManager.persist(documentUri, documentId)
                database.documentQueries.insertDocument(id = documentId, letterId = letterId)
            }

            // find existing categories for the letter
            val existingCategories =
                database.letterQueries.categoriesForLetter(letterId)
                    .executeAsList()

            // remove all categories that are not in the new `categories` list
            for (categoryId in existingCategories) {
                if (categoryId !in categories) {
                    database.letterQueries.untagCategoryFromLetter(letterId = letterId, categoryId = categoryId)
                }
            }

            // tag the letter with the new categories
            for (category in categories) {
                // skip any categories that have already been tagged
                if (category in existingCategories) {
                    continue
                }

                database.letterQueries.tagLetterWithCategory(letterId = letterId, categoryId = category)
            }
        }
    }
}
