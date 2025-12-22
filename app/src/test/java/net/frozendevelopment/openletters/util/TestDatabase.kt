package net.frozendevelopment.openletters.util

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import net.frozendevelopment.openletters.data.sqldelight.OpenLettersDB
import net.frozendevelopment.openletters.extensions.invoke
import org.sqlite.JDBC
import java.sql.DriverManager

fun testDatabase(): OpenLettersDB {
    DriverManager.registerDriver(JDBC())

    val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).apply {
        OpenLettersDB.Schema.create(this)
    }

    return OpenLettersDB(driver)
}
