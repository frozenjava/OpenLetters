package net.frozendevelopment.openletters.feature.settings

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.frozendevelopment.openletters.ui.theme.AppTheme
import net.frozendevelopment.openletters.ui.theme.ColorPalette
import net.frozendevelopment.openletters.util.StatefulViewModel
import net.frozendevelopment.openletters.util.ThemeManagerType

@Immutable
data class SettingsState(
    val appVersion: String,
    val appTheme: AppTheme = AppTheme.OPEN_LETTERS,
    val themeVariant: ColorPalette = ColorPalette.SYSTEM,
) {
    val availableThemes
        get() = AppTheme.available

    val availableVariants
        get() = ColorPalette.entries
}

class SettingsViewModel(
    private val appVersion: String,
    private val themeManager: ThemeManagerType
) : StatefulViewModel<SettingsState>(SettingsState(appVersion)) {
    override fun load() {
        viewModelScope.launch {
            themeManager.current.collect { (theme, variant) ->
                update { copy(appTheme = theme, themeVariant = variant) }
            }
        }
    }

    fun setTheme(theme: AppTheme) = viewModelScope.launch {
        themeManager.setTheme(theme)
        update { copy(appTheme = theme) }
    }

    fun setVariant(variant: ColorPalette) = viewModelScope.launch {
        themeManager.setVariant(variant)
        update { copy(themeVariant = variant) }
    }
}
