package net.frozendevelopment.openletters.feature.reminder.form

import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.frozendevelopment.openletters.data.sqldelight.models.LetterId
import net.frozendevelopment.openletters.extensions.dateString
import net.frozendevelopment.openletters.usecase.CreateReminderUseCase
import net.frozendevelopment.openletters.usecase.SearchLettersUseCase
import net.frozendevelopment.openletters.util.StatefulViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Immutable
data class ReminderFormState(
    val title: String = "",
    val titleError: String = "",
    val description: String = "",
    val selectedLetters: List<LetterId> = emptyList(),
    val letters: List<LetterId> = emptyList(),
    val shownDialog: Dialog? = null,
    val dateError: String = "",
    val selectedDate: LocalDateTime = LocalDate.now()
            .plusDays(1)
            .atTime(9, 0),
) {
    enum class Dialog {
        DATE, TIME, LETTERS
    }

    val isSavable: Boolean
        get() {
            // return true if title, and description are valid and date is in the future
            return isValidTitle && isValidDescription && selectedDate.isAfter(LocalDateTime.now())
        }

    private val isValidTitle: Boolean
        get() = title.length in 1..25

    val isValidDescription: Boolean
        get() = description.length < 100

    val titleHelperText: String
        get() = "${title.length}/25"

    val descriptionHelperText: String
        get() = "${description.length}/100"

    val showDatePicker: Boolean
        get() = shownDialog == Dialog.DATE

    val showTimePicker: Boolean
        get() = shownDialog == Dialog.TIME

    val showLetterSelector: Boolean
        get() = shownDialog == Dialog.LETTERS

    val formattedDate: String
        get() = selectedDate.dateString

    val formattedTime: String
        get() {
            val formatter = DateTimeFormatter.ofPattern("h:mm a")
            return selectedDate.format(formatter)
        }

    @OptIn(ExperimentalMaterial3Api::class)
    val timePickerState: TimePickerState
        @Composable get() {
            return rememberTimePickerState(
                initialHour = selectedDate.hour,
                initialMinute = selectedDate.minute,
                is24Hour = false
            )
        }

    @OptIn(ExperimentalMaterial3Api::class)
    val datePickerState: DatePickerState
        @Composable get() {
            return rememberDatePickerState(
                initialSelectedDateMillis = selectedDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                selectableDates = object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                        val instant = Instant.ofEpochMilli(utcTimeMillis)
                        val dateUTC = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"))
                        val today = LocalDate.now(ZoneId.systemDefault())

                        val date = LocalDate.of(dateUTC.year, dateUTC.month, dateUTC.dayOfMonth)

                        // Check if date is within the valid range
                        return date.isEqual(today) || date.isAfter(today)
                    }

                    override fun isSelectableYear(year: Int): Boolean {
                        val currentYear = LocalDate.now().year
                        return year in currentYear..currentYear + 1
                    }
                }
            )
        }
}

class ReminderFormViewModel(
    private val searchLetters: SearchLettersUseCase,
    private val createReminder: CreateReminderUseCase,
) : StatefulViewModel<ReminderFormState>(ReminderFormState()) {

    fun load() {
        viewModelScope.launch {
            update { copy(letters = searchLetters("")) }
        }
    }

    fun setTitle(title: String) = viewModelScope.launch {
        update { copy(
            title = title,
            titleError = if (title.length > 15 || title.isBlank()) "Title must be between 1 and 25 characters" else ""
        )}
    }

    fun setDescription(description: String) = viewModelScope.launch {
        update { copy(description = description) }
    }

    fun toggleLetterSelect(letterId: LetterId) = viewModelScope.launch {
        val letterSet = state.selectedLetters.toMutableSet().let {
            if (state.selectedLetters.contains(letterId)) {
                it - letterId
            } else {
                it + letterId
            }
        }

        update { copy(selectedLetters = letterSet.toList()) }
    }

    fun selectDate(dateInMillis: Long) = viewModelScope.launch {
        val instant = Instant.ofEpochMilli(dateInMillis)

        val dateUTC = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"))
            .withHour(state.selectedDate.hour)
            .withMinute(state.selectedDate.minute)

        val dateLocal = LocalDateTime.of(
            dateUTC.year,
            dateUTC.monthValue,
            dateUTC.dayOfMonth,
            dateUTC.hour,
            dateUTC.minute
        )

        update { copy(
            selectedDate = dateLocal,
            dateError = if (!dateLocal.isAfter(LocalDateTime.now())) "Date must be in the future" else ""
        )}
    }

    fun selectTime(hour: Int, minute: Int) = viewModelScope.launch {
        val date = state.selectedDate
            .withHour(hour)
            .withMinute(minute)

        update { copy(
            selectedDate = date,
            dateError = if (!date.isAfter(LocalDateTime.now())) "Date must be in the future" else ""
        )}
    }

    fun openDialog(dialog: ReminderFormState.Dialog?) = viewModelScope.launch {
        update { copy(shownDialog = dialog) }
    }

    fun save(): Boolean {
        if (!state.isSavable) return false

        createReminder(
            title = state.title,
            description = state.description.ifBlank { null },
            scheduledFor = state.selectedDate,
            letters = state.selectedLetters.toSet()
        )

        return true
    }
}
