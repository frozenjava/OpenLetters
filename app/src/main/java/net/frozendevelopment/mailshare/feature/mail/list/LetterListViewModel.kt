package net.frozendevelopment.mailshare.feature.mail.list

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.frozendevelopment.mailshare.data.sqldelight.CategoryQueries
import net.frozendevelopment.mailshare.data.sqldelight.LetterQueries
import net.frozendevelopment.mailshare.data.sqldelight.migrations.Category
import net.frozendevelopment.mailshare.data.sqldelight.models.CategoryId
import net.frozendevelopment.mailshare.data.sqldelight.models.LetterId
import net.frozendevelopment.mailshare.util.StatefulViewModel

@Immutable
data class LetterListState(
    val isLoading: Boolean = true,
    val isBusy: Boolean = false,
    val selectedCategoryId: CategoryId? = null,
    val letters: List<LetterId> = emptyList(),
    val categories: List<Category> = emptyList(),
    val searchTerms: String = ""
) {
    val showEmptyListView: Boolean
        get() = letters.isEmpty() && selectedCategoryId == null && !isLoading
}

class LetterListViewModel(
    private val letterQueries: LetterQueries,
    private val categoryQueries: CategoryQueries,
) : StatefulViewModel<LetterListState>(LetterListState()) {

    suspend fun load(categoryFilter: CategoryId? = null) {
        val categories = categoryQueries.allCategories().executeAsList()

        val letters = if (categoryFilter == null)
            letterQueries.allLetters().executeAsList()
        else
            letterQueries.lettersByCategory(categoryFilter).executeAsList()

        update {
            copy(
                isLoading = false,
                letters = letters,
                categories = categories
            )
        }
    }

    fun toggleCategory(category: CategoryId?) = viewModelScope.launch {
        val toggleCategory = if (category == state.selectedCategoryId) {
            null
        } else {
            category
        }

        update { copy(selectedCategoryId = toggleCategory) }
        load(categoryFilter = toggleCategory)
    }

    fun setSearchTerms(terms: String) = viewModelScope.launch {
        update { copy(searchTerms = terms) }
    }
}
