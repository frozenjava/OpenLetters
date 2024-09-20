package net.frozendevelopment.openletters.feature.letter.list.ui

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DocumentScanner
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
                modifier = Modifier
                    .fillMaxWidth(.95f),
                colors = tipCardColors,
                onClick = onScanClicked,
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
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
                        text = "Tap here to get started importing your letters! You'll do this by taking pictures of your envelopes and letters. We handle the rest for you locally on your phone.",
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