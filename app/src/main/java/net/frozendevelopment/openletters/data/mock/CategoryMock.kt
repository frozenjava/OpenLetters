package net.frozendevelopment.openletters.data.mock

import androidx.annotation.VisibleForTesting
import androidx.compose.ui.graphics.Color
import net.frozendevelopment.openletters.data.sqldelight.migrations.Category
import net.frozendevelopment.openletters.data.sqldelight.models.CategoryId

@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
fun mockCategory(
    id: CategoryId = CategoryId.random(),
    label: String = "Category",
    color: Color = Color.Red,
    priority: Long = 0,
    created: Long = 0,
    lastModified: Long = 0,
) = Category(
    id = id,
    label = label,
    color = color,
    priority = priority,
    created = created,
    lastModified = lastModified,
)
