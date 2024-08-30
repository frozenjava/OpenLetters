package net.frozendevelopment.mailshare.feature.scan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.DocumentScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.frozendevelopment.mailshare.ui.components.ImageViewer
import net.frozendevelopment.mailshare.ui.theme.MailShareTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanFormView(
    modifier: Modifier = Modifier,
    state: ScanState,
    openLetterScanner: () -> Unit,
    openSenderScanner: () -> Unit,
    openRecipientScanner: () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CenterAlignedTopAppBar(
            title = { Text(text = "Import Letter") },
            navigationIcon = {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "back"
                    )
                }
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth(.95f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .weight(1f, fill = true),
                value = state.sender ?: "",
                maxLines = 3,
                onValueChange = {},
                label = {
                    Text(text = "Sender")
                },
                placeholder = {
                    Text(
                        text = """
                        Jane Doe 123 Street Drive
                    """.trimIndent()
                    )
                }
            )

            IconButton(onClick = openSenderScanner) {
                Icon(imageVector = Icons.Outlined.DocumentScanner, contentDescription = "Scan")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(.95f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .weight(1f, fill = true),
                value = state.recipient ?: "",
                maxLines = 3,
                onValueChange = {},
                label = {
                    Text(text = "Recipient")
                },
                placeholder = {
                    Text(
                        text = """
                        Jane Doe 123 Street Drive
                    """.trimIndent()
                    )
                }
            )

            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Outlined.DocumentScanner, contentDescription = "Scan")
            }
        }

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

            Spacer(modifier = Modifier.weight(1f))

            OutlinedButton(onClick = { /*TODO*/ }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(.95f)
                        .weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        imageVector = Icons.Outlined.DocumentScanner,
                        contentDescription = "Scan",
                    )
                    Text(text = "Scan Additional Page")
                }
            }

            Button(
                modifier = Modifier.fillMaxWidth(.95f),
                onClick = { /*TODO*/ }
            ) {
                Text(text = "Save")
            }
        } else {
            Column {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    contentPadding = PaddingValues(start = 16.dp, end = 32.dp)
                ) {
                    items(
                        items = state.categories.keys.toList(),
                        key = { it.id.value }
                    ) { category ->
                        Box(
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.tertiary,
                                    shape = ButtonDefaults.shape
                                )
                                .padding(ButtonDefaults.ContentPadding),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = category.label,
                                color = MaterialTheme.colorScheme.onTertiary,
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                    }

                    item {
                        Box(
                            modifier = Modifier
                                .clip(ButtonDefaults.shape)
                                .border(1.dp, MaterialTheme.colorScheme.tertiary),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                modifier = Modifier.padding(ButtonDefaults.ContentPadding),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                                Text(text = "Create Tag")
                            }
                        }
                    }
                }
            }
            LazyRow {
                itemsIndexed(state.documents) { index, item ->
                    ImageViewer(uri = item)
                }
            }
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
                openLetterScanner = {},
                openSenderScanner = {},
                openRecipientScanner = {},
            )
        }
    }
}
