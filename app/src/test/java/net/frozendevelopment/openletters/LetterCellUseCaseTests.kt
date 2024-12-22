package net.frozendevelopment.openletters

import androidx.compose.ui.graphics.Color
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import net.frozendevelopment.openletters.data.sqldelight.models.CategoryId
import net.frozendevelopment.openletters.data.sqldelight.models.LetterId
import net.frozendevelopment.openletters.usecase.LetterCellModel
import net.frozendevelopment.openletters.usecase.LetterCellUseCase
import net.frozendevelopment.openletters.util.testDatabase
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneOffset

class LetterCellUseCaseTests {
    @Test
    fun `should return null if the letter does not exist`() {
        val database = testDatabase()
        val useCase = LetterCellUseCase(database.letterQueries)
        val result = useCase.invoke(LetterId.random())
        assertNull(result)
    }

    @Test
    fun `should return a LetterCellModel if the letter exists`() {
        val categoryId = CategoryId.random()
        val categoryColor = Color.Cyan

        val created = LocalDateTime.ofEpochSecond(100L, 0, ZoneOffset.UTC)
        val lastModified = LocalDateTime.ofEpochSecond(500L, 0, ZoneOffset.UTC)

        val expectedLetter =
            LetterCellModel(
                id = LetterId.random(),
                sender = "Sender",
                recipient = "Recipient",
                body = "Body",
                created = created,
                lastModified = lastModified,
                categoryColors = listOf(categoryColor),
            )

        val database =
            testDatabase().apply {
                letterQueries.upsertLetter(
                    id = expectedLetter.id,
                    sender = expectedLetter.sender,
                    recipient = expectedLetter.recipient,
                    body = expectedLetter.body,
                    created = expectedLetter.created,
                    lastModified = expectedLetter.lastModified,
                )

                categoryQueries.upsert(
                    id = categoryId,
                    label = "Category",
                    color = categoryColor,
                    priority = 0,
                    created = created,
                    lastModified = lastModified,
                )

                letterQueries.tagLetterWithCategory(
                    letterId = expectedLetter.id,
                    categoryId = categoryId,
                )
            }

        val useCase = LetterCellUseCase(database.letterQueries)
        val result = useCase.invoke(expectedLetter.id)
        assertEquals(expectedLetter, result)
    }
}
