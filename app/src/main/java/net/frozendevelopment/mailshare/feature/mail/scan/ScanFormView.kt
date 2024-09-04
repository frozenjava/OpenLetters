package net.frozendevelopment.mailshare.feature.mail.scan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DocumentScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.frozendevelopment.mailshare.data.sqldelight.migrations.Category
import net.frozendevelopment.mailshare.feature.mail.scan.ui.CategoryPicker
import net.frozendevelopment.mailshare.feature.mail.scan.ui.DocumentRow
import net.frozendevelopment.mailshare.feature.mail.scan.ui.ScanAppBar
import net.frozendevelopment.mailshare.feature.mail.scan.ui.ScannableTextField
import net.frozendevelopment.mailshare.ui.theme.MailShareTheme

@Composable
fun ScanFormView(
    modifier: Modifier = Modifier,
    state: ScanState,
    setSender: (String) -> Unit,
    setRecipient: (String) -> Unit,
    toggleCategory: (Category) -> Unit,
    openLetterScanner: () -> Unit,
    openSenderScanner: () -> Unit,
    openRecipientScanner: () -> Unit,
    onSaveClicked: () -> Unit,
    onBackClicked: () -> Unit,
    onDeleteDocumentClicked: (Int) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ScanAppBar(
            canLeaveSafely = state.canLeaveSafely,
            isSavable = state.isSavable,
            onSaveClicked = onSaveClicked,
            onBackClicked = onBackClicked,
        )

        ScannableTextField(
            modifier = Modifier.fillMaxWidth(.95f),
            value = state.sender ?: "",
            label = "Sender",
            placeholder = """
                        Jane Doe 123 Street Drive
                    """.trimIndent(),
            onValueChange = setSender,
            onScanClick = openSenderScanner,
        )

        ScannableTextField(
            modifier = Modifier.fillMaxWidth(.95f),
            value = state.recipient ?: "",
            label = "Recipient",
            placeholder = """
                        Jane Doe 123 Street Drive
                    """.trimIndent(),
            onValueChange = setRecipient,
            onScanClick = openRecipientScanner
        )

        if (state.documents.isEmpty()) {
            OutlinedCard(
                onClick = openLetterScanner
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f, fill = true)
                        .fillMaxWidth(.95f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(64.dp),
                        imageVector = Icons.Outlined.DocumentScanner,
                        contentDescription = "Scan",
                    )
                    Text(
                        text = "Scan Letter",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        } else {
            CategoryPicker(
                modifier = Modifier.fillMaxWidth(),
                categories = state.categories,
                toggleCategory = toggleCategory,
                onCreateClicked = {},
            )
            DocumentRow(
                modifier = Modifier.fillMaxWidth(),
                documents = state.documents,
                onDeleteDocumentClicked = onDeleteDocumentClicked
            )
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun ScanFormViewPreview() {
    MailShareTheme {
        Surface {
            ScanFormView(
                state = ScanState(),
                toggleCategory = {},
                setSender = {},
                setRecipient = {},
                openLetterScanner = {},
                openSenderScanner = {},
                openRecipientScanner = {},
                onSaveClicked = {},
                onBackClicked = {},
                onDeleteDocumentClicked = {},
            )
        }
    }
}
