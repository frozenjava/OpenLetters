package net.frozendevelopment.openletters

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import net.frozendevelopment.openletters.data.sqldelight.sqlDelightKoinModule
import net.frozendevelopment.openletters.feature.category.categoryKoinModule
import net.frozendevelopment.openletters.feature.letter.letterKoinModule
import net.frozendevelopment.openletters.feature.reminder.reminderKoinModule
import net.frozendevelopment.openletters.feature.settings.settingsKoinModule
import net.frozendevelopment.openletters.migration.appMigrationKoinModule
import net.frozendevelopment.openletters.usecase.useCaseKoinModule
import net.frozendevelopment.openletters.util.utilKoinModule
import net.frozendevelopment.openletters.work.AppMigrationWorker
import net.frozendevelopment.openletters.work.workKoinModule
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin

class OpenLettersApplication :
    Application(),
    KoinComponent {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@OpenLettersApplication)
            workManagerFactory()
            modules(
                // SqlDelightKoin().module,
                sqlDelightKoinModule,
                // FeatureKoin().module,
                letterKoinModule,
                categoryKoinModule,
                reminderKoinModule,
                settingsKoinModule,
                // UseCaseKoin().module,
                useCaseKoinModule,
                // AppMigrationKoin().module,
                appMigrationKoinModule,
//                WorkKoin().module,
                workKoinModule,
                // UtilKoin().module,
                utilKoinModule,
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
