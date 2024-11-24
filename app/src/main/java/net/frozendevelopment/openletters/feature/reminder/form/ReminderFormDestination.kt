package net.frozendevelopment.openletters.feature.reminder.form

import android.os.Bundle
import androidx.navigation.NavType
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.frozendevelopment.openletters.data.sqldelight.models.LetterId
import net.frozendevelopment.openletters.data.sqldelight.models.ReminderId
import kotlin.reflect.typeOf

@Serializable
data class ReminderFormDestination(
    val reminderId: ReminderId? = null,
    val preselectedLetters: List<LetterId> = emptyList(),
) {
    companion object {
        val typeMap =
            mapOf(
                typeOf<ReminderId?>() to NullableReminderIdNavType,
                typeOf<List<LetterId>>() to LetterListNavType,
            )
    }
}

val NullableReminderIdNavType =
    object : NavType<ReminderId?>(isNullableAllowed = true) {
        override fun get(
            bundle: Bundle,
            key: String,
        ): ReminderId? {
            return bundle.getString(key).let {
                if (it == null) null else ReminderId(it)
            }
        }

        override fun parseValue(value: String): ReminderId? {
            if (value.isBlank()) return null
            return ReminderId(value)
        }

        override fun serializeAsValue(value: ReminderId?): String {
            return value?.value ?: ""
        }

        override fun put(
            bundle: Bundle,
            key: String,
            value: ReminderId?,
        ) {
            value ?: return
            bundle.putString(key, value.value)
        }
    }

val LetterListNavType =
    object : NavType<List<LetterId>>(isNullableAllowed = false) {
        override fun get(
            bundle: Bundle,
            key: String,
        ): List<LetterId> {
            return bundle.getStringArrayList(key)?.map { LetterId(it) } ?: emptyList()
        }

        override fun parseValue(value: String): List<LetterId> {
            return Json.decodeFromString(value)
        }

        override fun serializeAsValue(value: List<LetterId>): String {
            return Json.encodeToString(value)
        }

        override fun put(
            bundle: Bundle,
            key: String,
            value: List<LetterId>,
        ) {
            bundle.putStringArrayList(key, ArrayList(value.map { it.value }))
        }
    }
