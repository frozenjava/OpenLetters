package net.frozendevelopment.mailshare.feature.list.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.frozendevelopment.mailshare.data.sqldelight.migrations.Category
import net.frozendevelopment.mailshare.data.sqldelight.models.CategoryId

@Composable
fun FilterBar(
    modifier: Modifier = Modifier,
    searchTerms: String,
    selectedCategoryId: CategoryId?,
    categories: List<Category>,
    onToggleCategory: (CategoryId?) -> Unit,
    onSearchChanged: (String) -> Unit,
) {
    Column(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.surface.copy(alpha = .9f)),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SearchBar(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp),
            searchTerms = searchTerms,
            onSearchChanged = onSearchChanged
        )

        CategorySelector(
            modifier = Modifier.fillMaxWidth()
                .padding(bottom = 16.dp),
            selectedCategoryId = selectedCategoryId,
            categories = categories,
            toggleCategory = onToggleCategory,
        )
    }
}
