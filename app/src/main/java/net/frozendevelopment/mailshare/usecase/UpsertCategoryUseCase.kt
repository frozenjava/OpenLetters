package net.frozendevelopment.mailshare.usecase

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
        color: Long
    ) {
        val currentTime = now()

        categoryQueries.upsert(
            id = id,
            label = label,
            color = color,
            created = currentTime,
            lastModified = currentTime
        )
    }

}