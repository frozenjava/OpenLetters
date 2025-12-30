package net.frozendevelopment.openletters.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import net.frozendevelopment.openletters.R
import net.frozendevelopment.openletters.data.sqldelight.models.LetterId
import net.frozendevelopment.openletters.ui.ActionCard
import net.frozendevelopment.openletters.ui.theme.OpenLettersTheme
import net.frozendevelopment.openletters.usecase.LetterCellModel
import net.frozendevelopment.openletters.usecase.LetterCellUseCase
import org.koin.compose.koinInject
import java.time.LocalDateTime
import java.time.ZoneOffset

@Composable
fun ActionLetterCell(
    modifier: Modifier = Modifier,
    id: LetterId,
    onClick: (LetterId) -> Unit,
    onLongClick: ((id: LetterId) -> Unit)? = null,
    onEditClick: (LetterId) -> Unit,
    onDeleteClick: (LetterId) -> Unit,
    letterUseCase: LetterCellUseCase = koinInject(),
) {
    val haptic = LocalHapticFeedback.current
    var showDeleteConfirmation: Boolean by remember { mutableStateOf(false) }

    var letter: LetterCellModel? by remember { mutableStateOf(null) }

    LaunchedEffect(id) {
        letter = letterUseCase(id)
    }

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
            },
        )
    }

    letter?.let { letter ->
        SwipeCell(
            leftMenu = {
                IconButton(
                    modifier = it,
                    onClick = { onEditClick(id) },
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = stringResource(R.string.edit),
                    )
                }
            },
            rightMenu = {
                IconButton(
                    modifier = it,
                    onClick = {
                        showDeleteConfirmation = true
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = stringResource(R.string.delete),
                    )
                }
            },
        ) {
            LetterCell(
                modifier = modifier,
                letter = letter,
                categoryColors = letter.categoryColors,
                onClick = onClick,
                onLongClick = onLongClick?.let { { it(id) } },
            )
        }
    } ?: ListCellLoader(modifier)
}

@Composable
fun LetterCell(
    modifier: Modifier = Modifier,
    id: LetterId,
    onClick: (LetterId) -> Unit,
    onLongClick: ((id: LetterId) -> Unit)? = null,
    letterUseCase: LetterCellUseCase = koinInject(),
) {
    var letter: LetterCellModel? by remember { mutableStateOf(null) }

    LaunchedEffect(id) {
        letter = letterUseCase(id)
    }

    letter?.let { letter ->
        LetterCell(
            modifier = modifier,
            letter = letter,
            categoryColors = letter.categoryColors,
            onClick = onClick,
            onLongClick = onLongClick?.let { { it(id) } },
        )
    } ?: ListCellLoader(modifier)
}

@Composable
fun LetterCell(
    modifier: Modifier = Modifier,
    letter: LetterCellModel,
    categoryColors: List<Color> = emptyList(),
    onClick: (LetterId) -> Unit,
    onLongClick: (() -> Unit)? = null,
) {
    ActionCard(
        modifier = modifier,
        onClick = { onClick(letter.id) },
        onLongClick = onLongClick,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                if (!letter.sender.isNullOrBlank()) {
                    Text(
                        modifier = Modifier.fillMaxWidth(.5f),
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("${stringResource(R.string.from)} ")
                            }

                            withStyle(style = SpanStyle(fontWeight = FontWeight.Light)) {
                                append(letter.sender)
                            }
                        },
                        fontSize = MaterialTheme.typography.labelMedium.fontSize,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                if (!letter.recipient.isNullOrBlank()) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("${stringResource(R.string.to)} ")
                            }

                            withStyle(style = SpanStyle(fontWeight = FontWeight.Light)) {
                                append(letter.recipient)
                            }
                        },
                        fontSize = MaterialTheme.typography.labelMedium.fontSize,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            Text(
                letter.body ?: stringResource(R.string.nothing_to_show),
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
            )

            Row(
                modifier = Modifier.align(Alignment.End),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                categoryColors.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(color),
                    )
                }
            }
        }
    }
}

@Composable
fun ListCellLoader(
    modifier: Modifier = Modifier,
    colors: CardColors = CardDefaults.cardColors(),
) {
    val contentColor = colors.contentColor.copy(alpha = .25f)

    val addressColumn: @Composable RowScope.() -> Unit = {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            repeat(2) {
                Box(
                    Modifier
                        .fillMaxWidth(.9f / (it + 1))
                        .background(color = contentColor, RoundedCornerShape(4.dp))
                        .height(8.dp),
                )
            }
        }
    }

    Column(
        modifier = modifier.background(
            color = colors.containerColor,
            shape = MaterialTheme.shapes.medium,
        )
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            addressColumn()
            Spacer(modifier = Modifier.width(8.dp))
            addressColumn()
        }

        repeat(3) {
            Box(
                Modifier
                    .fillMaxWidth(.9f / (it + 1))
                    .background(color = contentColor, RoundedCornerShape(4.dp))
                    .height(16.dp),
            )
        }
    }
}

@Composable
private fun LetterCellPreview(letter: LetterCellModel) {
    OpenLettersTheme {
        Surface {
            LetterCell(
                modifier = Modifier.fillMaxWidth(),
                letter = letter,
                onClick = {},
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun LetterCell() {
    LetterCellPreview(
        letter =
            LetterCellModel(
                id = LetterId.random(),
                sender =
                    """
                    James Smith
                    123 Street Drive
                    Town City, State
                    """.trimIndent(),
                recipient =
                    """
                    Jane Jones
                    4321 Circle Road
                    Village, State
                    """.trimIndent(),
                body =
                    """
                    Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
                    """.trimIndent(),
                created = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC),
                lastModified = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC),
                categoryColors = listOf(Color.Cyan, Color.Gray, Color.Yellow),
            ),
    )
}

@Composable
@PreviewLightDark
private fun PoorlyFormattedAddress() {
    LetterCellPreview(
        letter =
            LetterCellModel(
                id = LetterId.random(),
                sender = "James Smith 123 Street Drive Town City, State",
                recipient = "Jane Jones 4321 Circle Road Village, State",
                body =
                    """
                    Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
                    """.trimIndent(),
                created = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC),
                lastModified = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC),
                categoryColors = listOf(Color.Cyan, Color.Gray, Color.Yellow),
            ),
    )
}

@Composable
@PreviewLightDark
private fun NoSender() {
    LetterCellPreview(
        letter =
            LetterCellModel(
                id = LetterId.random(),
                sender = null,
                recipient = "Jane Jones 4321 Circle Road Village, State",
                body =
                    """
                    Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
                    """.trimIndent(),
                created = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC),
                lastModified = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC),
                categoryColors = listOf(Color.Cyan, Color.Gray, Color.Yellow),
            ),
    )
}

@Composable
@PreviewLightDark
private fun NoRecipient() {
    LetterCellPreview(
        letter =
            LetterCellModel(
                id = LetterId.random(),
                sender = "James Smith 123 Street Drive Town City, State",
                recipient = null,
                body =
                    """
                    Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
                    """.trimIndent(),
                created = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC),
                lastModified = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC),
                categoryColors = listOf(Color.Cyan, Color.Gray, Color.Yellow),
            ),
    )
}

@Composable
@PreviewLightDark
private fun LetterCellLoader() {
    OpenLettersTheme {
        Surface {
            ListCellLoader(modifier = Modifier.fillMaxWidth())
        }
    }
}
