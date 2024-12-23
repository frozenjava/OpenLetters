package net.frozendevelopment.openletters

import net.frozendevelopment.openletters.data.sqldelight.OpenLettersDB
import net.frozendevelopment.openletters.data.sqldelight.models.LetterId
import net.frozendevelopment.openletters.data.sqldelight.models.ReminderId
import net.frozendevelopment.openletters.usecase.ReminderWithDetailsUseCase
import net.frozendevelopment.openletters.util.testDatabase
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.LocalDateTime
import java.time.ZoneOffset

@RunWith(RobolectricTestRunner::class)
class ReminderWithDetailsUseCaseTests {
    private lateinit var database: OpenLettersDB
    private lateinit var reminderWithDetailsUseCase: ReminderWithDetailsUseCase

    @Before
    fun setup() {
        database = testDatabase()
        reminderWithDetailsUseCase = ReminderWithDetailsUseCase(database.reminderQueries)
    }

    private fun createReminderDetails(
        id: ReminderId,
        title: String = "Test Title",
        description: String? = "Test Description",
        acknowledged: Boolean = false,
        scheduledFor: LocalDateTime = LocalDateTime.ofEpochSecond(2000, 0, ZoneOffset.UTC),
        created: LocalDateTime = LocalDateTime.ofEpochSecond(1000, 0, ZoneOffset.UTC),
        lastModified: LocalDateTime = LocalDateTime.ofEpochSecond(1500, 0, ZoneOffset.UTC),
        notificationId: Long = 1,
        letterIds: List<LetterId> = emptyList(),
    ) {
        database.transaction {
            database.reminderQueries.upsert(
                id = id,
                title = title,
                description = description,
                scheduledFor = scheduledFor,
                created = created,
                lastModified = lastModified,
                notificationId = notificationId,
            )

            for (letterId in letterIds) {
                database.letterQueries.upsertLetter(
                    id = letterId,
                    sender = "Some Sender",
                    recipient = "Some Recipient",
                    body = "some body",
                    created = created,
                    lastModified = lastModified,
                )

                database.letterQueries.tagLetterWithReminder(letterId, id)
            }
        }
    }

    @Test
    fun `invoke returns null when no reminder exists`() {
        val result = reminderWithDetailsUseCase(ReminderId.random())
        Assert.assertNull(result)
    }

    @Test
    fun `invoke returns ReminderWithDetails with empty letters list`() {
        val reminderId = ReminderId.random()
        createReminderDetails(id = reminderId)

        val result = reminderWithDetailsUseCase(reminderId)

        Assert.assertNotNull(result)
        Assert.assertEquals(reminderId, result?.id)
        Assert.assertEquals("Test Title", result?.title)
        Assert.assertEquals("Test Description", result?.description)
        Assert.assertFalse(result?.acknowledged ?: true)
        Assert.assertEquals(LocalDateTime.ofEpochSecond(2000, 0, ZoneOffset.UTC), result?.scheduledAt)
        Assert.assertEquals(LocalDateTime.ofEpochSecond(1000, 0, ZoneOffset.UTC), result?.createdAt)
        Assert.assertEquals(LocalDateTime.ofEpochSecond(1500, 0, ZoneOffset.UTC), result?.lastModified)
        Assert.assertEquals(1, result?.notificationId)
        Assert.assertTrue(result?.letters?.isEmpty() ?: false)
    }

    @Test
    fun `invoke returns ReminderWithDetails with letters list`() {
        val reminderId = ReminderId.random()
        val letterIds = listOf(LetterId("letter1"), LetterId("letter2"))
        createReminderDetails(
            id = reminderId,
            letterIds = letterIds,
        )

        val result = reminderWithDetailsUseCase(reminderId)

        Assert.assertNotNull(result)
        Assert.assertEquals(reminderId, result?.id)
        Assert.assertEquals(letterIds, result?.letters)
    }
}
