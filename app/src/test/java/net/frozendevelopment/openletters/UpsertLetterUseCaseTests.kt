package net.frozendevelopment.openletters

import android.net.Uri
import androidx.compose.ui.graphics.Color
import net.frozendevelopment.openletters.data.sqldelight.LetterDetail
import net.frozendevelopment.openletters.data.sqldelight.models.CategoryId
import net.frozendevelopment.openletters.data.sqldelight.models.DocumentId
import net.frozendevelopment.openletters.data.sqldelight.models.LetterId
import net.frozendevelopment.openletters.mock.DocumentManagerMock
import net.frozendevelopment.openletters.usecase.UpsertLetterUseCase
import net.frozendevelopment.openletters.util.testDatabase
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.LocalDateTime
import java.time.ZoneOffset

@RunWith(RobolectricTestRunner::class)
class UpsertLetterUseCaseTests {
    @Test
    fun `upsertLetter should insert a new letter into the database`() {
        val database = testDatabase()
        val documentManager = DocumentManagerMock()
        val time = LocalDateTime.ofEpochSecond(1000, 0, ZoneOffset.UTC)
        val upsertLetter =
            UpsertLetterUseCase(
                documentManager = documentManager,
                database = database,
                now = { time },
            )
        val categoryId = CategoryId.random()
        val testId: LetterId = LetterId.random()
        val documentId = DocumentId.random()

        database.categoryQueries.upsert(
            id = categoryId,
            label = "Category",
            color = Color.Black,
            priority = 0,
            created = time,
            lastModified = time,
        )

        upsertLetter(
            sender = "Sender",
            recipient = "Recipient",
            transcript = "Transcript",
            documents = mapOf(documentId to Uri.parse("file:///somethingexists.pdf")),
            categories = listOf(categoryId),
            letterId = testId,
        )

        val expected =
            LetterDetail(
                id = testId,
                sender = "Sender",
                recipient = "Recipient",
                body = "Transcript",
                created = time,
                lastModified = time,
                documentIds = documentId.toString(),
                categoryIds = categoryId.toString(),
            )

        val letter = database.letterQueries.letterDetail(testId).executeAsOneOrNull()
        Assert.assertEquals(expected, letter)
        Assert.assertEquals(0, documentManager.deleteCallCount)
        Assert.assertEquals(1, documentManager.persistCallCount)
    }

    @Test
    fun `upsertLetter should update an existing letter in the database`() {
        val database = testDatabase()
        val documentManager = DocumentManagerMock()
        val time = LocalDateTime.ofEpochSecond(1000, 0, ZoneOffset.UTC)
        val upsertLetter =
            UpsertLetterUseCase(
                documentManager = documentManager,
                database = database,
                now = { time },
            )
        val categoryId = CategoryId.random()
        val testId: LetterId = LetterId.random()
        val documentId = DocumentId.random()

        database.categoryQueries.upsert(
            id = categoryId,
            label = "Category",
            color = Color.Black,
            priority = 0,
            created = time,
            lastModified = time,
        )

        upsertLetter(
            sender = "Sender",
            recipient = "Recipient",
            transcript = "Transcript",
            documents = mapOf(documentId to Uri.parse("file:///somethingexists.pdf")),
            categories = listOf(categoryId),
            letterId = testId,
        )

        val newSender = "New Sender"
        val newRecipient = "New Recipient"
        val newTranscript = "New Transcript"

        upsertLetter(
            sender = newSender,
            recipient = newRecipient,
            transcript = newTranscript,
            documents = mapOf(documentId to Uri.parse("file:///somethingexists.pdf")),
            categories = listOf(categoryId),
            letterId = testId,
        )

        val updatedLetter = database.letterQueries.letterDetail(testId).executeAsOneOrNull()

        val expectedUpdatedLetter =
            LetterDetail(
                id = testId,
                sender = newSender,
                recipient = newRecipient,
                body = newTranscript,
                created = time,
                lastModified = time,
                documentIds = documentId.toString(),
                categoryIds = categoryId.toString(),
            )

        Assert.assertEquals(expectedUpdatedLetter, updatedLetter)
        Assert.assertEquals(0, documentManager.deleteCallCount)
        Assert.assertEquals(1, documentManager.persistCallCount) // document persists only once
    }

    @Test
    fun `upsertLetter should delete existing documents that are not in the new documents map`() {
        val database = testDatabase()
        val documentManager = DocumentManagerMock()
        val time = LocalDateTime.ofEpochSecond(1000, 0, ZoneOffset.UTC)
        val upsertLetter =
            UpsertLetterUseCase(
                documentManager = documentManager,
                database = database,
                now = { time },
            )
        val categoryId = CategoryId.random()
        val testId: LetterId = LetterId.random()
        val documentId1 = DocumentId.random()
        val documentId2 = DocumentId.random()

        database.categoryQueries.upsert(
            id = categoryId,
            label = "Category",
            color = Color.Black,
            priority = 0,
            created = time,
            lastModified = time,
        )

        database.letterQueries.upsertLetter(
            id = testId,
            sender = "Sender",
            recipient = "Recipient",
            body = "Transcript",
            created = time,
            lastModified = time,
        )

        database.letterQueries.tagLetterWithCategory(
            letterId = testId,
            categoryId = categoryId,
        )

        database.documentQueries.insertDocument(
            id = documentId1,
            letterId = testId,
        )
        database.documentQueries.insertDocument(
            id = documentId2,
            letterId = testId,
        )

        upsertLetter(
            sender = "Sender",
            recipient = "Recipient",
            transcript = "Transcript",
            documents =
                mapOf(
                    documentId1 to Uri.parse("file:///somethingexists1.pdf"),
                ),
            categories = listOf(categoryId),
            letterId = testId,
        )

        val remainingDocuments = database.documentQueries.documentsForLetter(testId).executeAsList()

        Assert.assertTrue(remainingDocuments.any { it.id == documentId1 })
        Assert.assertFalse(remainingDocuments.any { it.id == documentId2 })

        Assert.assertEquals(1, documentManager.deleteCallCount)
        Assert.assertEquals(0, documentManager.persistCallCount)
    }

