package net.frozendevelopment.openletters.feature.letter.scan

import android.os.Bundle
import androidx.navigation.NavType
import kotlinx.serialization.Serializable
import net.frozendevelopment.openletters.data.sqldelight.models.LetterId
import kotlin.reflect.typeOf

@Serializable
data class ScanLetterDestination(
    val letterId: LetterId? = null,
    val canNavigateBack: Boolean = true,
) {
    companion object {
        val typeMap =
            mapOf(
                typeOf<LetterId?>() to NullableLetterIdNavType,
            )
    }
}

val NullableLetterIdNavType =
    object : NavType<LetterId?>(isNullableAllowed = true) {
        override fun get(
            bundle: Bundle,
            key: String,
        ): LetterId? =
            bundle.getString(key).let {
                if (it == null) null else LetterId(it)
            }

        override fun parseValue(value: String): LetterId? {
            if (value.isBlank()) return null
            return LetterId(value)
        }

        override fun serializeAsValue(value: LetterId?): String = value?.value ?: ""

        override fun put(
            bundle: Bundle,
            key: String,
            value: LetterId?,
        ) {
            value ?: return
            bundle.putString(key, value.value)
        }
    }
