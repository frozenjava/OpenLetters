package net.frozendevelopment.openletters.feature.letter.peek

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.frozendevelopment.openletters.R
import net.frozendevelopment.openletters.data.sqldelight.migrations.Category
import net.frozendevelopment.openletters.data.sqldelight.models.LetterId
import net.frozendevelopment.openletters.data.sqldelight.models.ReminderId
import net.frozendevelopment.openletters.ui.components.BrokenImageView
import net.frozendevelopment.openletters.ui.components.CategoryPill
import net.frozendevelopment.openletters.ui.components.LazyImageView
import net.frozendevelopment.openletters.ui.components.PagerIndicator
import net.frozendevelopment.openletters.ui.components.PeekMenu
import net.frozendevelopment.openletters.ui.components.ReminderCell
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun LetterPeekMenu(
    letterId: LetterId,
    onViewClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onAddReminderClicked: () -> Unit,
    onDismissRequest: () -> Unit,
    viewModel: LetterPeekViewModel =
        koinViewModel(
            key = letterId.value,
            parameters = { parametersOf(letterId) },
        ),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    var showDeleteConfirmation: Boolean by remember { mutableStateOf(false) }
    val horizontalPagerState = PagerState { state.pagerCount }
    val haptic = LocalHapticFeedback.current

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
            },
        )
    }

    PeekMenu(
        onDismissRequest = onDismissRequest,
        menuContent = {
            PeekMenu(
                onViewClick = onViewClick,
                onEditClick = onEditClick,
                onDeleteClick = {
                    showDeleteConfirmation = true
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                },
                onAddReminderClicked = onAddReminderClicked,
            )
        },
    ) {
        Box {
            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                state = horizontalPagerState,
                verticalAlignment = Alignment.Top,
            ) { page ->
                if (!state.transcript.isNullOrBlank() && page == 0) {
                    TranscriptionText(modifier = Modifier.padding(8.dp), body = state.transcript!!)
                } else if (page == horizontalPagerState.pageCount - 1) {
                    SenderRecipientCategoriesAndReminders(
                        modifier = Modifier.padding(8.dp),
                        sender = state.sender,
                        recipient = state.recipient,
                        categories = state.selectedCategories,
                        reminders = state.reminders,
                    )
                } else {
                    val pageIndexOffset: Int = if (!state.transcript.isNullOrBlank()) 1 else 0
                    val documentUri = state.documents.values.toList()[page - pageIndexOffset]
                    if (documentUri != null) {
                        LazyImageView(
                            modifier = Modifier.fillMaxSize(),
                            uri = documentUri,
                        )
                    } else {
                        BrokenImageView(modifier = Modifier.fillMaxSize())
                    }
                }
            }

            PagerIndicator(
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp),
                currentPage = horizontalPagerState.currentPage,
                pageCount = horizontalPagerState.pageCount,
            )
        }
    }
}

@Composable
private fun PeekMenu(
    onViewClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onAddReminderClicked: () -> Unit,
) {
    TextButton(onViewClick) {
        Text(stringResource(R.string.view))
    }
    TextButton(onEditClick) {
        Text(stringResource(R.string.edit))
    }
    TextButton(onDeleteClick) {
        Text(stringResource(R.string.delete))
    }
    TextButton(onAddReminderClicked) {
        Text(stringResource(R.string.create_reminder))
    }
}

@Composable
private fun TranscriptionText(
    modifier: Modifier = Modifier,
    body: String,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 128.dp),
    ) {
        item {
            Text(
                "Transcription",
                style = MaterialTheme.typography.titleLarge,
            )
        }

        item {
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
        }

        item {
            Text(
                body,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SenderRecipientCategoriesAndReminders(
    modifier: Modifier = Modifier,
    sender: String?,
    recipient: String?,
    categories: List<Category>,
    reminders: List<ReminderId>,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item(key = "sender-recipient") {
            Text(
                modifier = Modifier.fillMaxWidth(.5f),
                text =
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("From: ")
                        }

                        withStyle(style = SpanStyle(fontWeight = FontWeight.Light)) {
                            append(sender ?: "Unknown")
                        }
                    },
                fontSize = MaterialTheme.typography.labelLarge.fontSize,
            )
            Text(
                text =
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("To: ")
                        }

                        withStyle(style = SpanStyle(fontWeight = FontWeight.Light)) {
                            append(recipient ?: "Unknown")
                        }
                    },
                fontSize = MaterialTheme.typography.labelLarge.fontSize,
            )
        }

        item(key = "categories") {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                categories.forEach {
                    CategoryPill(category = it, isSelected = true)
                }
            }
        }

        if (reminders.isNotEmpty()) {
            item(key = "reminders-label") {
                Text("Reminders", fontWeight = FontWeight.Bold)
            }

            items(
                items = reminders,
                key = { it.value },
            ) {
                ReminderCell(
                    modifier =
                        Modifier
                            .padding(horizontal = 8.dp)
                            .fillMaxWidth(),
                    id = it,
                    onClick = {},
                )
            }
        }
    }
}
