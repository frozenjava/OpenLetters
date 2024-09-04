package net.frozendevelopment.mailshare.feature.mail.scan.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.ContactMail
import androidx.compose.material.icons.outlined.ContactMail
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.frozendevelopment.mailshare.ui.theme.MailShareTheme

@Composable
fun ScanCard(
    modifier: Modifier = Modifier,
    label: String,
    labelIcon: ImageVector,
    scannedText: String?,
    onScanClicked: () -> Unit
) {
    OutlinedCard(
        modifier = modifier,
        onClick = onScanClicked
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (scannedText.isNullOrBlank()) {
                Text(label)
                Icon(imageVector = labelIcon, contentDescription = null)
            } else {
                Text(scannedText)
                Icon(imageVector = Icons.Filled.Camera, contentDescription = null)
            }
        }
    }
}

@Composable
@Preview
private fun ScanCardPreview() {
    MailShareTheme {
        Surface {
            ScanCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                label = "Tap to import sender",
                labelIcon = Icons.Outlined.ContactMail,
                scannedText = null,
                onScanClicked = {},
            )
        }
    }
}
