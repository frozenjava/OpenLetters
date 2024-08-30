package net.frozendevelopment.mailshare

import android.app.Application
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import net.frozendevelopment.mailshare.data.sqldelight.SqlDelightKoin
import net.frozendevelopment.mailshare.feature.FeatureKoin
import net.frozendevelopment.mailshare.migration.AppMigrationKoin
import net.frozendevelopment.mailshare.usecase.UseCaseKoin
import net.frozendevelopment.mailshare.util.UtilKoin
import net.frozendevelopment.mailshare.work.AppMigrationWorker
import net.frozendevelopment.mailshare.work.WorkKoin
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module


class MailShareApplication: Application(), KoinComponent {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MailShareApplication)
            workManagerFactory()
            modules(
                SqlDelightKoin().module,
                FeatureKoin().module,
                UseCaseKoin().module,
                AppMigrationKoin().module,
                WorkKoin().module,
                UtilKoin().module
            )
        }

        initializeWorkManager()
    }

    private fun initializeWorkManager() {
        val workManager = WorkManager.getInstance(this)

        val appMigrationRequest = OneTimeWorkRequestBuilder<AppMigrationWorker>()
            .build()

        workManager.enqueue(appMigrationRequest)
    }
}
