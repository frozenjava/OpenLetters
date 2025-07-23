package net.frozendevelopment.openletters.usecase

import net.frozendevelopment.openletters.data.sqldelight.ReminderQueries
import net.frozendevelopment.openletters.data.sqldelight.models.LetterId
import net.frozendevelopment.openletters.data.sqldelight.models.ReminderId
import net.frozendevelopment.openletters.feature.reminder.notification.ReminderNotificationType
import java.time.LocalDateTime
import java.time.ZoneId

class UpsertReminderUseCase(
    private val reminderQueries: ReminderQueries,
    private val reminderNotification: ReminderNotificationType,
    private val now: () -> LocalDateTime = { LocalDateTime.now() },
) {
    operator fun invoke(
        title: String,
        description: String?,
        scheduledFor: LocalDateTime,
        letters: Set<LetterId>,
        reminderId: ReminderId = ReminderId.random(),
    ) {
        val notificationId =
            (
                reminderQueries
                    .largestNotificationId()
                    .executeAsOneOrNull()
                    ?.MAX ?: 0
            ) + 1

        // if a reminder with the id already exists, then cancel the current scheduled notification
        // just in case the reminder date/time has changed. it will get rescheduled
        val existingReminder = reminderQueries.reminderDetail(reminderId).executeAsOneOrNull()

        // the letters that are tagged to this reminder in the database
        // we need this to diff the current `letters` and old list of letters
        val taggedLetters =
            existingReminder
                ?.letterIds
                ?.split(",")
                ?.map { LetterId(it) }
                ?.toSet() ?: emptySet()

        if (existingReminder != null) {
            reminderNotification.cancel(existingReminder.notificationId.toInt())
        }

        reminderQueries.transaction {
            val currentTime = now()

            reminderQueries.upsert(
                id = reminderId,
                title = title,
                description = description,
                scheduledFor = scheduledFor,
                created = currentTime,
                lastModified = currentTime,
                notificationId = notificationId,
            )

            // tag any new letters that are not in the already tagged list
            for (letter in letters - taggedLetters) {
                reminderQueries.tagLetter(
                    letterId = letter,
                    reminderId = reminderId,
                )
            }

            // untag any letters that are not in the current list
            for (letter in taggedLetters - letters) {
                reminderQueries.untagLetter(
                    letterId = letter,
                    reminderId = reminderId,
                )
            }
        }

        reminderNotification.schedule(
            title = title,
            content = description,
            notificationId = notificationId.toInt(),
            reminderId = reminderId.toString(),
            notifyAtMillis = scheduledFor.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000L,
        )
    }
}
