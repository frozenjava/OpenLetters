package net.frozendevelopment.openletters.usecase

import net.frozendevelopment.openletters.data.sqldelight.ReminderQueries
import net.frozendevelopment.openletters.data.sqldelight.models.LetterId
import net.frozendevelopment.openletters.data.sqldelight.models.ReminderId
import net.frozendevelopment.openletters.feature.reminder.notification.ReminderNotification
import java.time.LocalDateTime
import java.time.ZoneId

class CreateReminderUseCase(
    private val reminderQueries: ReminderQueries,
    private val reminderNotification: ReminderNotification,
    private val now: () -> LocalDateTime = { LocalDateTime.now() },
) {

    operator fun invoke(
        title: String,
        description: String?,
        scheduledFor: LocalDateTime,
        letters: Set<LetterId>,
        reminderId: ReminderId = ReminderId.random(),
    ) {
        val notificationId = (reminderQueries
            .largestNotificationId()
            .executeAsOneOrNull()?.MAX ?: 0) + 1

        reminderQueries.transaction {
            val currentTime = now()

            reminderQueries.create(
                id = reminderId,
                title = title,
                description = description,
                scheduledFor = scheduledFor,
                created = currentTime,
                lastModified = currentTime,
                notificationId = notificationId,
            )

            for (letter in letters) {
                reminderQueries.tagLetter(
                    letterId = letter,
                    reminderId = reminderId
                )
            }
        }

        reminderNotification.schedule(
            title = title,
            content = description,
            notificationId = notificationId.toInt(),
            reminderId = reminderId.toString(),
            notifyAtMillis = scheduledFor.atZone(ZoneId.systemDefault()).toEpochSecond()*1000L
        )
    }
}
