package net.frozendevelopment.openletters.feature.reminder.detail

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.frozendevelopment.openletters.data.sqldelight.models.LetterId
import net.frozendevelopment.openletters.data.sqldelight.models.ReminderId
import net.frozendevelopment.openletters.usecase.AcknowledgeReminderUseCase
import net.frozendevelopment.openletters.usecase.ReminderWithDetailsUseCase
import net.frozendevelopment.openletters.util.StatefulViewModel
import java.time.LocalDateTime

sealed interface ReminderDetailState {
    data object Loading: ReminderDetailState
    data object NotFound: ReminderDetailState

    @Immutable
    data class Detail(
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
    private val reminderId: ReminderId,
    private val reminderWithDetails: ReminderWithDetailsUseCase,
    private val acknowledgeReminder: AcknowledgeReminderUseCase,
) : StatefulViewModel<ReminderDetailState>(ReminderDetailState.Loading) {
    override fun load() {
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
                title = reminder.title,
                description = reminder.description,
                isAcknowledged = isAcknowledged,
                date = reminder.scheduledAt,
                letters = reminder.letters
            )
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
