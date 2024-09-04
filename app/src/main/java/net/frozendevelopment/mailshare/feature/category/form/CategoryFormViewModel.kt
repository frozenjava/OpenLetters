package net.frozendevelopment.mailshare.feature.category.form

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.frozendevelopment.mailshare.usecase.UpsertCategoryUseCase
import net.frozendevelopment.mailshare.util.StatefulViewModel

@Immutable
data class CategoryFormState(
    val label: String = "",
    val color: Long = 0xFF0F0FF0,
) {
    val isSavable = label.isNotBlank()
}

class CategoryFormViewModel(
    private val upsertCategoryUseCase: UpsertCategoryUseCase
) : StatefulViewModel<CategoryFormState>(CategoryFormState()) {
    fun setLabel(label: String) = viewModelScope.launch {
        update { copy(label = label) }
    }

    fun setColor(color: Long) = viewModelScope.launch {
        update { copy(color = color) }
    }

    suspend fun save() {
        upsertCategoryUseCase(
            label = stateFlow.value.label,
            color = stateFlow.value.color
        )
    }
}
