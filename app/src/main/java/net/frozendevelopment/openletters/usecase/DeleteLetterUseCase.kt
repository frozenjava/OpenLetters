package net.frozendevelopment.openletters.usecase

import net.frozendevelopment.openletters.data.sqldelight.DocumentQueries
import net.frozendevelopment.openletters.data.sqldelight.LetterQueries
import net.frozendevelopment.openletters.data.sqldelight.models.LetterId
import net.frozendevelopment.openletters.util.DocumentManagerType

class DeleteLetterUseCase(
    private val letterQueries: LetterQueries,
    private val documentQueries: DocumentQueries,
    private val documentManager: DocumentManagerType,
) {
    operator fun invoke(id: LetterId) {
        val documents = documentQueries.documentsForLetter(id).executeAsList()

        letterQueries.deleteLetter(id)

        for (document in documents) {
            documentManager.delete(document.id)
        }
    }
}
