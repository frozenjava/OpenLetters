package net.frozendevelopment.openletters.migration

import androidx.compose.ui.graphics.Color
import net.frozendevelopment.openletters.data.sqldelight.CategoryQueries
import net.frozendevelopment.openletters.data.sqldelight.models.CategoryId
import java.time.Instant

class InitialCategoriesMigration(
    private val categoryQueries: net.frozendevelopment.openletters.data.sqldelight.CategoryQueries,
    private val now: () -> Long = { Instant.now().epochSecond }
) : AppMigration {
    override val migrationKey: String
        get() = "initial-categories"

    override fun invoke() {
        val currentTime = now()
        categoryQueries.transaction {
            categoryQueries.upsert(CategoryId.random(), "Advertisement", Color(16566787), 0, currentTime, currentTime)
            categoryQueries.upsert(CategoryId.random(), "Card", Color(243452), 0, currentTime, currentTime)
            categoryQueries.upsert(CategoryId.random(), "Coupon", Color(261293), 0, currentTime, currentTime)
            categoryQueries.upsert(CategoryId.random(), "Important", Color(16515843), 0, currentTime, currentTime)
            categoryQueries.upsert(CategoryId.random(), "Legal", Color(16515938), 0, currentTime, currentTime)
            categoryQueries.upsert(CategoryId.random(), "News", Color(1937238), 0, currentTime, currentTime)
            categoryQueries.upsert(CategoryId.random(), "Spam", Color(9407751), 0, currentTime, currentTime)
        }
    }
}
