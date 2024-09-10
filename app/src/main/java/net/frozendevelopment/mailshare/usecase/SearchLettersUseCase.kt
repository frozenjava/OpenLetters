package net.frozendevelopment.mailshare.usecase

import android.database.sqlite.SQLiteException
import net.frozendevelopment.mailshare.data.sqldelight.LetterQueries
import net.frozendevelopment.mailshare.data.sqldelight.models.CategoryId
import net.frozendevelopment.mailshare.data.sqldelight.models.LetterId

class SearchLettersUseCase(
    private val letterQueries: LetterQueries,
) {
    operator fun invoke(
        query: String,
        category: CategoryId? = null,
        limit: Long = Long.MAX_VALUE
    ): List<LetterId> {
        return if (query.isBlank()) {
            letterQueries.letterList(
                categoryId = category,
                limit = limit
            ).executeAsList()
        } else {
            try {
                letterQueries.search(
                    categoryId = category,
                    query = "$query*",
                    limit = limit
                ).executeAsList()
            } catch (e: SQLiteException) {
                // a user can crash the app by constructing a query that FTS doesnt like
                // for example `*bob` will crash the app. An empty list is better than a crash
                emptyList()
            }
        }
    }
}
