package net.frozendevelopment.openletters

import net.frozendevelopment.openletters.data.sqldelight.models.ReminderId
import net.frozendevelopment.openletters.mock.ReminderNotificationMock
import net.frozendevelopment.openletters.usecase.AcknowledgeReminderUseCase
import net.frozendevelopment.openletters.util.testDatabase
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.LocalDateTime
import java.time.ZoneOffset

@RunWith(RobolectricTestRunner::class)
class AcknowledgeReminderUseCaseTests {
    @Test
    fun `marks reminder as acknowledged and cancels notification`() {
        val reminderId = ReminderId.random()
        val database = testDatabase()
        val reminderNotification = ReminderNotificationMock()
        val acknowledge = AcknowledgeReminderUseCase(database.reminderQueries, reminderNotification)

        database.reminderQueries.upsert(
            id = reminderId,
            title = "My Test Reminder",
            description = "My Test Description",
            scheduledFor = LocalDateTime.ofEpochSecond(2000, 0, ZoneOffset.UTC),
            created = LocalDateTime.ofEpochSecond(100, 0, ZoneOffset.UTC),
            lastModified = LocalDateTime.ofEpochSecond(100, 0, ZoneOffset.UTC),
            notificationId = 1L,
        )

        acknowledge(reminderId)

        val databaseReminder = database.reminderQueries.reminder(reminderId).executeAsOneOrNull()

        Assert.assertTrue(databaseReminder?.acknowledged ?: false)
        Assert.assertEquals(listOf(ReminderNotificationMock.CancelArgs(1)), reminderNotification.cancelArgs)
    }
}
