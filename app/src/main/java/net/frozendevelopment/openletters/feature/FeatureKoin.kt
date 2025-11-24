package net.frozendevelopment.openletters.feature

import net.frozendevelopment.openletters.feature.category.categoryKoinModule
import net.frozendevelopment.openletters.feature.letter.letterKoinModule
import net.frozendevelopment.openletters.feature.reminder.reminderKoinModule
import net.frozendevelopment.openletters.feature.settings.settingsKoinModule

// @Module(
//     includes = [
//         LetterKoinModule::class,
//         CategoryKoinModule::class,
//         ReminderKoinModule::class,
//         SettingsKoinModule::class
//     ]
// )
class FeatureKoin

val featureKoinModules =
    listOf(
        categoryKoinModule,
        letterKoinModule,
        reminderKoinModule,
        settingsKoinModule,
//    LetterKoinModule.navigationModule,
//    CategoryKoinModule.navigationModule,
//    ReminderKoinModule.navigationModule,
//    SettingsKoinModule.navigationModule,
    )
