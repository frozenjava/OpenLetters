package net.frozendevelopment.mailshare.feature.scan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.DocumentScanner
import androidx.compose.material3.Button
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.frozendevelopment.mailshare.ui.theme.MailShareTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanFormView(
    modifier: Modifier = Modifier,
    openScanner: () -> Unit,
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
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back")
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
                value = "",
                maxLines = 3,
                onValueChange = {},
                label = {
                    Text(text = "Sender")
                },
                placeholder = {
                    Text(text = """
                        Jane Doe 123 Street Drive
                    """.trimIndent())
                }
            )

            IconButton(onClick = { /*TODO*/ }) {
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
                value = "",
                maxLines = 3,
                onValueChange = {},
                label = {
                    Text(text = "Recipient")
                },
                placeholder = {
                    Text(text = """
                        Jane Doe 123 Street Drive
                    """.trimIndent())
                }
            )

            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Outlined.DocumentScanner, contentDescription = "Scan")
            }
        }

        OutlinedCard(
            onClick = openScanner
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
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun ScanFormViewPreview() {
    MailShareTheme {
        Surface {
            ScanFormView(
                openScanner = {},
            )
        }
    }
}
