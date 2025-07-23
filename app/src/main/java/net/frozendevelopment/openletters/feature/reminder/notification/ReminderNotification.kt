package net.frozendevelopment.openletters.feature.reminder.notification

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_CANCEL_CURRENT
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_MUTABLE
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import net.frozendevelopment.openletters.DEEP_LINK_URI
import net.frozendevelopment.openletters.R
import net.frozendevelopment.openletters.REMINDERS_CHANNEL_ID

interface ReminderNotificationType {
    fun send(
        title: String,
        description: String?,
        reminderId: String,
    )

    fun schedule(
        title: String,
        content: String?,
        notificationId: Int,
        reminderId: String,
        notifyAtMillis: Long,
    )

    fun cancel(notificationId: Int)
}

class ReminderNotification(
    private val context: Context,
    private val notificationManager: NotificationManager = context.getSystemService(NotificationManager::class.java) as NotificationManager,
    private val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager,
) : ReminderNotificationType {
    companion object {
        const val TITLE_KEY: String = "title"
        const val CONTENT_KEY: String = "content"
        const val NOTIFICATION_ID_KEY: String = "notificationId"
        const val REMINDER_ID_KEY: String = "reminderId"
    }

    override fun send(
        title: String,
        description: String?,
        reminderId: String,
    ) {
        val openReminderIntent =
            Intent(
                Intent.ACTION_VIEW,
                "$DEEP_LINK_URI/reminder/$reminderId".toUri(),
            )

        val notification =
            NotificationCompat
                .Builder(context, REMINDERS_CHANNEL_ID)
                .setContentText(context.getString(R.string.app_name))
                .setContentTitle(title)
                .setSmallIcon(android.R.drawable.ic_popup_reminder)
//            .setLargeIcon(BitmapFactory.decodeResource(context.resources,
//                R.drawable.round_notifications_active_24
//            ))
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setStyle(
                    NotificationCompat
                        .BigTextStyle()
                        .bigText(description ?: "No information available"),
                )
//            .setStyle(
//                NotificationCompat.BigPictureStyle()
//                    .bigPicture(context.bitmapFromResource(R.drawable.code_with_zebru))
//            )
                .setContentIntent(
                    PendingIntent.getActivity(
                        context,
                        1,
                        openReminderIntent,
                        FLAG_CANCEL_CURRENT or FLAG_IMMUTABLE,
                    ),
                ).setAutoCancel(true)
                .build()

        notificationManager.notify(1, notification)
    }

    override fun schedule(
        title: String,
        content: String?,
        notificationId: Int,
        reminderId: String,
        notifyAtMillis: Long,
    ) {
        val intent = Intent(context.applicationContext, ReminderReceiver::class.java)
        intent.putExtra(TITLE_KEY, title)
        intent.putExtra(CONTENT_KEY, content)
        intent.putExtra(NOTIFICATION_ID_KEY, notificationId)
        intent.putExtra(REMINDER_ID_KEY, reminderId)

        val pendingIntent =
            PendingIntent.getBroadcast(
                context.applicationContext,
                notificationId,
                intent,
                FLAG_MUTABLE,
            )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            notifyAtMillis,
            pendingIntent,
        )
    }

    override fun cancel(notificationId: Int) {
        val intent = Intent(context.applicationContext, ReminderReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(
                context.applicationContext,
                notificationId,
                intent,
                FLAG_MUTABLE,
            )

        alarmManager.cancel(pendingIntent)
    }
}
