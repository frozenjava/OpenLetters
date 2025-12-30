package net.frozendevelopment.openletters.migration

import androidx.compose.ui.graphics.Color
import net.frozendevelopment.openletters.R
import net.frozendevelopment.openletters.data.sqldelight.CategoryQueries
import net.frozendevelopment.openletters.data.sqldelight.models.CategoryId
import java.time.LocalDateTime

class InitialCategoriesMigration(
    private val categoryQueries: CategoryQueries,
    private val stringResource: (id: Int) -> String,
    private val now: () -> LocalDateTime = { LocalDateTime.now() },
) : AppMigration {
    override val migrationKey: String
        get() = "initial-categories"

    override fun invoke() {
        val categories = listOf(
            stringResource(R.string.advertisement) to Color(16566787),
            stringResource(R.string.card) to Color(243452),
            stringResource(R.string.coupon) to Color(261293),
            stringResource(R.string.important) to Color(16515843),
            stringResource(R.string.news) to Color(1937238),
        ).sortedBy { it.first }

        val currentTime = now()

        categoryQueries.transaction {
            categories.forEachIndexed { index, (label, color) ->
                categoryQueries.upsert(
                    id = CategoryId.random(),
                    label = label,
                    color = color,
                    priority = index.toLong(),
                    created = currentTime,
                    lastModified = currentTime,
                )
            }
        }
    }
}
