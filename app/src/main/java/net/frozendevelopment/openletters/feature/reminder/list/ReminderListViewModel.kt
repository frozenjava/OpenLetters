package net.frozendevelopment.openletters.feature.reminder.list

import androidx.compose.runtime.Immutable
import net.frozendevelopment.openletters.data.sqldelight.ReminderQueries
import net.frozendevelopment.openletters.data.sqldelight.models.ReminderId
import net.frozendevelopment.openletters.util.StatefulViewModel

@Immutable
data class ReminderListState(
    val urgentReminders: List<ReminderId> = emptyList(),
    val upcomingReminders: List<ReminderId> = emptyList(),
    val pastReminders: List<ReminderId> = emptyList(),
) {
    val isEmpty: Boolean
        get() = urgentReminders.isEmpty() && upcomingReminders.isEmpty() && pastReminders.isEmpty()
}

class ReminderListViewModel(
    private val reminderQueries: ReminderQueries
) : StatefulViewModel<ReminderListState>(ReminderListState()) {
    suspend fun load() {
        update { copy(
            urgentReminders = reminderQueries.urgentReminders().executeAsList(),
            upcomingReminders = reminderQueries.upcomingReminders().executeAsList(),
            pastReminders = reminderQueries.pastReminders().executeAsList(),
        )}
    }
}
