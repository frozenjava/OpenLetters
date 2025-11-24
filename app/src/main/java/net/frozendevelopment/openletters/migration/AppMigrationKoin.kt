package net.frozendevelopment.openletters.migration

// import org.koin.core.annotation.Factory
// import org.koin.core.annotation.Module
import org.koin.dsl.module

// // @Module
// class AppMigrationKoin {
//    // @Factory
//    fun appMigrator(
//        appMigrationQueries: AppMigrationQueries,
//        categoryQueries: CategoryQueries,
//    ): AppMigrator =
//        AppMigrator(
//            appMigrationQueries = appMigrationQueries,
//            migrations =
//                listOf(
//                    InitialCategoriesMigration(categoryQueries),
//                ),
//        )
// }

val appMigrationKoinModule =
    module {
        factory {
            AppMigrator(
                appMigrationQueries = get(),
                migrations = listOf(InitialCategoriesMigration(get())),
            )
        }
    }
