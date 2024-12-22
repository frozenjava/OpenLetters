package net.frozendevelopment.openletters

import androidx.compose.ui.graphics.Color
import net.frozendevelopment.openletters.data.sqldelight.migrations.Category
import net.frozendevelopment.openletters.data.sqldelight.models.CategoryId
import net.frozendevelopment.openletters.usecase.SaveCategoryOrderUseCase
import net.frozendevelopment.openletters.util.testDatabase
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.LocalDateTime
import java.time.ZoneOffset

@RunWith(RobolectricTestRunner::class)
class SaveCategoryOrderUseCaseTests {
    @Test
    fun `priority is saved`() {
        val categoryId = CategoryId.random()
        val time = LocalDateTime.ofEpochSecond(1000, 0, ZoneOffset.UTC)
        val created = LocalDateTime.ofEpochSecond(500, 0, ZoneOffset.UTC)
        val database =
            testDatabase().apply {
                categoryQueries.upsert(
                    id = categoryId,
                    label = "Test Category",
                    color = Color.Black,
                    priority = Long.MAX_VALUE,
                    created = created,
                    lastModified = created,
                )
            }

        val expected =
            Category(
                id = categoryId,
                label = "Test Category",
                color = Color.Black,
                priority = 50L,
                created = created,
                lastModified = time,
            )

        val saveOrder =
            SaveCategoryOrderUseCase(
                categoryQueries = database.categoryQueries,
                now = { time },
            )

        saveOrder(categoryId, 50L)

        val dbCategory =
            database.categoryQueries
                .get(categoryId)
                .executeAsOneOrNull()

        Assert.assertEquals(expected, dbCategory)
    }
}
