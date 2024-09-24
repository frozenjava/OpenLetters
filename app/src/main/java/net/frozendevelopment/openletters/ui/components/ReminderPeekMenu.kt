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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.frozendevelopment.openletters.data.sqldelight.models.LetterId
import net.frozendevelopment.openletters.data.sqldelight.models.ReminderId
import net.frozendevelopment.openletters.feature.reminder.detail.ReminderDetailState
import net.frozendevelopment.openletters.feature.reminder.detail.ReminderDetailView
import net.frozendevelopment.openletters.feature.reminder.detail.ReminderDetailViewModel
import net.frozendevelopment.openletters.usecase.AcknowledgeReminderUseCase
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
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
    var showAcknowledgedDialog by remember { mutableStateOf(false) }

    if (showAcknowledgedDialog) {
        AlertDialog(
            onDismissRequest = { showAcknowledgedDialog = false },
            title = { Text("Acknowledge Reminder") },
            text = {
                Text(
                    """
                    Are you sure you want to acknowledge this reminder?
                    If so you will not get a notification at the scheduled time.
                    """.trimIndent()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showAcknowledgedDialog = false
                    onAcknowledgeClick()
                }) {
                    Text("Acknowledge")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAcknowledgedDialog = false }) {
                    Text("Cancel")
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
                    Text("View")
                }

                if (!(state as ReminderDetailState.Detail).isAcknowledged) {
                    TextButton(onClick = {
                        onEditClick()
                        onDismissRequest()
                    }) {
                        Text("Edit")
                    }
                }

                TextButton(onClick = {
                    onDeleteClick()
                    onDismissRequest()
                }) {
                    Text("Delete")
                }

                if (!(state as ReminderDetailState.Detail).isAcknowledged) {
                    TextButton(onClick = {
                        showAcknowledgedDialog = true
                        onDismissRequest()
                    }) {
                        Text("Acknowledge")
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
