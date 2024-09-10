package net.frozendevelopment.openletters.data.sqldelight

import android.content.Context
import android.util.Log
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import app.cash.sqldelight.logs.LogSqliteDriver
import io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory
import net.frozendevelopment.openletters.data.sqldelight.migrations.Category
import net.frozendevelopment.openletters.data.sqldelight.migrations.Document
import net.frozendevelopment.openletters.data.sqldelight.models.LetterId
import net.frozendevelopment.openletters.`data`.sqldelight.migrations.Letter
import net.frozendevelopment.openletters.data.sqldelight.migrations.LetterToCategory
import net.frozendevelopment.openletters.data.sqldelight.migrations.LetterToThread
import net.frozendevelopment.openletters.data.sqldelight.migrations.Thread
import net.frozendevelopment.openletters.data.sqldelight.models.CategoryId
import net.frozendevelopment.openletters.data.sqldelight.models.ColorAdapter
import net.frozendevelopment.openletters.data.sqldelight.models.DocumentId
import net.frozendevelopment.openletters.data.sqldelight.models.ThreadId
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class SqlDelightKoin {

    @Single
    fun openLettersDB(context: Context): OpenLettersDB {
        val driver = AndroidSqliteDriver(
            schema = OpenLettersDB.Schema,
            context = context,
            name = "openletters.db",
            factory = RequerySQLiteOpenHelperFactory()
        )

        val logDriver: SqlDriver = LogSqliteDriver(sqlDriver = driver) { log ->
            Log.d("SQLDELIGHT", log)
        }

        return OpenLettersDB(
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
    fun letterQueries(openLettersDB: OpenLettersDB) = openLettersDB.letterQueries

    @Factory
    fun categoryQueries(openLettersDB: OpenLettersDB) = openLettersDB.categoryQueries

    @Factory
    fun threadQueries(openLettersDB: OpenLettersDB) = openLettersDB.threadQueries

    @Factory
    fun documentQueries(openLettersDB: OpenLettersDB) = openLettersDB.documentQueries

    @Factory
    fun appMigrationQueries(openLettersDB: OpenLettersDB) = openLettersDB.appMigrationQueries
}
