package net.frozendevelopment.openletters.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import net.frozendevelopment.openletters.R
import net.frozendevelopment.openletters.data.sqldelight.ReminderInfo
import net.frozendevelopment.openletters.data.sqldelight.ReminderQueries
import net.frozendevelopment.openletters.data.sqldelight.models.ReminderId
import net.frozendevelopment.openletters.extensions.contrastColor
import net.frozendevelopment.openletters.extensions.dateString
import net.frozendevelopment.openletters.extensions.dateTimeString
import org.koin.compose.koinInject
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Composable
fun ActionReminderCell(
    modifier: Modifier = Modifier,
    id: ReminderId,
    onClick: (id: ReminderId) -> Unit,
    onLongClick: ((id: ReminderId) -> Unit)? = null,
    onEditClick: (id: ReminderId) -> Unit,
    onDeleteClick: (ReminderId) -> Unit,
    reminderQueries: ReminderQueries = koinInject()
) {
    // TODO: Lazily load this and show a loading placeholder or an error if it fails to load
    val reminder = reminderQueries.reminderInfo(id).executeAsOneOrNull() ?: return
    val haptic = LocalHapticFeedback.current

    var showDeleteConfirmation: Boolean by remember { mutableStateOf(false) }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text(stringResource(R.string.are_you_sure)) },
            text = { Text(stringResource(R.string.delete_confirmation)) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirmation = false
                    onDeleteClick(id)
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

    SwipeCell(
        leftMenu = reminder.takeIf { !it.acknowledged }?.let {
            {
                IconButton(
                    modifier = it,
                    onClick = { onEditClick(id) }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = stringResource(R.string.edit)
                    )
                }
            }
        },
        rightMenu = {
            IconButton(
                modifier = it,
                    onClick = {
                    showDeleteConfirmation = true
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = stringResource(R.string.delete)
                )
            }
        }
    ) {
        ReminderCell(
            modifier = modifier
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        awaitFirstDown().also {
                            it.consume()
                        }
                    }
                },
            title = reminder.title,
            description = reminder.description,
            scheduledFor = reminder.scheduledFor,
            created = reminder.created,
            onClick = { onClick(id) },
            onLongClick = onLongClick?.let {{
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                it(id)
            }},
            containerColor = reminder.cardColor,
            contentColor = reminder.cardColor.contrastColor
        )
    }
}

@Composable
fun ReminderCell(
    modifier: Modifier = Modifier,
    id: ReminderId,
    onClick: (id: ReminderId) -> Unit,
    onLongClick: ((id: ReminderId) -> Unit)? = null,
    reminderQueries: ReminderQueries = koinInject()
) {
    val reminder = reminderQueries.reminderInfo(id).executeAsOneOrNull() ?: return
    val haptic = LocalHapticFeedback.current

    ReminderCell(
        modifier = modifier
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    awaitFirstDown().also {
                        it.consume()
                    }
                }
            },
        title = reminder.title,
        description = reminder.description,
        scheduledFor = reminder.scheduledFor,
        created = reminder.created,
        onClick = { onClick(id) },
        onLongClick = onLongClick?.let {{
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            it(id)
        }},
        containerColor = reminder.cardColor,
        contentColor = reminder.cardColor.contrastColor
    )
}

@Composable
private fun ReminderCell(
    modifier: Modifier,
    title: String,
    description: String?,
    scheduledFor: LocalDateTime,
    created: LocalDateTime,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    containerColor: Color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    CompositionLocalProvider(
        LocalContentColor provides contentColor,
    ) {
        Box(
            modifier = modifier
                .minimumInteractiveComponentSize()
                .background(
                    color = containerColor,
                    shape = MaterialTheme.shapes.medium
                )
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { onClick() },
                        onLongPress = onLongClick?.let { { it() } }
                    )
                }
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
}

private val ReminderInfo.cardColor: Color
    @Composable get() {
        val startColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)

        if (acknowledged) {
            return startColor
        }

        val today = LocalDate.now()
        val diff = ChronoUnit.DAYS.between(today, scheduledFor).toInt()
        val maxDiff = 5
        val normalizedDiff = when {
            diff < 0 -> 1f // Past dates are fully red
            diff > maxDiff -> .1f // Dates further than maxDiff are the start color
            else -> maxOf(.15f, minOf(.75f, 1 - (diff.toFloat() / maxDiff))) // Interpolate for dates within range
        }

        return lerp(startColor, Color.Red, normalizedDiff)
    }
