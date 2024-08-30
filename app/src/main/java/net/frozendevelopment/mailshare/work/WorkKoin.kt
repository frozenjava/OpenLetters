package net.frozendevelopment.mailshare.work

import android.content.Context
import androidx.work.WorkerParameters
import net.frozendevelopment.mailshare.migration.AppMigrator
import org.koin.android.annotation.KoinWorker
import org.koin.core.annotation.Module

@Module
class WorkKoin {
    @KoinWorker
    fun appMigrationWorker(
        context: Context,
        parameters: WorkerParameters,
        appMigrator: AppMigrator,
    ): AppMigrationWorker {
        return AppMigrationWorker(context, parameters, appMigrator)
    }
}
