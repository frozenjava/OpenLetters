package net.frozendevelopment.openletters.data.sqldelight

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory
import net.frozendevelopment.openletters.data.sqldelight.migrations.Category
import net.frozendevelopment.openletters.data.sqldelight.migrations.Document
import net.frozendevelopment.openletters.`data`.sqldelight.migrations.Letter
import net.frozendevelopment.openletters.data.sqldelight.migrations.LetterToCategory
import net.frozendevelopment.openletters.data.sqldelight.migrations.LetterToReminder
import net.frozendevelopment.openletters.data.sqldelight.migrations.Reminder
import net.frozendevelopment.openletters.data.sqldelight.models.CategoryId
import net.frozendevelopment.openletters.data.sqldelight.models.ColorAdapter
import net.frozendevelopment.openletters.data.sqldelight.models.DocumentId
import net.frozendevelopment.openletters.data.sqldelight.models.LetterId
import net.frozendevelopment.openletters.data.sqldelight.models.LocalDateTimeAdapter
import net.frozendevelopment.openletters.data.sqldelight.models.ReminderId
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class SqlDelightKoin {
    @Single
    fun databaseDriver(context: Context): SqlDriver =
        AndroidSqliteDriver(
            schema = OpenLettersDB.Schema,
            context = context,
            name = "openletters.db",
            factory = RequerySQLiteOpenHelperFactory(),
        )

    @Single
    fun openLettersDB(driver: SqlDriver): OpenLettersDB {
        val appDriver =
            driver.apply {
                execute(null, "PRAGMA foreign_keys = ON;", 0)
            }

        return OpenLettersDB(
            driver = appDriver,
            letterAdapter =
                Letter.Adapter(
                    idAdapter = LetterId.adapter,
                    createdAdapter = LocalDateTimeAdapter,
                    lastModifiedAdapter = LocalDateTimeAdapter,
                ),
            documentAdapter =
                Document.Adapter(
                    idAdapter = DocumentId.adapter,
                    letterIdAdapter = LetterId.adapter,
                ),
            categoryAdapter =
                Category.Adapter(
                    idAdapter = CategoryId.adapter,
                    colorAdapter = ColorAdapter,
                    createdAdapter = LocalDateTimeAdapter,
                    lastModifiedAdapter = LocalDateTimeAdapter,
                ),
            letterToCategoryAdapter =
                LetterToCategory.Adapter(
                    letterIdAdapter = LetterId.adapter,
                    categoryIdAdapter = CategoryId.adapter,
                ),
            reminderAdapter =
                Reminder.Adapter(
                    idAdapter = ReminderId.adapter,
                    createdAdapter = LocalDateTimeAdapter,
                    lastModifiedAdapter = LocalDateTimeAdapter,
                    scheduledForAdapter = LocalDateTimeAdapter,
                ),
            letterToReminderAdapter =
                LetterToReminder.Adapter(
                    letterIdAdapter = LetterId.adapter,
                    reminderIdAdapter = ReminderId.adapter,
                ),
        )
    }

    @Factory
    fun reminderQueries(openLettersDB: OpenLettersDB) = openLettersDB.reminderQueries

    @Factory
    fun letterQueries(openLettersDB: OpenLettersDB) = openLettersDB.letterQueries

    @Factory
    fun categoryQueries(openLettersDB: OpenLettersDB) = openLettersDB.categoryQueries

    @Factory
    fun documentQueries(openLettersDB: OpenLettersDB) = openLettersDB.documentQueries

    @Factory
    fun appMigrationQueries(openLettersDB: OpenLettersDB) = openLettersDB.appMigrationQueries
}
