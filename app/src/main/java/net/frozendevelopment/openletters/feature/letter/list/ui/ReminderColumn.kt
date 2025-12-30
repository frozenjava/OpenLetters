package net.frozendevelopment.openletters.feature.letter.list.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import net.frozendevelopment.openletters.R
import net.frozendevelopment.openletters.data.sqldelight.models.ReminderId
import net.frozendevelopment.openletters.ui.components.ReminderCell

@Composable
fun ReminderColumn(
    modifier: Modifier = Modifier,
    urgentReminders: List<ReminderId>,
    upcomingReminders: List<ReminderId>,
    onReminderClicked: (id: ReminderId, edit: Boolean) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 192.dp, top = 128.dp, start = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (urgentReminders.isNotEmpty()) {
            item {
                if (urgentReminders.isNotEmpty()) {
                    Text(
                        modifier = Modifier.fillMaxWidth(.95f),
                        text = stringResource(R.string.reminders),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }

            items(items = urgentReminders, key = { it.value }) {
                ReminderCell(
                    modifier = Modifier.fillMaxWidth(.95f),
                    id = it,
                    onClick = { onReminderClicked(it, false) },
                )
            }
        }

        if (upcomingReminders.isNotEmpty()) {
            item {
                if (upcomingReminders.isNotEmpty()) {
                    Text(
                        modifier = Modifier.fillMaxWidth(.95f),
                        text = stringResource(R.string.upcoming),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }

            items(items = upcomingReminders, key = { it.value }) {
                ReminderCell(
                    modifier = Modifier.fillMaxWidth(.95f),
                    id = it,
                    onClick = { onReminderClicked(it, false) },
                )
            }
        }
    }
}
