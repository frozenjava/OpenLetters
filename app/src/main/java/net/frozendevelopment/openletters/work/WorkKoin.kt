package net.frozendevelopment.openletters.work

import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module

// @Module
// class WorkKoin {
//    @KoinWorker
//    fun appMigrationWorker(
//        context: Context,
//        @Provided parameters: WorkerParameters,
//        appMigrator: AppMigrator,
//    ): AppMigrationWorker = AppMigrationWorker(context, parameters, appMigrator)
// }

val workKoinModule =
    module {
        worker { AppMigrationWorker(androidContext(), get(), get()) }
    }
