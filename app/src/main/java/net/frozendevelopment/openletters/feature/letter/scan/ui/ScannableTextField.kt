package net.frozendevelopment.openletters.feature.letter.scan.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DocumentScanner
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.PopupProperties
import net.frozendevelopment.openletters.ui.theme.OpenLettersTheme

@Composable
fun ScannableTextField(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    placeholder: String,
    suggestions: List<String>,
    onValueChange: (String) -> Unit,
    onScanClick: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    var showDropdown by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f, fill = true)) {
            OutlinedTextField(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .onGloballyPositioned {
                            textFieldSize = it.size.toSize()
                        },
                value = value,
                maxLines = 3,
                onValueChange = {
                    onValueChange(it)
                    showDropdown = suggestions.isNotEmpty()
                },
                label = {
                    Text(text = label)
                },
                placeholder = {
                    Text(text = placeholder)
                },
            )

            DropdownMenu(
                expanded = showDropdown && suggestions.isNotEmpty(),
                onDismissRequest = { showDropdown = false },
                properties = PopupProperties(focusable = false),
                modifier = Modifier.width(with(LocalDensity.current) { textFieldSize.width.toDp() }),
            ) {
                suggestions.forEachIndexed { index, value ->
                    DropdownMenuItem(
                        text = { Text(text = value) },
                        onClick = {
                            onValueChange(value)
                            showDropdown = false
                            focusRequester.freeFocus()
                            keyboardController?.hide()
                        },
                    )

                    if (index < suggestions.lastIndex) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }
        }

        IconButton(onClick = onScanClick) {
            Icon(imageVector = Icons.Outlined.DocumentScanner, contentDescription = "Scan")
        }
    }
}

@Composable
@Preview
private fun PreviewScannableTextField() {
    OpenLettersTheme {
        Surface {
            ScannableTextField(
                modifier = Modifier.fillMaxWidth(),
                value = "",
                label = "Sender",
                placeholder = "123 Some Street",
                suggestions = listOf("123 Some Street", "456 Other Street"),
                onValueChange = {},
                onScanClick = {},
            )
        }
    }
}
