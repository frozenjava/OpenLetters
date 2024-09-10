package net.frozendevelopment.mailshare.feature.mail.list

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOne
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.frozendevelopment.mailshare.data.sqldelight.CategoryQueries
import net.frozendevelopment.mailshare.data.sqldelight.LetterQueries
import net.frozendevelopment.mailshare.data.sqldelight.migrations.Category
import net.frozendevelopment.mailshare.data.sqldelight.models.CategoryId
import net.frozendevelopment.mailshare.data.sqldelight.models.LetterId
import net.frozendevelopment.mailshare.usecase.SearchLettersUseCase
import net.frozendevelopment.mailshare.util.StatefulViewModel

@Immutable
data class LetterListState(
    val showEmptyListView: Boolean = false,
    val isLoading: Boolean = true,
    val isBusy: Boolean = false,
    val selectedCategoryId: CategoryId? = null,
    val letters: List<LetterId> = emptyList(),
    val categories: List<Category> = emptyList(),
    val searchTerms: String = ""
)

class LetterListViewModel(
    private val letterQueries: LetterQueries,
    private val searchUseCase: SearchLettersUseCase,
    private val categoryQueries: CategoryQueries,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : StatefulViewModel<LetterListState>(LetterListState()) {

    init {
        viewModelScope.launch(ioDispatcher) {
            letterQueries.hasLetters()
                .asFlow()
                .mapToOne(ioDispatcher)
                .collect {
                    update { copy(showEmptyListView = it != 1L) }
                }
        }
    }

    suspend fun load(
        categoryFilter: CategoryId? = null,
        searchTerms: String = "",
    ) {
        val categories = categoryQueries.allCategories().executeAsList()

        val letters = searchUseCase(
            query = searchTerms,
            category = categoryFilter
        )

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
        load(categoryFilter = toggleCategory, searchTerms = state.searchTerms)
    }

    fun setSearchTerms(terms: String) = viewModelScope.launch {
        update { copy(searchTerms = terms) }
        load(state.selectedCategoryId, searchTerms = terms)
    }
}
