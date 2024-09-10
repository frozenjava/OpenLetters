package net.frozendevelopment.mailshare.work

import android.content.Context
import androidx.work.WorkerParameters
import net.frozendevelopment.mailshare.migration.AppMigrator
import org.koin.android.annotation.KoinWorker
import org.koin.core.annotation.Module
import org.koin.core.annotation.Provided

@Module
class WorkKoin {
    @KoinWorker
    fun appMigrationWorker(
        context: Context,
        @Provided parameters: WorkerParameters,
        appMigrator: AppMigrator,
    ): AppMigrationWorker {
        return AppMigrationWorker(context, parameters, appMigrator)
    }
}
