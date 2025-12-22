package net.frozendevelopment.openletters.feature.category.form

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.frozendevelopment.openletters.data.sqldelight.CategoryQueries
import net.frozendevelopment.openletters.data.sqldelight.models.CategoryId
import net.frozendevelopment.openletters.extensions.Random
import net.frozendevelopment.openletters.usecase.UpsertCategoryUseCase
import net.frozendevelopment.openletters.util.StatefulViewModel

@Immutable
data class CategoryFormState(
    private val mode: CategoryFormDestination.Mode,
    val isBusy: Boolean = true,
    val label: String = "",
    val color: Color = Color.Random,
) {
    val isSavable = label.isNotBlank()

    val title: String
        get() =
            when (mode) {
                is CategoryFormDestination.Mode.Create -> "Create Category"
                is CategoryFormDestination.Mode.Edit -> "Edit Category"
            }
}

class CategoryFormViewModel(
    private val mode: CategoryFormDestination.Mode,
    private val upsertCategoryUseCase: UpsertCategoryUseCase,
    private val categoryQueries: CategoryQueries,
) : StatefulViewModel<CategoryFormState>(CategoryFormState(mode)) {
    private val categoryId: CategoryId
        get() =
            when (mode) {
                is CategoryFormDestination.Mode.Create -> CategoryId.random()
                is CategoryFormDestination.Mode.Edit -> mode.id
            }

    override fun load() {
        if (mode is CategoryFormDestination.Mode.Edit) {
            val category = categoryQueries.get(mode.id).executeAsOneOrNull()
            if (category != null) {
                update {
                    copy(
                        label = category.label,
                        color = category.color,
                        isBusy = false,
                    )
                }
            }
        } else {
            update { copy(isBusy = false) }
        }
    }

    fun setLabel(label: String) = viewModelScope.launch {
        update { copy(label = label) }
    }

    fun setColor(color: Color) = viewModelScope.launch {
        update { copy(color = color) }
    }

    suspend fun save() {
        upsertCategoryUseCase(
            id = categoryId,
            label = stateFlow.value.label,
            color = stateFlow.value.color,
        )
    }
}
