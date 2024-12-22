package net.frozendevelopment.openletters.usecase

import net.frozendevelopment.openletters.data.sqldelight.ReminderQueries
import net.frozendevelopment.openletters.data.sqldelight.models.ReminderId
import net.frozendevelopment.openletters.feature.reminder.notification.ReminderNotificationType

class AcknowledgeReminderUseCase(
    private val reminderQueries: ReminderQueries,
    private val notification: ReminderNotificationType,
) {
    operator fun invoke(id: ReminderId) {
        reminderQueries.acknowledgeReminder(id)
        val notificationId =
            reminderQueries.notificationId(id)
                .executeAsOneOrNull()
                ?.toInt() ?: return

        notification.cancel(notificationId)
    }
}
