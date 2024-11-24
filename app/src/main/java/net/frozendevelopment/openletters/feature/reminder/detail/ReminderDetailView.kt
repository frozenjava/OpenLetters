package net.frozendevelopment.openletters.feature.reminder.detail

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import net.frozendevelopment.openletters.R
import net.frozendevelopment.openletters.data.sqldelight.models.LetterId
import net.frozendevelopment.openletters.extensions.dateTimeString
import net.frozendevelopment.openletters.extensions.openAppSettings
import net.frozendevelopment.openletters.ui.components.LetterCell

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderDetailScreen(
    modifier: Modifier = Modifier,
    state: ReminderDetailState,
    onLetterClicked: (LetterId) -> Unit,
    onAcknowledgeClicked: () -> Unit,
    onBackClicked: () -> Unit,
) {
    val context = LocalContext.current
    var showAcknowledgedDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        CenterAlignedTopAppBar(
            title = { Text("Reminder") },
            navigationIcon = {
                IconButton(onClick = onBackClicked) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                    )
                }
            },
            actions = {
                if (state is ReminderDetailState.Detail && !state.isAcknowledged) {
                    TextButton(onClick = { showAcknowledgedDialog = true }) {
                        Text("Acknowledge")
                    }
                }
            },
        )

        if (state is ReminderDetailState.Detail && !state.hasNotificationPermission) {
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

        ReminderDetailView(
            modifier = Modifier.weight(1f),
            state = state,
            onLetterClicked = onLetterClicked,
        )
    }

    if (showAcknowledgedDialog) {
        AlertDialog(
            onDismissRequest = { showAcknowledgedDialog = false },
            title = { Text("Acknowledge Reminder") },
            text = {
                Text(
                    """
                    Are you sure you want to acknowledge this reminder?
                    If so you will not get a notification at the scheduled time.
                    """.trimIndent(),
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showAcknowledgedDialog = false
                    onAcknowledgeClicked()
                }) {
                    Text("Acknowledge")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAcknowledgedDialog = false }) {
                    Text("Cancel")
                }
            },
        )
    }
}

@Composable
fun ReminderDetailView(
    modifier: Modifier = Modifier,
    state: ReminderDetailState,
    onLetterClicked: (LetterId) -> Unit,
) {
    when (state) {
        is ReminderDetailState.Detail -> {
            ReminderDetail(
                modifier = modifier,
                state = state,
                onLetterClicked = onLetterClicked,
            )
        }
        ReminderDetailState.Loading -> Loading()
        ReminderDetailState.NotFound -> ReminderNotFound()
    }
}

@Composable
private fun ReminderDetail(
    modifier: Modifier = Modifier,
    state: ReminderDetailState.Detail,
    onLetterClicked: (LetterId) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 8.dp),
    ) {
        item {
            Text(
                text = state.title,
                style = MaterialTheme.typography.headlineLarge,
            )
        }

        item {
            Text(
                text =
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Scheduled for: ")
                        }

                        withStyle(style = SpanStyle(fontWeight = FontWeight.Light)) {
                            append(state.date.dateTimeString)
                        }
                    },
                style = MaterialTheme.typography.labelSmall,
            )
        }

        if (!state.description.isNullOrBlank()) {
            item {
                Text(
                    text = state.description,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }

        if (state.hasLetters) {
            item {
                HorizontalDivider()
                Text(
                    text = "Tagged Letters",
                    style = MaterialTheme.typography.labelLarge,
                )
            }

            items(
                items = state.letters,
                key = { id -> id.value },
            ) { id ->
                LetterCell(
                    modifier = Modifier.fillMaxWidth(),
                    id = id,
                    onClick = onLetterClicked,
                )
            }

            item {
                Spacer(modifier = Modifier.height(168.dp))
            }
        } else {
            item {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "No letters tagged.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}

@Composable
private fun ReminderNotFound() {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = "Reminder not found",
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun Loading() {
    CircularProgressIndicator()
}
