package net.frozendevelopment.openletters

import androidx.compose.ui.graphics.Color
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import net.frozendevelopment.openletters.data.mock.mockLetter
import net.frozendevelopment.openletters.data.sqldelight.models.CategoryId
import net.frozendevelopment.openletters.data.sqldelight.models.DocumentId
import net.frozendevelopment.openletters.data.sqldelight.models.LetterId
import net.frozendevelopment.openletters.mock.DocumentManagerMock
import net.frozendevelopment.openletters.usecase.DeleteLetterUseCase
import net.frozendevelopment.openletters.util.testDatabase
import org.junit.Test

class DeleteLetterUseCaseTests {
    @Test
    fun `deleteLetter should delete a letter from the database`() {
        val letterId = LetterId.random()
        val categoryId = CategoryId.random()
        val documentId = DocumentId.random()
        val documentManager = DocumentManagerMock()

        val letter = mockLetter(letterId)

        val database =
            testDatabase().apply {
                letterQueries.upsertLetter(
                    id = letterId,
                    sender = letter.sender,
                    recipient = letter.recipient,
                    body = letter.body,
                    created = letter.created,
                    lastModified = letter.lastModified,
                )

                documentQueries.insertDocument(documentId, letterId)

                categoryQueries.upsert(
                    id = categoryId,
                    label = "Category",
                    color = Color.Black,
                    priority = 0,
                    created = letter.created,
                    lastModified = letter.lastModified,
                )

                letterQueries.tagLetterWithCategory(
                    letterId = letterId,
                    categoryId = categoryId,
                )
            }

        val deleteLetter =
            DeleteLetterUseCase(
                letterQueries = database.letterQueries,
                documentQueries = database.documentQueries,
                documentManager = documentManager,
            )
        deleteLetter(letterId)

        val deletedLetter = database.letterQueries.letterDetail(letterId).executeAsOneOrNull()
        val deletedDocuments = database.documentQueries.documentsForLetter(letterId).executeAsList()
        val deletedCategories = database.letterQueries.categoriesForLetter(letterId).executeAsList()
        val category = database.categoryQueries.get(categoryId).executeAsOneOrNull()

        assertNull(deletedLetter)
        assertTrue(deletedDocuments.isEmpty())
        assertTrue(deletedCategories.isEmpty())
        assertEquals(1, documentManager.deleteCallCount)
        assertNotNull(category)
    }
}
