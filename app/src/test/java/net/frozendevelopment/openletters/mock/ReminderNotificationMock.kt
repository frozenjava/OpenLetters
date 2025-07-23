package net.frozendevelopment.openletters.mock

import net.frozendevelopment.openletters.feature.reminder.notification.ReminderNotificationType

class ReminderNotificationMock : ReminderNotificationType {
    var sendCallCount: Int = 0
        private set

    var scheduleCallCount: Int = 0
        private set

    var cancelCallCount: Int = 0
        private set

    var sendHandler: (String, String?, String) -> Unit = { _, _, _ -> }
    var scheduleHandler: (String, String?, Int, String, Long) -> Unit = { _, _, _, _, _ -> }
    var cancelHandler: (Int) -> Unit = { }

    var sendArgs: List<SendArgs> = emptyList()
        private set
    var scheduleArgs: List<ScheduleArgs> = emptyList()
        private set
    var cancelArgs: List<CancelArgs> = emptyList()
        private set

    data class SendArgs(
        val title: String,
        val description: String?,
        val reminderId: String,
    )

    data class ScheduleArgs(
        val title: String,
        val content: String?,
        val notificationId: Int,
        val reminderId: String,
        val notifyAtMillis: Long,
    )

    data class CancelArgs(
        val notificationId: Int,
    )

    override fun send(
        title: String,
        description: String?,
        reminderId: String,
    ) {
        sendCallCount++
        sendHandler(title, description, reminderId)
        sendArgs = sendArgs + SendArgs(title, description, reminderId)
    }

    override fun schedule(
        title: String,
        content: String?,
        notificationId: Int,
        reminderId: String,
        notifyAtMillis: Long,
    ) {
        scheduleCallCount++
        scheduleHandler(title, content, notificationId, reminderId, notifyAtMillis)
        scheduleArgs = scheduleArgs + ScheduleArgs(title, content, notificationId, reminderId, notifyAtMillis)
    }

    override fun cancel(notificationId: Int) {
        cancelCallCount++
        cancelHandler(notificationId)
        cancelArgs = cancelArgs + CancelArgs(notificationId)
    }
}
