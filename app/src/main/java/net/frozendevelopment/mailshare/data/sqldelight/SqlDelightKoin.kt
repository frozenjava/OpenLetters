package net.frozendevelopment.mailshare.data.sqldelight

import android.content.Context
import android.util.Log
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import app.cash.sqldelight.logs.LogSqliteDriver
import io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory
import net.frozendevelopment.mailshare.data.sqldelight.migrations.Category
import net.frozendevelopment.mailshare.data.sqldelight.migrations.Document
import net.frozendevelopment.mailshare.data.sqldelight.models.LetterId
import net.frozendevelopment.mailshare.`data`.sqldelight.migrations.Letter
import net.frozendevelopment.mailshare.data.sqldelight.migrations.LetterToCategory
import net.frozendevelopment.mailshare.data.sqldelight.migrations.LetterToThread
import net.frozendevelopment.mailshare.`data`.sqldelight.migrations.Thread
import net.frozendevelopment.mailshare.data.sqldelight.models.CategoryId
import net.frozendevelopment.mailshare.data.sqldelight.models.ColorAdapter
import net.frozendevelopment.mailshare.data.sqldelight.models.DocumentId
import net.frozendevelopment.mailshare.data.sqldelight.models.ThreadId
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import kotlin.math.log

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

        val logDriver: SqlDriver = LogSqliteDriver(sqlDriver = driver) { log ->
            Log.d("SQLDELIGHT", log)
        }

        return MailShareDB(
            driver = logDriver,
            letterAdapter = Letter.Adapter(
                idAdapter = LetterId.adapter,
            ),
            documentAdapter = Document.Adapter(
                idAdapter = DocumentId.adapter,
                letterIdAdapter = LetterId.adapter,
            ),
            categoryAdapter = Category.Adapter(
                idAdapter = CategoryId.adapter,
                colorAdapter = ColorAdapter,
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
