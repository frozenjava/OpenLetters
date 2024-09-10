package net.frozendevelopment.openletters.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import net.frozendevelopment.openletters.migration.AppMigrator

class AppMigrationWorker(
    context: Context,
    parameters: WorkerParameters,
    private val appMigrator: AppMigrator,
) : CoroutineWorker(context, parameters) {
    override suspend fun doWork(): Result {
        appMigrator.migrate()
        return Result.success()
    }
}
