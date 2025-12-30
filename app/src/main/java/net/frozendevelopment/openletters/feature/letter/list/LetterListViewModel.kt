package net.frozendevelopment.openletters.feature.letter.list

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOne
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.frozendevelopment.openletters.data.sqldelight.CategoryQueries
import net.frozendevelopment.openletters.data.sqldelight.LetterQueries
import net.frozendevelopment.openletters.data.sqldelight.ReminderQueries
import net.frozendevelopment.openletters.data.sqldelight.migrations.Category
import net.frozendevelopment.openletters.data.sqldelight.models.CategoryId
import net.frozendevelopment.openletters.data.sqldelight.models.LetterId
import net.frozendevelopment.openletters.data.sqldelight.models.ReminderId
import net.frozendevelopment.openletters.usecase.DeleteLetterUseCase
import net.frozendevelopment.openletters.usecase.SearchLettersUseCase
import net.frozendevelopment.openletters.util.StatefulViewModel

@Immutable
data class LetterListState(
    val showEmptyListView: Boolean = false,
    val isLoading: Boolean = true,
    val isBusy: Boolean = false,
    val selectedCategoryId: CategoryId? = null,
    val letters: List<LetterId> = emptyList(),
    val categories: List<Category> = emptyList(),
    val searchTerms: String = "",
    val urgentReminders: List<ReminderId> = emptyList(),
    val upcomingReminders: List<ReminderId> = emptyList(),
)

class LetterListViewModel(
    private val reminderQueries: ReminderQueries,
    private val letterQueries: LetterQueries,
    private val searchUseCase: SearchLettersUseCase,
    private val categoryQueries: CategoryQueries,
    private val deleteLetter: DeleteLetterUseCase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : StatefulViewModel<LetterListState>(LetterListState()) {
    init {
        viewModelScope.launch(ioDispatcher) {
            letterQueries
                .hasLetters()
                .asFlow()
                .mapToOne(ioDispatcher)
                .collect {
                    update { copy(showEmptyListView = it != 1L) }
                }
        }
    }

    override fun load() {
        load(categoryFilter = state.selectedCategoryId, searchTerms = state.searchTerms)
    }

    fun load(
        categoryFilter: CategoryId? = null,
        searchTerms: String = "",
    ) {
        val categories = categoryQueries.allCategories().executeAsList()

        val letters = searchUseCase(
            query = searchTerms,
            category = categoryFilter,
        )

        val urgentReminders = if (searchTerms.isBlank() && categoryFilter == null) {
            reminderQueries.urgentReminders().executeAsList()
        } else {
            emptyList()
        }

        val upcomingReminders = if (searchTerms.isBlank() && categoryFilter == null) {
            reminderQueries.upcomingReminders().executeAsList()
        } else {
            emptyList()
        }

        update {
            copy(
                isLoading = false,
                letters = letters,
                categories = categories,
                urgentReminders = urgentReminders,
                upcomingReminders = upcomingReminders,
            )
        }
    }

    fun delete(id: LetterId) = viewModelScope.launch {
        deleteLetter(id)
        load()
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
