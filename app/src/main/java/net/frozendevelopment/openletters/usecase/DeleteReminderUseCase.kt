package net.frozendevelopment.openletters.usecase

import net.frozendevelopment.openletters.data.sqldelight.ReminderQueries
import net.frozendevelopment.openletters.data.sqldelight.models.ReminderId
import net.frozendevelopment.openletters.feature.reminder.notification.ReminderNotification

class DeleteReminderUseCase(
    private val reminderQueries: ReminderQueries,
    private val reminderNotification: ReminderNotification,
) {
    operator fun invoke(id: ReminderId) {
        val existingReminder = reminderQueries
            .reminderDetail(id)
            .executeAsOneOrNull() ?: return

        reminderNotification.cancel(existingReminder.notificationId.toInt())
        reminderQueries.delete(id)
    }
}
