package net.frozendevelopment.openletters

import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.runBlocking
import net.frozendevelopment.openletters.data.sqldelight.migrations.Category
import net.frozendevelopment.openletters.data.sqldelight.models.CategoryId
import net.frozendevelopment.openletters.usecase.UpsertCategoryUseCase
import net.frozendevelopment.openletters.util.testDatabase
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.LocalDateTime
import java.time.ZoneOffset

@RunWith(RobolectricTestRunner::class)
class UpsertCategoryUseCaseTests {
    @Test
    fun `should insert new category`() =
        runBlocking {
            val database = testDatabase()
            val categoryId = CategoryId.random()
            val time = LocalDateTime.ofEpochSecond(1000, 0, ZoneOffset.UTC)
            val upsertCategory = UpsertCategoryUseCase(database.categoryQueries, { time })

            val expectedCategory =
                Category(
                    id = categoryId,
                    label = "Test Category",
                    color = Color.Blue,
                    created = time,
                    lastModified = time,
                    priority = Long.MAX_VALUE,
                )

            upsertCategory(
                id = categoryId,
                label = "Test Category",
                color = Color.Blue,
            )

            val category = database.categoryQueries.get(categoryId).executeAsOneOrNull()
            Assert.assertEquals(expectedCategory, category)
        }

    @Test
    fun `should update existing category`() =
        runBlocking {
            val database = testDatabase()
            val categoryId = CategoryId.random()
            val created = LocalDateTime.ofEpochSecond(50, 0, ZoneOffset.UTC)
            val time = LocalDateTime.ofEpochSecond(1000, 0, ZoneOffset.UTC)
            val upsertCategory = UpsertCategoryUseCase(database.categoryQueries, { time })

            database.categoryQueries.upsert(
                id = categoryId,
                label = "Test Category",
                color = Color.Black,
                priority = 0,
                created = created,
                lastModified = created,
            )

            val expectedCategory =
                Category(
                    id = categoryId,
                    label = "Updated Title",
                    color = Color.Blue,
                    created = created,
                    lastModified = time,
                    priority = Long.MAX_VALUE,
                )

            upsertCategory(
                id = categoryId,
                label = "Updated Title",
                color = Color.Blue,
            )

            val category = database.categoryQueries.get(categoryId).executeAsOneOrNull()
            Assert.assertEquals(expectedCategory, category)
        }
}
