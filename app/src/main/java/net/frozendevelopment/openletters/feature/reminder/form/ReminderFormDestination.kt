package net.frozendevelopment.openletters.feature.reminder.form

import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavType
import kotlinx.serialization.Serializable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.frozendevelopment.openletters.data.sqldelight.models.ReminderId
import kotlin.reflect.typeOf

@Serializable
data class ReminderFormDestination(val reminderId: ReminderId? = null) {
    companion object {
        val typeMap = mapOf(typeOf<ReminderId?>() to NullableReminderIdNavType)
    }
}

val NullableReminderIdNavType = object : NavType<ReminderId?>(isNullableAllowed = true) {
    override fun get(bundle: Bundle, key: String): ReminderId? {
        return bundle.getString(key).let {
            if (it == null) null else ReminderId(it)
        }
    }

    override fun parseValue(value: String): ReminderId {
        return ReminderId(value)
    }

    override fun serializeAsValue(value: ReminderId?): String {
        return value?.value ?: ""
    }

    override fun put(bundle: Bundle, key: String, value: ReminderId?) {
        value ?: return
        bundle.putString(key, value.value)
    }
}
