package net.frozendevelopment.openletters.usecase

import net.frozendevelopment.openletters.data.sqldelight.CategoryQueries
import net.frozendevelopment.openletters.data.sqldelight.models.CategoryId
import java.time.LocalDateTime

class SaveCategoryOrderUseCase(
    private val categoryQueries: CategoryQueries,
    private val now: () -> LocalDateTime = { LocalDateTime.now() }
) {
    operator fun invoke(
        categoryId: CategoryId,
        order: Long
    ) {
        categoryQueries.setPriority(
            priority = order,
            modifiedTimestamp = now(),
            id = categoryId
        )
    }
}