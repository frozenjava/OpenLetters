package net.frozendevelopment.openletters.feature.category.manage

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.frozendevelopment.openletters.data.sqldelight.CategoryQueries
import net.frozendevelopment.openletters.data.sqldelight.migrations.Category
import net.frozendevelopment.openletters.data.sqldelight.models.CategoryId
import net.frozendevelopment.openletters.usecase.SaveCategoryOrderUseCase
import net.frozendevelopment.openletters.util.StatefulViewModel

@Immutable
data class ManageCategoryState(
    val selectedCategory: CategoryId? = null,
    val categories: List<Category> = emptyList(),
) {
    val isEmpty: Boolean
        get() = categories.isEmpty()
}

class ManageCategoryViewModel(
    private val saveCategoryOrder: SaveCategoryOrderUseCase,
    private val categoryQueries: CategoryQueries,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : StatefulViewModel<ManageCategoryState>(ManageCategoryState()) {
    override fun load() {
        val categories = categoryQueries.allCategories().executeAsList()
        update { copy(categories = categories) }
    }

    fun delete(category: CategoryId) =
        viewModelScope.launch(ioDispatcher) {
            categoryQueries.delete(category)
            load()
        }

    fun onMove(
        from: Int,
        to: Int,
    ) {
        if (from == to) {
            return
        }

        Log.d("ManageCategoryViewModel", "onMove: $from -> $to")

        val categories =
            stateFlow.value.categories.toMutableList().apply {
                add(to, removeAt(from))
            }

        update {
            copy(
                categories = categories,
            )
        }
    }

    fun saveOrder() =
        viewModelScope.launch(ioDispatcher) {
            state.categories.forEachIndexed { index, category ->
                saveCategoryOrder(category.id, index.toLong())
            }
        }

    fun select(category: CategoryId?) =
        viewModelScope.launch {
            val selectedCategory =
                if (category == state.selectedCategory) {
                    null
                } else {
                    category
                }
            update { copy(selectedCategory = selectedCategory) }
        }
}
