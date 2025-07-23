package net.frozendevelopment.openletters

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import net.frozendevelopment.openletters.data.sqldelight.SqlDelightKoin
import net.frozendevelopment.openletters.feature.FeatureKoin
import net.frozendevelopment.openletters.migration.AppMigrationKoin
import net.frozendevelopment.openletters.usecase.UseCaseKoin
import net.frozendevelopment.openletters.util.UtilKoin
import net.frozendevelopment.openletters.work.AppMigrationWorker
import net.frozendevelopment.openletters.work.WorkKoin
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module

class OpenLettersApplication :
    Application(),
    KoinComponent {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@OpenLettersApplication)
            workManagerFactory()
            modules(
                SqlDelightKoin().module,
                FeatureKoin().module,
                UseCaseKoin().module,
                AppMigrationKoin().module,
                WorkKoin().module,
                UtilKoin().module,
            )
        }

        initializeWorkManager()
        initializeNotificationChannel()
    }

    private fun initializeWorkManager() {
        val workManager = WorkManager.getInstance(this)

        val appMigrationRequest =
            OneTimeWorkRequestBuilder<AppMigrationWorker>()
                .build()

        workManager.enqueue(appMigrationRequest)
    }

    private fun initializeNotificationChannel() {
        val notificationChannel =
            NotificationChannel(
                REMINDERS_CHANNEL_ID,
                REMINDERS_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH,
            )

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }
}
