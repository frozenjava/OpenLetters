package net.frozendevelopment.openletters.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import net.frozendevelopment.openletters.data.sqldelight.ReminderQueries
import net.frozendevelopment.openletters.data.sqldelight.models.ReminderId
import net.frozendevelopment.openletters.extensions.dateString
import net.frozendevelopment.openletters.extensions.dateTimeString
import org.koin.compose.koinInject
import java.time.LocalDateTime

@Composable
fun ReminderCell(
    modifier: Modifier,
    id: ReminderId,
    onClick: (ReminderId) -> Unit,
    reminderQueries: ReminderQueries = koinInject()
) {
    val reminder = reminderQueries.reminderInfo(id).executeAsOneOrNull() ?: return

    ReminderCell(
        modifier = modifier,
        title = reminder.title,
        description = reminder.description,
        scheduledFor = reminder.scheduledFor,
        created = reminder.created,
        onClick = { onClick(id) }
    )
}

@Composable
fun ReminderCell(
    modifier: Modifier,
    title: String,
    description: String?,
    scheduledFor: LocalDateTime,
    created: LocalDateTime,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = modifier,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(Icons.Default.Alarm, contentDescription = null)
                Text(text = title, style = MaterialTheme.typography.titleLarge)
            }

            if (!description.isNullOrBlank()) {
                Text(text = description, style = MaterialTheme.typography.bodyLarge)
            }

            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Scheduled for: ")
                    }

                    withStyle(style = SpanStyle(fontWeight = FontWeight.Light)) {
                        append(scheduledFor.dateTimeString)
                    }
                },
                fontSize = MaterialTheme.typography.labelMedium.fontSize,
            )

            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Created: ")
                    }

                    withStyle(style = SpanStyle(fontWeight = FontWeight.Light)) {
                        append(created.dateString)
                    }
                },
                fontSize = MaterialTheme.typography.labelMedium.fontSize,
            )
        }
    }
}