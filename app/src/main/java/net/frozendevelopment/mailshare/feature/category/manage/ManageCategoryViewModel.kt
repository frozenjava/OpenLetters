package net.frozendevelopment.mailshare.feature.category.manage

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.frozendevelopment.mailshare.data.sqldelight.CategoryQueries
import net.frozendevelopment.mailshare.data.sqldelight.migrations.Category
import net.frozendevelopment.mailshare.data.sqldelight.models.CategoryId
import net.frozendevelopment.mailshare.util.StatefulViewModel

@Immutable
data class ManageCategoryState(
    val categories: List<Category> = emptyList(),
)

class ManageCategoryViewModel(
    private val categoryQueries: CategoryQueries,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): StatefulViewModel<ManageCategoryState>(ManageCategoryState()) {
    suspend fun load() {
        val categories = categoryQueries.allCategories().executeAsList()
        update { copy(categories = categories) }
    }

    fun delete(category: CategoryId) = viewModelScope.launch(ioDispatcher) {
        categoryQueries.delete(category)
        load()
    }
}