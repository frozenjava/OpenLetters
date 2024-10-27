package net.frozendevelopment.openletters.ui.components

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.frozendevelopment.openletters.R
import net.frozendevelopment.openletters.data.sqldelight.models.LetterId
import net.frozendevelopment.openletters.data.sqldelight.models.ReminderId
import net.frozendevelopment.openletters.feature.reminder.detail.ReminderDetailState
import net.frozendevelopment.openletters.feature.reminder.detail.ReminderDetailView
import net.frozendevelopment.openletters.feature.reminder.detail.ReminderDetailViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ReminderPeekMenu(
    reminderId: ReminderId,
    onViewClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onAcknowledgeClick: () -> Unit,
    onLetterClicked: (LetterId) -> Unit,
    onDismissRequest: () -> Unit,
    viewModel: ReminderDetailViewModel = koinViewModel<ReminderDetailViewModel>(
        key = reminderId.value,
        parameters = { parametersOf(reminderId) },
    ),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current
    var showAcknowledgedDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmation: Boolean by remember { mutableStateOf(false) }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text(stringResource(R.string.are_you_sure)) },
            text = { Text(stringResource(R.string.delete_confirmation)) },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteClick()
                    onDismissRequest()
                }) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    if (showAcknowledgedDialog) {
        AlertDialog(
            onDismissRequest = { showAcknowledgedDialog = false },
            title = { Text(stringResource(R.string.acknowledge_reminder)) },
            text = { Text(stringResource(R.string.acknowledge_reminder_confirmation)) },
            confirmButton = {
                TextButton(onClick = {
                    showAcknowledgedDialog = false
                    onAcknowledgeClick()
                    onDismissRequest()
                }) {
                    Text(stringResource(R.string.acknowledge))
                }
            },
            dismissButton = {
                TextButton(onClick = { showAcknowledgedDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    PeekMenu(
        onDismissRequest = onDismissRequest,
        menuContent = {
            if (state is ReminderDetailState.Detail) {
                TextButton(onClick = {
                    onViewClick()
                    onDismissRequest()
                }) {
                    Text(stringResource(R.string.view))
                }

                if (!(state as ReminderDetailState.Detail).isAcknowledged) {
                    TextButton(onClick = {
                        onEditClick()
                        onDismissRequest()
                    }) {
                        Text(stringResource(R.string.edit))
                    }
                }

                TextButton(onClick = {
                    showDeleteConfirmation = true
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }) {
                    Text(stringResource(R.string.delete))
                }

                if (!(state as ReminderDetailState.Detail).isAcknowledged) {
                    TextButton(onClick = {
                        showAcknowledgedDialog = true
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }) {
                        Text(stringResource(R.string.acknowledge))
                    }
                }
            } else {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        },
    ) {
        ReminderDetailView(
            state = state,
            onLetterClicked = {
                onLetterClicked(it)
                onDismissRequest()
            },
            modifier = Modifier
                .fillMaxWidth(.95f)
                .fillMaxHeight(.5f),
        )
    }
}
