package net.frozendevelopment.openletters.feature.reminder.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import net.frozendevelopment.openletters.data.sqldelight.ReminderQueries
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDateTime
import java.time.ZoneId

class AlarmHydrationReceiver : BroadcastReceiver(), KoinComponent {
    private val reminderNotification: ReminderNotification by inject()
    private val reminderQueries: ReminderQueries by inject()

    override fun onReceive(
        context: Context?,
        intent: Intent?,
    ) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            rehydrateAlarms()
        } else if (intent?.action == Intent.ACTION_TIME_CHANGED) {
            rehydrateAlarms()
        }
    }

    private fun rehydrateAlarms() {
        val reminders = reminderQueries.unacknowledgedReminders().executeAsList()
        val pastReminders = reminders.filter { it.scheduledFor < LocalDateTime.now() }
        val upcomingReminders = reminders.filter { it.scheduledFor > LocalDateTime.now() }

        for ((index, reminder) in pastReminders.withIndex()) {
            // Any alarm scheduled in the past will be sent immediately
            // to avoid this stagger the alarms by adding scheduling at (now + (1 + index) minutes))
            val notifyAtMillis =
                LocalDateTime.now()
                    .plusMinutes((1 + index).toLong())
                    .atZone(ZoneId.systemDefault())
                    .toEpochSecond() * 1000L

            reminderNotification.schedule(
                title = reminder.title,
                content = reminder.description,
                notificationId = reminder.notificationId.toInt(),
                reminderId = reminder.id.value,
                notifyAtMillis = notifyAtMillis,
            )
        }

        for (reminder in upcomingReminders) {
            reminderNotification.schedule(
                title = reminder.title,
                content = reminder.description,
                notificationId = reminder.notificationId.toInt(),
                reminderId = reminder.id.value,
                notifyAtMillis = reminder.scheduledFor.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000L,
            )
        }
    }
}