    @Test
    fun `upsertLetter should persist new documents`() {
        val database = testDatabase()
        val documentManager = DocumentManagerMock()
        val time = LocalDateTime.ofEpochSecond(1000, 0, ZoneOffset.UTC)
        val upsertLetter =
            UpsertLetterUseCase(
                documentManager = documentManager,
                database = database,
                now = { time },
            )
        val categoryId = CategoryId.random()
        val testId: LetterId = LetterId.random()
        val documentId = DocumentId.random()

        database.categoryQueries.upsert(
            id = categoryId,
            label = "Category",
            color = Color.Black,
            priority = 0,
            created = time,
            lastModified = time,
        )

        upsertLetter(
            sender = "Sender",
            recipient = "Recipient",
            transcript = "Transcript",
            documents = mapOf(documentId to Uri.parse("file:///somethingexists.pdf")),
            categories = listOf(categoryId),
            letterId = testId,
        )

        val documentsForLetter = database.documentQueries.documentsForLetter(testId).executeAsList()

        Assert.assertEquals(1, documentsForLetter.size)
        Assert.assertEquals(documentId.toString(), documentsForLetter[0].id.toString())
        Assert.assertEquals(1, documentManager.persistCallCount) // The new document should be persisted
    }

    @Test
    fun `upsertLetter should remove existing categories that are not in the new categories list`() {
        val database = testDatabase()
        val documentManager = DocumentManagerMock()
        val time = LocalDateTime.ofEpochSecond(1000, 0, ZoneOffset.UTC)
        val upsertLetter =
            UpsertLetterUseCase(
                documentManager = documentManager,
                database = database,
                now = { time },
            )
        val categoryId1 = CategoryId.random()
        val categoryId2 = CategoryId.random()
        val testId: LetterId = LetterId.random()
        val documentId = DocumentId.random()

        database.categoryQueries.upsert(
            id = categoryId1,
            label = "Category 1",
            color = Color.Black,
            priority = 0,
            created = time,
            lastModified = time,
        )
        database.categoryQueries.upsert(
            id = categoryId2,
            label = "Category 2",
            color = Color.White,
            priority = 1,
            created = time,
            lastModified = time,
        )

        upsertLetter(
            sender = "Sender",
            recipient = "Recipient",
            transcript = "Transcript",
            documents = mapOf(documentId to Uri.parse("file:///somethingexists.pdf")),
            categories = listOf(categoryId1, categoryId2),
            letterId = testId,
        )

        upsertLetter(
            sender = "Sender",
            recipient = "Recipient",
            transcript = "Transcript",
            documents = mapOf(documentId to Uri.parse("file:///somethingexists.pdf")),
            categories = listOf(categoryId1),
            letterId = testId,
        )

        val remainingCategories = database.letterQueries.categoriesForLetter(testId).executeAsList()

        Assert.assertFalse(remainingCategories.contains(categoryId2))
        Assert.assertEquals(1, database.letterQueries.categoriesForLetter(testId).executeAsList().size)
    }

    @Test
    fun `upsertLetter should tag the letter with the new categories`() {
        val database = testDatabase()
        val documentManager = DocumentManagerMock()
        val time = LocalDateTime.ofEpochSecond(1000, 0, ZoneOffset.UTC)
        val upsertLetter =
            UpsertLetterUseCase(
                documentManager = documentManager,
                database = database,
                now = { time },
            )
        val categoryId1 = CategoryId.random()
        val categoryId2 = CategoryId.random()
        val categoryId3 = CategoryId.random()
        val testId: LetterId = LetterId.random()
        val documentId = DocumentId.random()

        database.categoryQueries.upsert(
            id = categoryId1,
            label = "Category 1",
            color = Color.Black,
            priority = 0,
            created = time,
            lastModified = time,
        )
        database.categoryQueries.upsert(
            id = categoryId2,
            label = "Category 2",
            color = Color.White,
            priority = 1,
            created = time,
            lastModified = time,
        )
        database.categoryQueries.upsert(
            id = categoryId3,
            label = "Category 3",
            color = Color.Gray,
            priority = 2,
            created = time,
            lastModified = time,
        )

        upsertLetter(
            sender = "Sender",
            recipient = "Recipient",
            transcript = "Transcript",
            documents = mapOf(documentId to Uri.parse("file:///somethingexists.pdf")),
            categories = listOf(categoryId1, categoryId2),
            letterId = testId,
        )

        upsertLetter(
            sender = "Sender",
            recipient = "Recipient",
            transcript = "Transcript",
            documents = mapOf(documentId to Uri.parse("file:///somethingexists.pdf")),
            categories = listOf(categoryId1, categoryId2, categoryId3),
            letterId = testId,
        )

        val taggedCategories = database.letterQueries.categoriesForLetter(testId).executeAsList()

        Assert.assertTrue(taggedCategories.contains(categoryId1))
        Assert.assertTrue(taggedCategories.contains(categoryId2))
        Assert.assertTrue(taggedCategories.contains(categoryId3))
    }
}
