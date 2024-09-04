package net.frozendevelopment.mailshare.migration

import net.frozendevelopment.mailshare.data.sqldelight.CategoryQueries
import net.frozendevelopment.mailshare.data.sqldelight.models.CategoryId
import java.time.Instant

class InitialCategoriesMigration(
    private val categoryQueries: CategoryQueries,
    private val now: () -> Long = { Instant.now().epochSecond }
) : AppMigration {
    override val migrationKey: String
        get() = "initial-categories"

    override fun invoke() {
        val currentTime = now()
        categoryQueries.transaction {
            categoryQueries.upsert(CategoryId.random(), "Advertisement", 16566787, currentTime, currentTime)
            categoryQueries.upsert(CategoryId.random(), "Card", 243452, currentTime, currentTime)
            categoryQueries.upsert(CategoryId.random(), "Coupon", 261293, currentTime, currentTime)
            categoryQueries.upsert(CategoryId.random(), "Important", 16515843, currentTime, currentTime)
            categoryQueries.upsert(CategoryId.random(), "Legal", 16515938, currentTime, currentTime)
            categoryQueries.upsert(CategoryId.random(), "News", 1937238, currentTime, currentTime)
            categoryQueries.upsert(CategoryId.random(), "Spam", 9407751, currentTime, currentTime)
        }
    }
}
