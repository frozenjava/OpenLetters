package net.frozendevelopment.openletters.migration

import net.frozendevelopment.openletters.data.sqldelight.AppMigrationQueries
import net.frozendevelopment.openletters.data.sqldelight.CategoryQueries
import net.frozendevelopment.openletters.data.sqldelight.LetterQueries
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module

@Module
class AppMigrationKoin {
    @Factory
    fun appMigrator(
        appMigrationQueries: net.frozendevelopment.openletters.data.sqldelight.AppMigrationQueries,
        categoryQueries: net.frozendevelopment.openletters.data.sqldelight.CategoryQueries,
        letterQueries: LetterQueries,
    ): AppMigrator {
        return AppMigrator(
            appMigrationQueries = appMigrationQueries,
            migrations =
                listOf(
                    InitialCategoriesMigration(categoryQueries),
                    TestDataMigration(letterQueries),
                ),
        )
    }
}
