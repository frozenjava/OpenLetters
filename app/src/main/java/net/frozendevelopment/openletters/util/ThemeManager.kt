package net.frozendevelopment.openletters.util

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import net.frozendevelopment.openletters.ui.theme.AppTheme
import net.frozendevelopment.openletters.ui.theme.ColorPalette

interface ThemeManagerType {
    val current: StateFlow<Pair<AppTheme, ColorPalette>>
    suspend fun setTheme(theme: AppTheme)
    suspend fun setVariant(variant: ColorPalette)
//    fun current(): Flow<Pair<AppTheme, AppThemeVariant>>
}

class ThemeManager(
    private val datastore: DataStore<Preferences>,
    coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
): ThemeManagerType {
    private val themeKey = stringPreferencesKey("theme")
    private val variantKey = stringPreferencesKey("variant")

    override val current: StateFlow<Pair<AppTheme, ColorPalette>> = datastore.data.map { preferences ->
        val themeName = preferences[themeKey] ?: AppTheme.OPEN_LETTERS.name
        val variantName = preferences[variantKey] ?: ColorPalette.LIGHT.name
        Pair(AppTheme.valueOf(themeName), ColorPalette.valueOf(variantName))
    }.stateIn(
        coroutineScope,
        SharingStarted.Eagerly,
        Pair(AppTheme.OPEN_LETTERS, ColorPalette.SYSTEM)
    )

    override suspend fun setTheme(theme: AppTheme) {
        datastore.edit { preferences ->
            preferences[themeKey] = theme.name
        }
    }

    override suspend fun setVariant(variant: ColorPalette) {
        datastore.edit { preferences ->
            preferences[variantKey] = variant.name
        }
    }
}
