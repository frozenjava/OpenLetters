package net.frozendevelopment.openletters.feature.letter.list.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DocumentScanner
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.frozendevelopment.openletters.R
import net.frozendevelopment.openletters.ui.components.PulseIndicator
import net.frozendevelopment.openletters.ui.theme.OpenLettersTheme
import net.frozendevelopment.openletters.ui.theme.tipCardColors

@Composable
fun EmptyListView(
    modifier: Modifier = Modifier,
    onScanClicked: () -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        PulseIndicator {
            ElevatedCard(
                modifier =
                    Modifier
                        .fillMaxWidth(.95f),
                colors = tipCardColors,
                onClick = onScanClicked,
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DocumentScanner,
                            contentDescription = null,
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = "Import Letter",
                            style = MaterialTheme.typography.headlineLarge,
                        )
                    }
                    Text(
                        text = stringResource(R.string.letter_tooltip),
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun EmptyListViewPreview() {
    OpenLettersTheme {
        Surface {
            EmptyListView(
                modifier = Modifier.fillMaxWidth(),
                onScanClicked = {},
            )
        }
    }
}

@Preview
@Composable
fun DarkEmptyListViewPreview() {
    OpenLettersTheme(darkTheme = true) {
        Surface {
            EmptyListView(
                modifier = Modifier.fillMaxWidth(),
                onScanClicked = {},
            )
        }
    }
}
