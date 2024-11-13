package net.frozendevelopment.openletters.feature.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.toSize


@Composable
fun <T> DropDownButton(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    value: @Composable () -> Unit,
    menuItems: List<T>,
    menuItemLabel: @Composable ((T) -> Unit),
    onMenuItemSelected: (T) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var buttonSize by remember { mutableStateOf(Size.Zero) }

    Column(modifier = modifier) {
        ValueButton(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned {
                    buttonSize = it.size.toSize()
                },
            title = title,
            value = value,
            onClick = { expanded = !expanded },
            rightContent = {
                Icon(Icons.Filled.ExpandMore, contentDescription = null)
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(with(LocalDensity.current) { buttonSize.width.toDp() })
        ) {
            menuItems.forEach { item ->
                DropdownMenuItem(
                    text = { menuItemLabel(item) },
                    onClick = {
                        onMenuItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}
