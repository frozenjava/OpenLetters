package net.frozendevelopment.openletters.feature.reminder.detail

import android.Manifest
import android.app.Application
import android.os.Build
import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.frozendevelopment.openletters.data.sqldelight.models.LetterId
import net.frozendevelopment.openletters.data.sqldelight.models.ReminderId
import net.frozendevelopment.openletters.extensions.isPermissionGranted
import net.frozendevelopment.openletters.usecase.AcknowledgeReminderUseCase
import net.frozendevelopment.openletters.usecase.ReminderWithDetailsUseCase
import net.frozendevelopment.openletters.util.StatefulViewModel
import java.time.LocalDateTime

sealed interface ReminderDetailState {
    data object Loading: ReminderDetailState
    data object NotFound: ReminderDetailState

    @Immutable
    data class Detail(
        val hasNotificationPermission: Boolean = false,
        val title: String = "",
        val description: String? = null,
        val isAcknowledged: Boolean = false,
        val date: LocalDateTime = LocalDateTime.now(),
        val letters: List<LetterId> = emptyList(),
    ): ReminderDetailState {
        val hasLetters: Boolean
            get() = letters.isNotEmpty()
    }
}

class ReminderDetailViewModel(
    private val application: Application,
    private val reminderId: ReminderId,
    private val reminderWithDetails: ReminderWithDetailsUseCase,
    private val acknowledgeReminder: AcknowledgeReminderUseCase,
) : StatefulViewModel<ReminderDetailState>(ReminderDetailState.Loading) {
    override fun load() {
        val hasNotificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            application.isPermissionGranted(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            true
        }

        val reminder = reminderWithDetails(reminderId)
        if (reminder == null) {
            update {
                ReminderDetailState.NotFound
            }
            return
        }

        var isAcknowledged = reminder.acknowledged
        if (!isAcknowledged && reminder.scheduledAt <= LocalDateTime.now()) {
            acknowledgeReminder(reminderId)
            isAcknowledged = true
        }

        update {
            ReminderDetailState.Detail(
                hasNotificationPermission = hasNotificationPermission,
                title = reminder.title,
                description = reminder.description,
                isAcknowledged = isAcknowledged,
                date = reminder.scheduledAt,
                letters = reminder.letters
            )
        }
    }

    fun handlePermissionResult(granted: Boolean) {
        update {
            if (this !is ReminderDetailState.Detail) return@update this
            copy(hasNotificationPermission = granted)
        }
    }

    fun acknowledge() = viewModelScope.launch {
        acknowledgeReminder(reminderId)
        update {
            if (this is ReminderDetailState.Detail) {
                copy(isAcknowledged = true)
            } else {
                this
            }
        }
    }
}
