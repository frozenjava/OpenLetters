package net.frozendevelopment.openletters.migration

import net.frozendevelopment.openletters.data.sqldelight.AppMigrationQueries

interface AppMigration {
    val migrationKey: String

    operator fun invoke()
}

class AppMigrator(
    private val migrations: List<AppMigration>,
    private val appMigrationQueries: AppMigrationQueries,
) {
    fun migrate() {
        for (migration in migrations) {
            if (appMigrationQueries.getAppMigration(migration.migrationKey).executeAsOneOrNull() == null) {
                migration()
                appMigrationQueries.insertAppMigration(migration.migrationKey)
            }
        }
    }
}
