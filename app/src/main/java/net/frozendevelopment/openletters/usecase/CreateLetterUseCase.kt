package net.frozendevelopment.openletters.usecase

import android.content.Context
import android.net.Uri
import net.frozendevelopment.openletters.data.sqldelight.OpenLettersDB
import net.frozendevelopment.openletters.data.sqldelight.models.CategoryId
import net.frozendevelopment.openletters.data.sqldelight.models.DocumentId
import net.frozendevelopment.openletters.data.sqldelight.models.LetterId
import net.frozendevelopment.openletters.data.sqldelight.models.ThreadId
import net.frozendevelopment.openletters.util.DocumentManagerType
import net.frozendevelopment.openletters.util.TextExtractorType
import java.time.Instant

class CreateLetterUseCase(
    private val documentManager: DocumentManagerType,
    private val textExtractor: TextExtractorType,
    private val database: OpenLettersDB,
    private val now: () -> Long = { Instant.now().epochSecond },
) {
    suspend operator fun invoke(
        sender: String?,
        recipient: String?,
        documents: List<Uri>,
        categories: List<CategoryId>,
        threads: List<ThreadId>,
    ) {
        val letterId = LetterId.random()
        val currentTime = now()

        val extractedText = documents
            .mapNotNull { textExtractor.extractFromImage(it) }
            .filter { it.isNotBlank() }
            .joinToString("\n\n")

        database.transaction {
            database.letterQueries.upsertLetter(
                id = letterId,
                sender = sender,
                recipient = recipient,
                body = extractedText,
                created = currentTime,
                lastModified = currentTime,
            )

            for (document in documents) {
                val docId = DocumentId.random()
                database.documentQueries.insertDocument(id = docId, letterId = letterId)
                documentManager.persist(document, docId)
            }

            for (category in categories) {
                database.letterQueries.tagLetterWithCategory(letterId = letterId, categoryId = category)
            }

            for (thread in threads) {
                database.letterQueries.addLetterToThread(letterId = letterId, threadId = thread)
            }
        }
    }
}
