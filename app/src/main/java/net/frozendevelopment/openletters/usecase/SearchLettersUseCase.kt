package net.frozendevelopment.openletters.usecase

import android.database.sqlite.SQLiteException
import net.frozendevelopment.openletters.data.sqldelight.LetterQueries
import net.frozendevelopment.openletters.data.sqldelight.models.CategoryId
import net.frozendevelopment.openletters.data.sqldelight.models.LetterId
import net.frozendevelopment.openletters.extensions.sanitizeForSearch

class SearchLettersUseCase(
    private val letterQueries: LetterQueries,
) {
    operator fun invoke(
        query: String,
        category: CategoryId? = null,
        limit: Long = Long.MAX_VALUE,
    ): List<LetterId> = if (query.isBlank()) {
        letterQueries
            .letterList(
                categoryId = category,
                limit = limit,
            ).executeAsList()
    } else {
        try {
            letterQueries
                .search(
                    categoryId = category,
                    query = "${query.sanitizeForSearch()}*",
                    limit = limit,
                ).executeAsList()
        } catch (e: SQLiteException) {
            // a user can crash the app by constructing a query that FTS doesnt like
            // for example `*bob` will crash the app. An empty list is better than a crash
            emptyList()
        }
    }
}
