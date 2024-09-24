package net.frozendevelopment.openletters.usecase

import net.frozendevelopment.openletters.data.sqldelight.ReminderQueries
import net.frozendevelopment.openletters.data.sqldelight.models.LetterId
import net.frozendevelopment.openletters.data.sqldelight.models.ReminderId
import java.time.LocalDateTime

data class ReminderWithDetails(
    val id: ReminderId,
    val title: String,
    val description: String?,
    val acknowledged: Boolean,
    val scheduledAt: LocalDateTime,
    val createdAt: LocalDateTime,
    val lastModified: LocalDateTime,
    val notificationId: Int,
    val letters: List<LetterId>
)

class ReminderWithDetailsUseCase(
    private val reminderQueries: ReminderQueries,
) {
    operator fun invoke(reminderId: ReminderId): ReminderWithDetails? {
        val reminder = reminderQueries.reminderDetail(reminderId).executeAsOneOrNull() ?: return null

        val letterIds = reminder.letterIds?.split(",")
            ?.map { LetterId(it.trim()) }
            ?: emptyList()

        return ReminderWithDetails(
            id = reminder.id,
            title = reminder.title,
            description = reminder.description,
            acknowledged = reminder.acknowledged,
            scheduledAt = reminder.scheduledFor,
            createdAt = reminder.created,
            lastModified = reminder.lastModified,
            notificationId = reminder.notificationId.toInt(),
            letters = letterIds
        )
    }
}
