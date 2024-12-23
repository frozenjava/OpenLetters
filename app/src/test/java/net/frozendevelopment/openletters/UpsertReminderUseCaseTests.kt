package net.frozendevelopment.openletters

import net.frozendevelopment.openletters.data.sqldelight.OpenLettersDB
import net.frozendevelopment.openletters.data.sqldelight.migrations.Reminder
import net.frozendevelopment.openletters.data.sqldelight.models.LetterId
import net.frozendevelopment.openletters.data.sqldelight.models.ReminderId
import net.frozendevelopment.openletters.mock.ReminderNotificationMock
import net.frozendevelopment.openletters.usecase.UpsertReminderUseCase
import net.frozendevelopment.openletters.util.testDatabase
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

@RunWith(RobolectricTestRunner::class)
class UpsertReminderUseCaseTests {
    private lateinit var database: OpenLettersDB
    private lateinit var reminderNotification: ReminderNotificationMock
    private lateinit var upsertReminder: UpsertReminderUseCase

    private val now: LocalDateTime = LocalDateTime.ofEpochSecond(500, 0, ZoneOffset.UTC)

    private fun createReminder(
        reminderId: ReminderId,
        notificationId: Long,
        title: String = "Test Reminder",
        description: String? = null,
        acknowledged: Boolean = false,
        scheduledFor: LocalDateTime = LocalDateTime.ofEpochSecond(2000, 0, ZoneOffset.UTC),
        created: LocalDateTime = LocalDateTime.ofEpochSecond(500, 0, ZoneOffset.UTC),
        lastModified: LocalDateTime = LocalDateTime.ofEpochSecond(500, 0, ZoneOffset.UTC),
    ): Reminder {
        return Reminder(
            id = reminderId,
            notificationId = notificationId,
            title = title,
            description = description,
            scheduledFor = scheduledFor,
            created = created,
            acknowledged = acknowledged,
            lastModified = lastModified,
        )
    }

    @Before
    fun setup() {
        database = testDatabase()
        reminderNotification = ReminderNotificationMock()
        upsertReminder =
            UpsertReminderUseCase(
                reminderQueries = database.reminderQueries,
                reminderNotification = reminderNotification,
                now = { now },
            )
    }

    @Test
    fun `create new reminder`() {
        val letterId = LetterId.random()
        val reminderId = ReminderId.random()
        val notificationId = 1L
        val expectedReminder = createReminder(reminderId, notificationId)

        database.letterQueries.upsertLetter(
            id = letterId,
            sender = null,
            recipient = null,
            body = "Test Letter",
            created = LocalDateTime.now(),
            lastModified = LocalDateTime.now(),
        )

        upsertReminder(
            title = expectedReminder.title,
            description = expectedReminder.description,
            scheduledFor = expectedReminder.scheduledFor,
            letters = setOf(letterId),
            reminderId = reminderId,
        )

        val databaseReminder = database.reminderQueries.reminder(reminderId).executeAsOneOrNull()
        val taggedLetters = database.reminderQueries.lettersForReminder(reminderId).executeAsList()

        Assert.assertEquals(expectedReminder, databaseReminder)
        Assert.assertEquals(listOf(letterId), taggedLetters)
        Assert.assertEquals(1, reminderNotification.scheduleCallCount)
        Assert.assertEquals(0, reminderNotification.cancelCallCount)
        Assert.assertEquals(
            ReminderNotificationMock.ScheduleArgs(
                title = expectedReminder.title,
                content = expectedReminder.description,
                notificationId = expectedReminder.notificationId.toInt(),
                reminderId = expectedReminder.id.value,
                notifyAtMillis = expectedReminder.scheduledFor.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000L,
            ),
            reminderNotification.scheduleArgs.first(),
        )
    }

    @Test
    fun `update existing reminder`() {
        val letters = listOf(LetterId.random(), LetterId.random(), LetterId.random())
        val reminderId = ReminderId.random()
        val notificationId = 2L
        val expectedReminder =
            createReminder(
                reminderId,
                notificationId,
                lastModified = now,
                created = LocalDateTime.ofEpochSecond(100, 0, ZoneOffset.UTC),
            )

        for (letterId in letters) {
            database.letterQueries.upsertLetter(
                id = letterId,
                sender = null,
                recipient = null,
                body = "Test Letter",
                created = LocalDateTime.now(),
                lastModified = LocalDateTime.now(),
            )
        }

        database.reminderQueries.upsert(
            id = reminderId,
            title = "My Test Reminder",
            description = "My Test Description",
            scheduledFor = LocalDateTime.ofEpochSecond(2000, 0, ZoneOffset.UTC),
            created = LocalDateTime.ofEpochSecond(100, 0, ZoneOffset.UTC),
            lastModified = LocalDateTime.ofEpochSecond(100, 0, ZoneOffset.UTC),
            notificationId = 1L,
        )

        upsertReminder(
            title = expectedReminder.title,
            description = expectedReminder.description,
            scheduledFor = expectedReminder.scheduledFor,
            letters = letters.slice(1..letters.lastIndex).toSet(),
            reminderId = reminderId,
        )

        val databaseReminder = database.reminderQueries.reminder(reminderId).executeAsOneOrNull()
        val taggedLetters = database.reminderQueries.lettersForReminder(reminderId).executeAsList()

        Assert.assertEquals(expectedReminder, databaseReminder)
        Assert.assertEquals(letters.slice(1..letters.lastIndex), taggedLetters)
        Assert.assertEquals(1, reminderNotification.scheduleCallCount)
        Assert.assertEquals(1, reminderNotification.cancelCallCount)
        Assert.assertEquals(ReminderNotificationMock.CancelArgs(1), reminderNotification.cancelArgs.first())
        Assert.assertEquals(
            ReminderNotificationMock.ScheduleArgs(
                title = expectedReminder.title,
                content = expectedReminder.description,
                notificationId = expectedReminder.notificationId.toInt(),
                reminderId = expectedReminder.id.value,
                notifyAtMillis = expectedReminder.scheduledFor.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000L,
            ),
            reminderNotification.scheduleArgs.first(),
        )
    }
}
