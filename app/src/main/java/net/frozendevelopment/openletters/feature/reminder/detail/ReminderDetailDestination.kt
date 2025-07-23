package net.frozendevelopment.openletters.feature.reminder.detail

import android.os.Bundle
import androidx.navigation.NavType
import androidx.navigation.navDeepLink
import kotlinx.serialization.Serializable
import net.frozendevelopment.openletters.DEEP_LINK_URI
import net.frozendevelopment.openletters.data.sqldelight.models.ReminderId
import kotlin.reflect.typeOf

@Serializable
data class ReminderDetailDestination(
    val reminderId: ReminderId,
) {
    companion object {
        val typeMap = mapOf(typeOf<ReminderId>() to ReminderIdNavType)
        val deepLinks =
            listOf(
                navDeepLink<ReminderDetailDestination>(
                    basePath = "$DEEP_LINK_URI/reminder",
                    typeMap = typeMap,
                ),
            )
    }
}

val ReminderIdNavType =
    object : NavType<ReminderId>(isNullableAllowed = false) {
        override fun get(
            bundle: Bundle,
            key: String,
        ): ReminderId? =
            bundle.getString(key).let {
                if (it == null) null else ReminderId(it)
            }

        override fun parseValue(value: String): ReminderId = ReminderId(value)

        override fun serializeAsValue(value: ReminderId): String = value.value

        override fun put(
            bundle: Bundle,
            key: String,
            value: ReminderId,
        ) {
            bundle.putString(key, value.value)
        }
    }
