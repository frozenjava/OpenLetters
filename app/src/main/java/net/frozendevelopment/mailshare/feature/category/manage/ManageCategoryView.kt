package net.frozendevelopment.mailshare.feature.category.manage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import net.frozendevelopment.mailshare.data.sqldelight.models.CategoryId
import net.frozendevelopment.mailshare.ui.components.CategoryPill

const val MANAGE_CATEGORY_ROUTE = "/categories"

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ManageCategoryView(
    state: ManageCategoryState,
    openNavigationDrawer: () -> Unit,
    editCategoryClicked: (CategoryId?) -> Unit,
    onDeleteClicked: (CategoryId) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        CenterAlignedTopAppBar(
            title = { Text(text = "Categories") },
            navigationIcon = {
                IconButton(onClick = openNavigationDrawer) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Back",
                    )
                }
            },
            actions = {
                IconButton(onClick = { editCategoryClicked(null) }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Back",
                    )
                }
            }
        )

        FlowRow {
            for (category in state.categories) {
                CategoryPill(
                    category = category,
                    isSelected = true,
                    onToggle = { /*TODO*/ },
                )
            }
        }
    }
}
