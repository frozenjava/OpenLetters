package net.frozendevelopment.openletters.feature.reminder.list

import android.Manifest
import android.app.Application
import android.os.Build
import androidx.compose.runtime.Immutable
import net.frozendevelopment.openletters.data.sqldelight.ReminderQueries
import net.frozendevelopment.openletters.data.sqldelight.models.ReminderId
import net.frozendevelopment.openletters.extensions.isPermissionGranted
import net.frozendevelopment.openletters.usecase.DeleteReminderUseCase
import net.frozendevelopment.openletters.util.StatefulViewModel

@Immutable
data class ReminderListState(
    val hasNotificationPermission: Boolean = false,
    val urgentReminders: List<ReminderId> = emptyList(),
    val upcomingReminders: List<ReminderId> = emptyList(),
    val pastReminders: List<ReminderId> = emptyList(),
) {
    val isEmpty: Boolean
        get() = urgentReminders.isEmpty() && upcomingReminders.isEmpty() && pastReminders.isEmpty()
}

class ReminderListViewModel(
    private val application: Application,
    private val reminderQueries: ReminderQueries,
    private val deleteReminder: DeleteReminderUseCase,
) : StatefulViewModel<ReminderListState>(ReminderListState()) {
    override fun load() {
        val hasNotificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            application.isPermissionGranted(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            true
        }

        update { copy(
            hasNotificationPermission = hasNotificationPermission,
            urgentReminders = reminderQueries.urgentReminders().executeAsList(),
            upcomingReminders = reminderQueries.upcomingReminders().executeAsList(),
            pastReminders = reminderQueries.pastReminders().executeAsList(),
        )}
    }

    fun handlePermissionResult(granted: Boolean) {
        update { copy(hasNotificationPermission = granted) }
    }

    fun delete(id: ReminderId) {
        deleteReminder(id)
        load()
    }
}
