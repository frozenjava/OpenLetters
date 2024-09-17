package net.frozendevelopment.openletters.feature.category.manage.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DragIndicator
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.frozendevelopment.openletters.data.sqldelight.migrations.Category
import net.frozendevelopment.openletters.data.sqldelight.models.CategoryId
import net.frozendevelopment.openletters.extensions.contrastColor
import net.frozendevelopment.openletters.ui.theme.OpenLettersTheme
import java.time.LocalDateTime
import java.time.ZoneOffset

@Composable
fun CategoryRow(
    modifier: Modifier = Modifier,
    category: Category,
    onEditClicked: (CategoryId) -> Unit,
    onDeleteClicked: (CategoryId) -> Unit,
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            icon = { Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Delete") },
            title = { Text(text = "Delete Category \"${category.label}\"") },
            text = { Text(text = "Are you sure you want to delete this category? This will not delete any letters associated with this category.") },
            confirmButton = {
                Button(onClick = {
                    onDeleteClicked(category.id)
                    showDeleteConfirmation = false
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Surface(
        modifier = modifier,
        color = category.color,
        contentColor = category.color.contrastColor
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.DragIndicator,
                contentDescription = "Drag",
            )
            Text(
                text = category.label,
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(Modifier.weight(1f))
            IconButton(onClick = { onEditClicked(category.id) }) {
                Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Edit")
            }

            VerticalDivider(modifier = Modifier.height(16.dp))

            IconButton(onClick = { showDeleteConfirmation = true }) {
                Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Delete")
            }
        }
    }
}

@Composable
@Preview
private fun ManagePillPreview() {
    OpenLettersTheme {
        Surface {
            CategoryRow(
                category = Category(
                    id = CategoryId.random(),
                    label = "Some Label",
                    color = Color(0xFF0F0FF0),
                    created = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC),
                    lastModified = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC),
                    priority = 0,
                ),
                onEditClicked = {},
                onDeleteClicked = {},
            )
        }
    }
}