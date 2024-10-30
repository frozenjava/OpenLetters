package net.frozendevelopment.openletters.feature.letter.scan.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.frozendevelopment.openletters.R
import net.frozendevelopment.openletters.data.mock.mockCategory
import net.frozendevelopment.openletters.data.sqldelight.migrations.Category
import net.frozendevelopment.openletters.data.sqldelight.models.CategoryId
import net.frozendevelopment.openletters.ui.components.CategoryPill
import net.frozendevelopment.openletters.ui.theme.OpenLettersTheme

@Composable
fun CategoryPicker(
    modifier: Modifier = Modifier,
    categories: Map<Category, Boolean>,
    toggleCategory: (Category) -> Unit,
    onCreateClicked: () -> Unit,
) {
    Column {
        LazyRow(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            contentPadding = PaddingValues(start = 16.dp, end = 32.dp)
        ) {
            items(
                items = categories.keys.toList(),
                key = { it.id.value }
            ) { category ->
                CategoryPill(
                    category = category,
                    isSelected = categories[category] == true,
                    onToggle = { toggleCategory(category) }
                )
            }

            item {
                OutlinedButton(onClick = onCreateClicked) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Text(text = stringResource(R.string.create_category))
                }
            }
        }
    }
}

@Preview
@Composable
private fun CategoryPickerPreview() {
    OpenLettersTheme {
        Surface {
            CategoryPicker(
                categories = mapOf(
                    mockCategory(label = "A") to true,
                    mockCategory(label = "B", color = Color.Cyan) to false,
                ),
                toggleCategory = {},
                onCreateClicked = {},
            )
        }
    }
}

@Preview
@Composable
private fun CategoryPickerDarkPreview() {
    OpenLettersTheme(darkTheme = true) {
        Surface {
            CategoryPicker(
                categories = mapOf(
                    mockCategory(label = "A") to true,
                    mockCategory(label = "B", color = Color.Cyan) to false,
                ),
                toggleCategory = {},
                onCreateClicked = {},
            )
        }
    }

}
