package net.frozendevelopment.openletters.feature.letter.list.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DocumentScanner
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import net.frozendevelopment.openletters.R
import net.frozendevelopment.openletters.extensions.pulse
import net.frozendevelopment.openletters.ui.theme.OpenLettersTheme

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
        Text(
            modifier = Modifier.padding(bottom = 16.dp),
            text = stringResource(R.string.no_letters_yet),
            style = MaterialTheme.typography.displayMedium,
        )

        Text(
            modifier = Modifier.padding(bottom = 16.dp),
            text = stringResource(R.string.scan_your_first),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )

        Button(
            modifier = Modifier.pulse(),
            onClick = onScanClicked,
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.DocumentScanner,
                    contentDescription = null,
                )
                Text(
                    text = stringResource(R.string.scan_letter),
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        }
    }
}

@PreviewLightDark
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
