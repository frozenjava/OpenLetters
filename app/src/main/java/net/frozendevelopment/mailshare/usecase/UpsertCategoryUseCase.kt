package net.frozendevelopment.mailshare.usecase

import androidx.compose.ui.graphics.Color
import net.frozendevelopment.mailshare.data.sqldelight.CategoryQueries
import net.frozendevelopment.mailshare.data.sqldelight.models.CategoryId
import java.time.Instant

class UpsertCategoryUseCase(
    private val categoryQueries: CategoryQueries,
    private val now: () -> Long = { Instant.now().epochSecond },
) {
    suspend operator fun invoke(
        id: CategoryId = CategoryId.random(),
        label: String,
        color: Color
    ) {
        val currentTime = now()

        categoryQueries.upsert(
            id = id,
            label = label,
            color = color,
            priority = 0,
            created = currentTime,
            lastModified = currentTime
        )
    }
}
