package net.frozendevelopment.openletters.usecase

import androidx.compose.ui.graphics.Color
import net.frozendevelopment.openletters.data.sqldelight.CategoryQueries
import net.frozendevelopment.openletters.data.sqldelight.models.CategoryId
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

class UpsertCategoryUseCase(
    private val categoryQueries: CategoryQueries,
    private val now: () -> LocalDateTime = { LocalDateTime.now() },
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
