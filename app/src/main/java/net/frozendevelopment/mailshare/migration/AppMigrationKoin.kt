package net.frozendevelopment.mailshare.migration

import net.frozendevelopment.mailshare.data.sqldelight.AppMigrationQueries
import net.frozendevelopment.mailshare.data.sqldelight.CategoryQueries
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module

@Module
class AppMigrationKoin {
    @Factory
    fun appMigrator(
        appMigrationQueries: AppMigrationQueries,
        categoryQueries: CategoryQueries,
    ): AppMigrator {
        return AppMigrator(
            appMigrationQueries = appMigrationQueries,
            migrations = listOf(
                InitialCategoriesMigration(categoryQueries)
            )
        )
    }
}
