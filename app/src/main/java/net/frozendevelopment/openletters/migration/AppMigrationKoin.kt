package net.frozendevelopment.openletters.migration

import net.frozendevelopment.openletters.data.sqldelight.AppMigrationQueries
import net.frozendevelopment.openletters.data.sqldelight.CategoryQueries
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module

@Module
class AppMigrationKoin {
    @Factory
    fun appMigrator(
        appMigrationQueries: AppMigrationQueries,
        categoryQueries: CategoryQueries,
    ): AppMigrator =
        AppMigrator(
            appMigrationQueries = appMigrationQueries,
            migrations =
                listOf(
                    InitialCategoriesMigration(categoryQueries),
                ),
        )
}
