package net.frozendevelopment.openletters.feature.settings

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
// import org.koin.android.annotation.KoinViewModel
// import org.koin.core.annotation.Module

// // @Module
// class SettingsKoinModule {
//    // @KoinViewModel
//    fun settingsViewModel(
//        application: Application,
//        themeManager: ThemeManagerType,
//    ) = SettingsViewModel(
//        themeManager = themeManager,
//    )
//
//    companion object {
//        val navigationModule = module {
//            settingsNavigation()
//        }
//    }
// }

val settingsKoinModule =
    module {
        settingsNavigation()
        viewModel {
            SettingsViewModel(
                themeManager = get(),
            )
        }
    }
