package net.frozendevelopment.mailshare.usecase

import android.net.Uri
import net.frozendevelopment.mailshare.data.sqldelight.MailShareDB
import net.frozendevelopment.mailshare.data.sqldelight.models.CategoryId
import net.frozendevelopment.mailshare.data.sqldelight.models.DocumentId
import net.frozendevelopment.mailshare.data.sqldelight.models.LetterId
import net.frozendevelopment.mailshare.data.sqldelight.models.ThreadId
import net.frozendevelopment.mailshare.util.TextExtractorType
import java.time.Instant

class CreateLetterUseCase(
    private val textExtractor: TextExtractorType,
    private val database: MailShareDB,
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
