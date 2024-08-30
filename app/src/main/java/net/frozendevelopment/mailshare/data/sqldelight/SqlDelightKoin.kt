package net.frozendevelopment.mailshare.data.sqldelight

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory
import net.frozendevelopment.mailshare.data.sqldelight.migrations.Category
import net.frozendevelopment.mailshare.data.sqldelight.migrations.Document
import net.frozendevelopment.mailshare.data.sqldelight.models.LetterId
import net.frozendevelopment.mailshare.`data`.sqldelight.migrations.Letter
import net.frozendevelopment.mailshare.data.sqldelight.migrations.LetterToCategory
import net.frozendevelopment.mailshare.data.sqldelight.migrations.LetterToThread
import net.frozendevelopment.mailshare.`data`.sqldelight.migrations.Thread
import net.frozendevelopment.mailshare.data.sqldelight.models.CategoryId
import net.frozendevelopment.mailshare.data.sqldelight.models.DocumentId
import net.frozendevelopment.mailshare.data.sqldelight.models.ThreadId
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
            name = "mailshare.db",
            factory = RequerySQLiteOpenHelperFactory()
        )

        return MailShareDB(
            driver = driver,
            letterAdapter = Letter.Adapter(
                idAdapter = LetterId.adapter,
            ),
            documentAdapter = Document.Adapter(
                idAdapter = DocumentId.adapter,
                letterIdAdapter = LetterId.adapter,
            ),
            categoryAdapter = Category.Adapter(
                idAdapter = CategoryId.adapter,
            ),
            threadAdapter = Thread.Adapter(
                idAdapter = ThreadId.adapter
            ),
            letterToThreadAdapter = LetterToThread.Adapter(
                letterIdAdapter = LetterId.adapter,
                threadIdAdapter = ThreadId.adapter
            ),
            letterToCategoryAdapter = LetterToCategory.Adapter(
                letterIdAdapter = LetterId.adapter,
                categoryIdAdapter = CategoryId.adapter
            )
        )
    }

    @Factory
    fun letterQueries(mailShareDB: MailShareDB) = mailShareDB.letterQueries

    @Factory
    fun categoryQueries(mailShareDB: MailShareDB) = mailShareDB.categoryQueries

    @Factory
    fun threadQueries(mailShareDB: MailShareDB) = mailShareDB.threadQueries

    @Factory
    fun documentQueries(mailShareDB: MailShareDB) = mailShareDB.documentQueries

    @Factory
    fun appMigrationQueries(mailShareDB: MailShareDB) = mailShareDB.appMigrationQueries
}
