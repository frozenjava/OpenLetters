package net.frozendevelopment.openletters.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.OpenInBrowser
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import net.frozendevelopment.openletters.R
import net.frozendevelopment.openletters.feature.settings.ui.DropDownButton
import net.frozendevelopment.openletters.ui.components.VersionStamp
import net.frozendevelopment.openletters.ui.theme.AppTheme
import net.frozendevelopment.openletters.ui.theme.ColorPalette
import net.frozendevelopment.openletters.ui.theme.OpenLettersTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsView(
    modifier: Modifier = Modifier,
    state: SettingsState,
    onBackClicked: () -> Unit,
    onThemeChanged: (AppTheme) -> Unit,
    onColorVariantChanged: (ColorPalette) -> Unit,
    onViewSourceClicked: () -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CenterAlignedTopAppBar(
            title = { Text(stringResource(R.string.settings)) },
            navigationIcon = {
                IconButton(onClick = onBackClicked) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                    )
                }
            },
        )

        DropDownButton(
            modifier = Modifier.fillMaxWidth(.95f),
            title = { Text(stringResource(R.string.theme)) },
            value = { Text(state.appTheme.label) },
            menuItems = state.availableThemes,
            menuItemLabel = { Text(it.label) },
            onMenuItemSelected = onThemeChanged,
        )

        DropDownButton(
            modifier = Modifier.fillMaxWidth(.95f),
            title = { Text(stringResource(R.string.color_palette)) },
            value = { Text(state.themeVariant.label) },
            menuItems = state.availableVariants,
            menuItemLabel = { Text(it.label) },
            onMenuItemSelected = onColorVariantChanged,
        )

        HorizontalDivider()

        TextButton(
            modifier = Modifier.fillMaxWidth(.95f),
            onClick = onViewSourceClicked,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(stringResource(R.string.view_source))
                Icon(imageVector = Icons.Outlined.OpenInBrowser, contentDescription = null)
            }
        }

        Spacer(modifier = Modifier.weight(1f, fill = true))

        VersionStamp(modifier = Modifier.navigationBarsPadding())
    }
}

private val ColorPalette.label: String
    @Composable get() =
        stringResource(
            when (this) {
                ColorPalette.LIGHT -> R.string.light
                ColorPalette.DARK -> R.string.dark
                ColorPalette.SYSTEM -> R.string.system
            },
        )

private val AppTheme.label: String
    @Composable get() =
        stringResource(
            when (this) {
                AppTheme.MATERIAL_YOU -> R.string.material_you
                AppTheme.HIGH_CONTRAST -> R.string.high_contrast
                AppTheme.MEDIUM_CONTRAST -> R.string.medium_contrast
                AppTheme.OPEN_LETTERS -> R.string.open_letters
            },
        )

@Composable
private fun SettingsPreview(darkTheme: Boolean) {
    OpenLettersTheme(darkTheme = darkTheme) {
        Surface {
            SettingsView(
                modifier = Modifier.fillMaxSize(),
                state = SettingsState(),
                onBackClicked = {},
                onThemeChanged = {},
                onColorVariantChanged = {},
                onViewSourceClicked = {},
            )
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun SettingsLightPreview() {
    SettingsPreview(darkTheme = false)
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun SettingsDarkPreview() {
    SettingsPreview(darkTheme = true)
}
