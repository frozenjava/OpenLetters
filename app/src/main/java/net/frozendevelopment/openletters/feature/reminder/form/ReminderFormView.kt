package net.frozendevelopment.openletters.feature.reminder.form

import android.Manifest
import android.app.Activity
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import net.frozendevelopment.openletters.R
import net.frozendevelopment.openletters.data.sqldelight.models.LetterId
import net.frozendevelopment.openletters.data.sqldelight.models.ReminderId
import net.frozendevelopment.openletters.extensions.openAppSettings
import net.frozendevelopment.openletters.feature.letter.detail.LetterDetailDestination
import net.frozendevelopment.openletters.ui.components.FormAppBar
import net.frozendevelopment.openletters.ui.components.LetterCell
import net.frozendevelopment.openletters.ui.components.SelectCell
import net.frozendevelopment.openletters.ui.navigation.LocalNavigator
import net.frozendevelopment.openletters.ui.theme.OpenLettersTheme
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.Module
import org.koin.core.parameter.parametersOf
import org.koin.dsl.navigation3.navigation
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Serializable
data class ReminderFormDestination(
    val reminderId: ReminderId? = null,
    val preselectedLetters: List<LetterId> = emptyList(),
) : NavKey

@OptIn(KoinExperimentalAPI::class)
fun Module.reminderFormNavigation() = navigation<ReminderFormDestination> { route ->
    val navigator = LocalNavigator.current
    val coroutineScope = rememberCoroutineScope()
    val viewModel =
        koinViewModel<ReminderFormViewModel> {
            parametersOf(route.reminderId, route.preselectedLetters)
        }
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    val notificationPermissionResultLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = viewModel::handlePermissionResult,
        )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !state.hasNotificationPermission) {
        LaunchedEffect(Unit) {
            notificationPermissionResultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Surface {
        ReminderFormView(
            modifier = Modifier.fillMaxSize(),
            state = state,
            onTitleChanged = viewModel::setTitle,
            onDescriptionChanged = viewModel::setDescription,
            onDateSelected = viewModel::selectDate,
            onTimeSelected = viewModel::selectTime,
            toggleLetterSelect = viewModel::toggleLetterSelect,
            onLetterClicked = { navigator.navigate(LetterDetailDestination(it)) },
            openDialog = viewModel::openDialog,
            onBackClicked = navigator::onBackPressed,
            onSaveClicked = {
                coroutineScope.launch {
                    if (viewModel.save()) {
                        navigator.onBackPressed()
                    }
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderFormView(
    modifier: Modifier = Modifier,
    state: ReminderFormState,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onDateSelected: (Long) -> Unit,
    onTimeSelected: (Int, Int) -> Unit,
    toggleLetterSelect: (LetterId) -> Unit,
    onLetterClicked: (LetterId) -> Unit,
    openDialog: (ReminderFormState.Dialog?) -> Unit,
    onSaveClicked: () -> Unit,
    onBackClicked: () -> Unit,
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        FormAppBar(
            isSavable = state.isSavable,
            onBackClicked = onBackClicked,
            onSaveClicked = onSaveClicked,
            title = { Text(stringResource(R.string.reminder)) },
        )

        if (!state.hasNotificationPermission) {
            Surface(
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                color = MaterialTheme.colorScheme.errorContainer,
            ) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(stringResource(R.string.notification_permission_required))
                    TextButton(onClick = { (context as? Activity)?.openAppSettings() }) {
                        Text(stringResource(R.string.grant))
                    }
                }
            }
        }

        LazyColumn(modifier = Modifier.imePadding()) {
            item {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(.95f),
                    value = state.title,
                    label = { Text(stringResource(R.string.title)) },
                    onValueChange = onTitleChanged,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Words),
                    supportingText = {
                        if (state.titleError.isNotBlank()) {
                            Text(state.titleError)
                        } else {
                            Text(state.titleHelperText)
                        }
                    },
                    isError = state.titleError.isNotBlank(),
                )
            }

            item {
                OutlinedTextField(
                    value = state.formattedDate,
                    onValueChange = { },
                    label = { Text(stringResource(R.string.date)) },
                    supportingText = {
                        if (state.dateError.isNotBlank()) {
                            Text(state.dateError)
                        } else {
                            Text(stringResource(R.string.select_a_date))
                        }
                    },
                    readOnly = true,
                    isError = state.dateError.isNotBlank(),
                    trailingIcon = {
                        IconButton(onClick = { openDialog(ReminderFormState.Dialog.DATE) }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = stringResource(R.string.select_date),
                            )
                        }
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth(.95f)
                            .onFocusChanged {
                                if (it.isFocused) {
                                    openDialog(ReminderFormState.Dialog.DATE)
                                }
                            },
                )
            }

            item {
                OutlinedTextField(
                    value = state.formattedTime,
                    onValueChange = { },
                    label = { Text(stringResource(R.string.time)) },
                    supportingText = {
                        if (state.dateError.isNotBlank()) {
                            Text(state.dateError)
                        } else {
                            Text(stringResource(R.string.select_a_time))
                        }
                    },
                    readOnly = true,
                    isError = state.dateError.isNotBlank(),
                    trailingIcon = {
                        IconButton(onClick = { openDialog(ReminderFormState.Dialog.TIME) }) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = stringResource(R.string.select_time),
                            )
                        }
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth(.95f)
                            .onFocusChanged {
                                if (it.isFocused) {
                                    openDialog(ReminderFormState.Dialog.TIME)
                                }
                            },
                )
            }

            item {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(.95f),
                    value = state.description,
                    label = { Text(stringResource(R.string.description_optional)) },
                    onValueChange = onDescriptionChanged,
                    supportingText = { Text(state.descriptionHelperText) },
                    keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Sentences),
                    isError = !state.isValidDescription,
                    minLines = 3,
                    maxLines = 5,
                )
            }

            if (state.selectedLetters.isNotEmpty()) {
                item {
                    Text(
                        modifier =
                            Modifier
                                .fillMaxWidth(.95f)
                                .padding(vertical = 16.dp),
                        text = stringResource(R.string.tag_letters),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }

                items(
                    items = state.selectedLetters,
                    key = { it.value },
                ) {
                    LetterCell(
                        modifier = Modifier.fillMaxWidth(.95f),
                        id = it,
                        onClick = { onLetterClicked(it) },
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    Button(
                        modifier = Modifier.fillMaxWidth(.95f),
                        onClick = { openDialog(ReminderFormState.Dialog.LETTERS) },
                    ) {
                        Text(stringResource(R.string.tag_additional_letters))
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(168.dp))
                }
            } else {
                item {
                    Button(
                        modifier = Modifier.fillMaxWidth(.95f),
                        onClick = { openDialog(ReminderFormState.Dialog.LETTERS) },
                    ) {
                        Text(stringResource(R.string.tag_letters))
                    }
                }
            }
        }
    }

    if (state.showDatePicker) {
        DatePickerDialog(
            state.datePickerState,
            onDoneClicked = {
                openDialog(null)
                if (it != null) {
                    onDateSelected(it)
                }
                focusManager.clearFocus()
            },
        ) {
            openDialog(null)
            focusManager.clearFocus()
        }
    }

    if (state.showTimePicker) {
        TimePickerDialog(
            timePickerState = state.timePickerState,
            onDoneClicked = { hour, minute ->
                openDialog(null)
                onTimeSelected(hour, minute)
                focusManager.clearFocus()
            },
        ) {
            openDialog(null)
            focusManager.clearFocus()
        }
    }

    if (state.showLetterSelector) {
        SelectLetterDialog(
            letters = state.letters,
            selectedLetters = state.selectedLetters,
            onDismissRequest = { openDialog(null) },
            toggleLetterSelect = toggleLetterSelect,
        )
    }
}

private fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerDialog(
    datePickerState: DatePickerState,
    bottomSheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    onDoneClicked: (Long?) -> Unit,
    onDismissRequest: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = bottomSheetState,
        dragHandle = null,
    ) {
        DatePicker(
            state = datePickerState,
            showModeToggle = false,
            title = {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Select a Date")
                    Button(onClick = { onDoneClicked(datePickerState.selectedDateMillis) }) {
                        Text("Done")
                    }
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    bottomSheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    timePickerState: TimePickerState,
    onDoneClicked: (Int, Int) -> Unit,
    onDismissRequest: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = bottomSheetState,
        dragHandle = null,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Select a Time")
                Button(onClick = { onDoneClicked(timePickerState.hour, timePickerState.minute) }) {
                    Text("Done")
                }
            }
            TimeInput(state = timePickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectLetterDialog(
    letters: List<LetterId>,
    selectedLetters: List<LetterId>,
    onDismissRequest: () -> Unit,
    toggleLetterSelect: (LetterId) -> Unit,
    bottomSheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = bottomSheetState,
        dragHandle = null,
    ) {
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 28.dp, bottom = 168.dp),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(.75f),
        ) {
            item {
                Column {
                    Text(
                        "Tap to select or deselect a letter",
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }

            items(items = letters, key = { it.value }) {
                val isSelected = selectedLetters.contains(it)
                SelectCell(
                    modifier = Modifier.fillMaxWidth(.95f),
                    isSelected = isSelected,
                    onClick = { toggleLetterSelect(it) },
                ) {
                    LetterCell(id = it, onClick = { toggleLetterSelect(it) })
                }
            }
        }

        Button(
            modifier =
                Modifier
                    .fillMaxWidth(.95f)
                    .align(Alignment.CenterHorizontally),
            onClick = onDismissRequest,
        ) {
            Text("Done")
        }
    }
}

@Composable
private fun ReminderFormPreview(state: ReminderFormState) {
    OpenLettersTheme {
        Surface {
            ReminderFormView(
                modifier = Modifier.fillMaxSize(),
                state = state,
                onTitleChanged = {},
                onDescriptionChanged = {},
                onDateSelected = {},
                onTimeSelected = { _, _ -> },
                toggleLetterSelect = {},
                onLetterClicked = {},
                onSaveClicked = {},
                onBackClicked = {},
                openDialog = {},
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun BlankForm() {
    ReminderFormPreview(
        state = ReminderFormState(),
    )
}

@Composable
@PreviewLightDark
private fun DateSelector() {
    ReminderFormPreview(
        state = ReminderFormState(shownDialog = ReminderFormState.Dialog.DATE),
    )
}
