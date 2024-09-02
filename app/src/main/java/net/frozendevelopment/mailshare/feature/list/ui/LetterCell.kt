package net.frozendevelopment.mailshare.feature.list.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.frozendevelopment.mailshare.data.sqldelight.LetterInfo
import net.frozendevelopment.mailshare.data.sqldelight.models.LetterId
import net.frozendevelopment.mailshare.ui.theme.MailShareTheme
import net.frozendevelopment.mailshare.usecase.MetaLetterUseCase
import org.koin.compose.koinInject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LetterCell(
    modifier: Modifier = Modifier,
    id: LetterId,
    onClick: (LetterId) -> Unit,
    letterUseCase: MetaLetterUseCase = koinInject(),
) {
    // TODO: If the letter isn't found show an error
    val letter = letterUseCase.load(id) ?: return

    LetterCell(
        modifier = modifier,
        letter = letter,
        onClick = onClick,
    )
}

@Composable
fun LetterCell(
    modifier: Modifier = Modifier,
    letter: LetterInfo,
    onClick: (LetterId) -> Unit,
) {
    val date = Date(letter.created * 1000) // Convert seconds to milliseconds
    val dateFormat = SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault())
    val formattedDate = dateFormat.format(date)

    Card(
        modifier = modifier,
        onClick = { onClick(letter.id) },
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("From: ")
                        }

                        withStyle(style = SpanStyle(fontWeight = FontWeight.Light)) {
                            append(letter.sender ?: "Unknown")
                        }
                    },
                    fontSize = MaterialTheme.typography.labelMedium.fontSize,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("To: ")
                        }

                        withStyle(style = SpanStyle(fontWeight = FontWeight.Light)) {
                            append(letter.recipient ?: "Unknown")
                        }
                    },
                    fontSize = MaterialTheme.typography.labelMedium.fontSize,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Text(
                letter.body ?: "Nothing to show",
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Created: ")
                    }

                    withStyle(style = SpanStyle(fontWeight = FontWeight.Light)) {
                        append(formattedDate)
                    }
                },
                fontSize = MaterialTheme.typography.labelMedium.fontSize,
            )
        }
    }
}

@Composable
private fun LetterCellPreview(
    darkTheme: Boolean,
    letter: LetterInfo
) {
    MailShareTheme(darkTheme) {
        Surface {
            LetterCell(
                modifier = Modifier.fillMaxWidth(),
                letter = letter,
                onClick = {}
            )
        }
    }
}

@Composable
@Preview
private fun LetterCellLightPreview() {
    LetterCellPreview(
        darkTheme = false,
        letter = LetterInfo(
            id = LetterId.random(),
            sender = """
                James Smith
                123 Street Drive
                Town City, State
            """.trimIndent(),
            recipient = """
                Jane Jones
                4321 Circle Road
                Village, State
            """.trimIndent(),
            body = """
                Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
            """.trimIndent(),
            created = 0,
            lastModified = 0,
            categoryIds = null,
            categoryLabels = null,
        )
    )
}

@Composable
@Preview
private fun LetterCellDarkPreview() {
    LetterCellPreview(
        darkTheme = true,
        letter = LetterInfo(
            id = LetterId.random(),
            sender = """
                James Smith
                123 Street Drive
                Town City, State
            """.trimIndent(),
            recipient = """
                Jane Jones
                4321 Circle Road
                Village, State
            """.trimIndent(),
            body = """
                Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
            """.trimIndent(),
            created = 0,
            lastModified = 0,
            categoryIds = null,
            categoryLabels = null,
        )
    )
}
