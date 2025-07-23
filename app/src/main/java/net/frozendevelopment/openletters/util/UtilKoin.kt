package net.frozendevelopment.openletters.util

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Module
class UtilKoin {
    @Factory
    fun textExtractor(application: Application): TextExtractorType = TextExtractor(application)

    @Factory
    fun documentManager(application: Application): DocumentManagerType = DocumentManager(application)

    @Factory
    fun themeManager(application: Application): ThemeManagerType = ThemeManager(datastore = application.dataStore)
}
