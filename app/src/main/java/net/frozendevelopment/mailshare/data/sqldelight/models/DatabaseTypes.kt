package net.frozendevelopment.mailshare.data.sqldelight.models

import app.cash.sqldelight.ColumnAdapter
import java.util.UUID


@JvmInline
value class LetterId(val value: String) {
    companion object {
        val adapter = object: ColumnAdapter<LetterId, String> {
            override fun decode(databaseValue: String): LetterId {
                return LetterId(databaseValue)
            }

            override fun encode(value: LetterId): String {
                return value.value
            }
        }

        fun random(): LetterId {
            return LetterId(UUID.randomUUID().toString())
        }
    }
}

@JvmInline
value class DocumentId(val value: String) {
    companion object {
        val adapter = object: ColumnAdapter<DocumentId, String> {
            override fun decode(databaseValue: String): DocumentId {
                return DocumentId(databaseValue)
            }

            override fun encode(value: DocumentId): String {
                return value.value
            }
        }

        fun random(): DocumentId {
            return DocumentId(UUID.randomUUID().toString())
        }
    }
}

@JvmInline
value class CategoryId(val value: String) {
    companion object {
        val adapter = object: ColumnAdapter<CategoryId, String> {
            override fun decode(databaseValue: String): CategoryId {
                return CategoryId(databaseValue)
            }

            override fun encode(value: CategoryId): String {
                return value.value
            }
        }

        fun random(): CategoryId {
            return CategoryId(UUID.randomUUID().toString())
        }
    }
}

@JvmInline
value class ThreadId(val value: String) {
    companion object {
        val adapter = object: ColumnAdapter<ThreadId, String> {
            override fun decode(databaseValue: String): ThreadId {
                return ThreadId(databaseValue)
            }

            override fun encode(value: ThreadId): String {
                return value.value
            }
        }

        fun random(): ThreadId {
            return ThreadId(UUID.randomUUID().toString())
        }
    }
}
