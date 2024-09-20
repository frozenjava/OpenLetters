package net.frozendevelopment.openletters.feature.letter.list.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.frozendevelopment.openletters.data.sqldelight.models.ReminderId
import net.frozendevelopment.openletters.ui.components.ReminderCell

@Composable
fun ReminderColumn(
    modifier: Modifier = Modifier,
    urgentReminders: List<ReminderId>,
    upComingReminders: List<ReminderId>,
    onReminderClicked: (ReminderId) -> Unit,
    onViewAllClicked: () -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 192.dp, top = 128.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (urgentReminders.isNotEmpty()) {
            item {
                if (urgentReminders.isNotEmpty()) {
                    Text(
                        modifier = Modifier.fillMaxWidth(.95f),
                        text = "Urgent Reminders",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            items(items = urgentReminders, key = { it.value }) {
                ReminderCell(
                    modifier = Modifier.fillMaxWidth(.95f),
                    id = it,
                    onClick = onReminderClicked
                )
            }
        }

        if (upComingReminders.isNotEmpty()) {
            item {
                if (upComingReminders.isNotEmpty()) {
                    Text(
                        modifier = Modifier.fillMaxWidth(.95f),
                        text = "Up and Coming Reminders",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            items(items = upComingReminders, key = { it.value }) {
                ReminderCell(
                    modifier = Modifier.fillMaxWidth(.95f),
                    id = it,
                    onClick = onReminderClicked
                )
            }
        }

        item {
            FilledTonalButton(
                modifier = Modifier.fillMaxWidth(.95f),
                onClick = onViewAllClicked
            ) {
                Text("View All")
            }
        }
    }
}
