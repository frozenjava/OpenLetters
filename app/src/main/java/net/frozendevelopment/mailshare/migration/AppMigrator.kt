package net.frozendevelopment.mailshare.migration

import net.frozendevelopment.mailshare.data.sqldelight.AppMigrationQueries

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
