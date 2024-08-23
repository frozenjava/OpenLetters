package net.frozendevelopment.mailshare

import android.app.Application
import net.frozendevelopment.mailshare.data.sqldelight.SqlDelightKoin
import net.frozendevelopment.mailshare.feature.FeatureKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module


class MailShareApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MailShareApplication)
            modules(
                SqlDelightKoin().module,
                FeatureKoin().module
            )
        }
    }

}