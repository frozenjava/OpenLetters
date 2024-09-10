package net.frozendevelopment.mailshare.usecase

import net.frozendevelopment.mailshare.data.sqldelight.CategoryQueries
import net.frozendevelopment.mailshare.data.sqldelight.LetterQueries
import net.frozendevelopment.mailshare.data.sqldelight.MailShareDB
import net.frozendevelopment.mailshare.util.DocumentManagerType
import net.frozendevelopment.mailshare.util.TextExtractorType
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module

@Module
class UseCaseKoin {

    @Factory
    fun createLetterUseCase(
        documentManager: DocumentManagerType,
        textExtractor: TextExtractorType,
        database: MailShareDB
    ): CreateLetterUseCase {
        return CreateLetterUseCase(
            documentManager,
            textExtractor,
            database
        )
    }

    @Factory
    fun metaLetterUseCase(
        letterQueries: LetterQueries
    ): MetaLetterUseCase {
        return MetaLetterUseCase(queries = letterQueries)
    }

    @Factory
    fun upsertCategoryUseCase(
        categoryQueries: CategoryQueries
    ) = UpsertCategoryUseCase(categoryQueries)

    @Factory
    fun letterWithDetailsUseCase(
        documentManager: DocumentManagerType,
        database: MailShareDB
    ) = LetterWithDetailsUseCase(documentManager, database)

    @Factory
    fun searchLettersUseCase(
        letterQueries: LetterQueries
    ) = SearchLettersUseCase(letterQueries)
}
