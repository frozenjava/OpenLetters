package net.frozendevelopment.mailshare.feature.scan.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DocumentScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import net.frozendevelopment.mailshare.ui.theme.MailShareTheme

@Composable
fun ScannableTextField(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    onScanClick: () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextField(
            modifier = Modifier
                .weight(1f, fill = true),
            value = value,
            maxLines = 3,
            onValueChange = onValueChange,
            label = {
                Text(text = label)
            },
            placeholder = {
                Text(text = placeholder)
            }
        )

        IconButton(onClick = onScanClick) {
            Icon(imageVector = Icons.Outlined.DocumentScanner, contentDescription = "Scan")
        }
    }
}

@Composable
@Preview
private fun PreviewScannableTextField() {
    MailShareTheme {
        Surface {
            ScannableTextField(
                modifier = Modifier.fillMaxWidth(),
                value = "",
                label = "Sender",
                placeholder = "123 Some Street",
                onValueChange = {},
                onScanClick = {}
            )
        }
    }

}