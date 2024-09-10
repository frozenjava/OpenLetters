package net.frozendevelopment.openletters.feature.letter.detail

import androidx.core.bundle.Bundle
import androidx.navigation.NavType
import kotlinx.serialization.Serializable
import net.frozendevelopment.openletters.data.sqldelight.models.LetterId

@Serializable
data class LetterDetailDestination(val letterId: LetterId)

val LetterIdNavType = object : NavType<LetterId>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): LetterId? {
        return bundle.getString(key).let {
            if (it == null) null else LetterId(it)
        }
    }

    override fun parseValue(value: String): LetterId {
        return LetterId(value)
    }

    override fun serializeAsValue(value: LetterId): String {
        return value.value
    }

    override fun put(bundle: Bundle, key: String, value: LetterId) {
        bundle.putString(key, value.value)
    }
}
