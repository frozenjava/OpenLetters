package net.frozendevelopment.mailshare.data.sqldelight

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class SqlDelightKoin {

    @Single
    fun mailShareDB(context: Context): MailShareDB {
        val driver = AndroidSqliteDriver(
            schema = MailShareDB.Schema,
            context = context,
            name = "MailShare.db"
        )

        return MailShareDB(driver = driver)
    }

    @Factory
    fun letterQueries(mailShareDB: MailShareDB) = mailShareDB.letterQueries
}
