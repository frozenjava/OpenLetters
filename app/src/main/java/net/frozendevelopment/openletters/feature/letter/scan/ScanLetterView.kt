package net.frozendevelopment.openletters.feature.letter.scan

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.outlined.DocumentScanner
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import net.frozendevelopment.openletters.R
import net.frozendevelopment.openletters.data.sqldelight.migrations.Category
import net.frozendevelopment.openletters.data.sqldelight.models.CategoryId
import net.frozendevelopment.openletters.data.sqldelight.models.DocumentId
import net.frozendevelopment.openletters.feature.letter.scan.ui.CategoryPicker
import net.frozendevelopment.openletters.feature.letter.scan.ui.ScanAppBar
import net.frozendevelopment.openletters.feature.letter.scan.ui.ScannableTextField
import net.frozendevelopment.openletters.ui.components.BrokenImageView
import net.frozendevelopment.openletters.ui.components.LazyImageView
import net.frozendevelopment.openletters.ui.theme.OpenLettersTheme
import java.time.LocalDateTime

@Composable
fun ScanLetterView(
    modifier: Modifier = Modifier,
    state: ScanState,
    canNavigateBack: Boolean,
    setSender: (String) -> Unit,
    setRecipient: (String) -> Unit,
    toggleCategory: (Category) -> Unit,
    setTranscript: (String) -> Unit,
    openLetterScanner: () -> Unit,
    openSenderScanner: () -> Unit,
    openRecipientScanner: () -> Unit,
    onSaveClicked: () -> Unit,
    onBackClicked: () -> Unit,
    onDeleteDocumentClicked: (DocumentId) -> Unit,
    onCreateCategoryClicked: () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ScanAppBar(
            canNavigateBack = canNavigateBack,
            canLeaveSafely = state.canLeaveSafely,
            isSavable = state.isSavable,
            onSaveClicked = onSaveClicked,
            onBackClicked = onBackClicked,
        )

        ScannableTextField(
            modifier = Modifier.fillMaxWidth(.95f),
            value = state.sender ?: "",
            label = "Sender",
            placeholder =
                """
                Jane Doe 123 Street Drive
                """.trimIndent(),
            suggestions = state.possibleSenders,
            onValueChange = setSender,
            onScanClick = openSenderScanner,
        )

        ScannableTextField(
            modifier = Modifier.fillMaxWidth(.95f),
            value = state.recipient ?: "",
            label = "Recipient",
            placeholder =
                """
                Jane Doe 123 Street Drive
                """.trimIndent(),
            suggestions = state.possibleRecipients,
            onValueChange = setRecipient,
            onScanClick = openRecipientScanner,
        )

        if (state.documents.isEmpty()) {
            OutlinedCard(
                onClick = openLetterScanner,
            ) {
                Column(
                    modifier =
                        Modifier
                            .weight(1f, fill = true)
                            .fillMaxWidth(.95f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        modifier =
                            Modifier
                                .padding(16.dp)
                                .size(64.dp),
                        imageVector = Icons.Outlined.DocumentScanner,
                        contentDescription = "Scan",
                    )
                    Text(
                        text = "Scan Letter",
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
            }
        } else {
            CategoryPicker(
                modifier = Modifier.fillMaxWidth(),
                categories = state.categoryMap,
                toggleCategory = toggleCategory,
                onCreateClicked = onCreateCategoryClicked,
            )
            TranscriptAndDocuments(
                modifier = Modifier.fillMaxSize(),
                state = state,
                openLetterScanner = openLetterScanner,
                onDeleteDocumentClicked = onDeleteDocumentClicked,
                onEditTranscript = setTranscript,
                onImageClick = {
                    onSaveClicked()
                    onDeleteDocumentClicked(DocumentId.random())
                },
            )
        }
    }
}

@Composable
private fun TranscriptAndDocuments(
    modifier: Modifier = Modifier,
    state: ScanState,
    openLetterScanner: () -> Unit,
    onDeleteDocumentClicked: (DocumentId) -> Unit,
    onEditTranscript: (String) -> Unit,
    onImageClick: (Uri) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 128.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier =
                        Modifier
                            .weight(1f)
                            .padding(16.dp),
                    text = stringResource(R.string.transcription),
                    style = MaterialTheme.typography.titleLarge,
                )

                if (state.isCreatingTranscript) {
                    CircularProgressIndicator()
                } else {
                    IconButton({ onEditTranscript(state.transcript ?: "") }) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Scan",
                        )
                    }
                }
            }

            if (state.isCreatingTranscript) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    repeat(5) { index ->
                        Box(
                            Modifier
                                .padding(horizontal = 16.dp)
                                .background(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(8.dp))
                                .height(24.dp)
                                .fillMaxWidth(1f - (index * .1f)),
                        )
                    }
                }
            } else {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = state.transcript ?: stringResource(R.string.no_transcript_available),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 64.dp),
            ) {
                items(
                    items = state.documents.keys.toList(),
                    key = { it.value },
                ) {
                    val documentUri = state.documents[it]
                    if (documentUri != null) {
                        Box {
                            LazyImageView(
                                modifier =
                                    Modifier
                                        .size(128.dp)
                                        .clickable(onClick = { onImageClick(documentUri) }),
                                uri = documentUri,
                            )
                            IconButton(
                                modifier = Modifier.align(Alignment.TopEnd),
                                onClick = { onDeleteDocumentClicked(it) },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.RemoveCircleOutline,
                                    contentDescription = "Remove",
                                    tint = Color.Red,
                                )
                            }
                        }
                    } else {
                        Box {
                            BrokenImageView(modifier = Modifier.size(128.dp))
                            IconButton(
                                modifier = Modifier.align(Alignment.TopEnd),
                                onClick = { onDeleteDocumentClicked(it) },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.RemoveCircleOutline,
                                    contentDescription = "Remove",
                                    tint = Color.Red,
                                )
                            }
                        }
                    }
                }

                item {
                    OutlinedCard(
                        modifier = Modifier.size(128.dp),
                        onClick = openLetterScanner,
                        colors =
                            CardDefaults.outlinedCardColors(
                                contentColor = MaterialTheme.colorScheme.primary,
                            ),
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Column(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Icon(
                                    modifier = Modifier.size(64.dp),
                                    imageVector = Icons.Outlined.DocumentScanner,
                                    contentDescription = "Scan",
                                )

                                Text(text = "Add Document")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScanFormPreview(state: ScanState) {
    OpenLettersTheme {
        Surface {
            ScanLetterView(
                canNavigateBack = false,
                state = state,
                toggleCategory = {},
                setSender = {},
                setRecipient = {},
                setTranscript = {},
                openLetterScanner = {},
                openSenderScanner = {},
                openRecipientScanner = {},
                onSaveClicked = {},
                onBackClicked = {},
                onDeleteDocumentClicked = {},
                onCreateCategoryClicked = {},
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun EmptyScanForm() {
    ScanFormPreview(ScanState())
}

@Composable
@PreviewLightDark
private fun FilledOutScanForm() {
    ScanFormPreview(
        ScanState(
            sender = "<NAME>",
            recipient = "<NAME>",
            transcript = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
            newDocuments =
                mapOf(
                    DocumentId.random() to Uri.EMPTY,
                    DocumentId.random() to Uri.EMPTY,
                ),
            existingDocuments =
                mapOf(
                    DocumentId.random() to Uri.EMPTY,
                ),
            categories =
                listOf(
                    Category(
                        id = CategoryId.random(),
                        label = "Category 1",
                        color = Color.Red,
                        priority = 0,
                        created = LocalDateTime.now(),
                        lastModified = LocalDateTime.now(),
                    ),
                    Category(
                        id = CategoryId.random(),
                        label = "Category 2",
                        color = Color.Blue,
                        priority = 1,
                        created = LocalDateTime.now(),
                        lastModified = LocalDateTime.now(),
                    ),
                ),
            selectedCategories =
                setOf(
                    Category(
                        id = CategoryId.random(),
                        label = "Category 1",
                        color = Color.Red,
                        priority = 0,
                        created = LocalDateTime.now(),
                        lastModified = LocalDateTime.now(),
                    ),
                ),
        ),
    )
}

@Composable
@PreviewLightDark
private fun LoadingTranscript() {
    ScanFormPreview(
        ScanState(
            isCreatingTranscript = true,
            newDocuments =
                mapOf(
                    DocumentId.random() to Uri.EMPTY,
                ),
            categories =
                listOf(
                    Category(
                        id = CategoryId.random(),
                        label = "Category 1",
                        color = Color.Red,
                        priority = 0,
                        created = LocalDateTime.now(),
                        lastModified = LocalDateTime.now(),
                    ),
                    Category(
                        id = CategoryId.random(),
                        label = "Category 2",
                        color = Color.Blue,
                        priority = 1,
                        created = LocalDateTime.now(),
                        lastModified = LocalDateTime.now(),
                    ),
                ),
        ),
    )
}
