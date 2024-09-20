package net.frozendevelopment.openletters.feature.reminder.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class ReminderReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val scheduleNotificationService = context?.let { ReminderNotification(it) }
        val title: String = intent?.getStringExtra(ReminderNotification.TITLE_KEY) ?: "Open Letters Reminder"
        val reminderId: String = intent?.getStringExtra(ReminderNotification.REMINDER_ID_KEY) ?: return
        val description: String? = intent.getStringExtra(ReminderNotification.CONTENT_KEY)
        scheduleNotificationService?.send(title, description, reminderId)
    }
}
