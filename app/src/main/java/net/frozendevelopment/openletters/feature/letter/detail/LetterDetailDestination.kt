package net.frozendevelopment.openletters.feature.letter.detail

import android.os.Bundle
import androidx.navigation.NavType
import kotlinx.serialization.Serializable
import net.frozendevelopment.openletters.data.sqldelight.models.LetterId
import kotlin.reflect.typeOf

@Serializable
data class LetterDetailDestination(
    val letterId: LetterId,
) {
    companion object {
        val typeMap =
            mapOf(
                typeOf<LetterId>() to LetterIdNavType,
            )
    }
}

val LetterIdNavType =
    object : NavType<LetterId>(isNullableAllowed = false) {
        override fun get(
            bundle: Bundle,
            key: String,
        ): LetterId? =
            bundle.getString(key).let {
                if (it == null) null else LetterId(it)
            }

        override fun parseValue(value: String): LetterId = LetterId(value)

        override fun serializeAsValue(value: LetterId): String = value.value

        override fun put(
            bundle: Bundle,
            key: String,
            value: LetterId,
        ) {
            bundle.putString(key, value.value)
        }
    }
