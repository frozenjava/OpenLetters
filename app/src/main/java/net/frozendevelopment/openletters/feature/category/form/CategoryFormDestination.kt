package net.frozendevelopment.openletters.feature.category.form

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavType
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.frozendevelopment.openletters.data.sqldelight.models.CategoryId

@Serializable
sealed interface CategoryFormMode : Parcelable {
    @Serializable
    @Parcelize
    data object Create : CategoryFormMode

    @Serializable
    @Parcelize
    data class Edit(
        val id: CategoryId,
    ) : CategoryFormMode
}

@Serializable
data class CategoryFormDestination(
    val mode: CategoryFormMode = CategoryFormMode.Create,
)

val CategoryFormModeType =
    object : NavType<CategoryFormMode>(isNullableAllowed = false) {
        override fun get(
            bundle: Bundle,
            key: String,
        ): CategoryFormMode? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getParcelable(key, CategoryFormMode::class.java)
            } else {
                @Suppress("DEPRECATION")
                bundle.getParcelable(key)
            }

        override fun parseValue(value: String): CategoryFormMode = Json.decodeFromString(value)

        override fun serializeAsValue(value: CategoryFormMode): String = Json.encodeToString(value)

        override fun put(
            bundle: Bundle,
            key: String,
            value: CategoryFormMode,
        ) {
            bundle.putParcelable(key, value)
        }
    }
