package net.frozendevelopment.openletters.data.sqldelight.models

import android.os.Parcelable
import app.cash.sqldelight.ColumnAdapter
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.frozendevelopment.openletters.data.sqldelight.migrations.Category
import net.frozendevelopment.openletters.data.sqldelight.models.CategoryId
import java.util.UUID

@JvmInline
@Parcelize
@Serializable(with = LetterId.Serializer::class)
value class LetterId(val value: String): Parcelable {
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

    object Serializer: KSerializer<LetterId> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LetterId", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): LetterId {
            return LetterId(decoder.decodeString())
        }

        override fun serialize(encoder: Encoder, value: LetterId) {
            encoder.encodeString(value.value)
        }
    }

    override fun toString(): String {
        return value
    }
}

@JvmInline
@Parcelize
@Serializable(with = DocumentId.Serializer::class)
value class DocumentId(val value: String): Parcelable {
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

    object Serializer: KSerializer<DocumentId> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("DocumentId", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): DocumentId {
            return DocumentId(decoder.decodeString())
        }

        override fun serialize(encoder: Encoder, value: DocumentId) {
            encoder.encodeString(value.value)
        }
    }

    override fun toString(): String {
        return value
    }
}

@JvmInline
@Parcelize
@Serializable(with = CategoryId.Serializer::class)
value class CategoryId(val value: String): Parcelable {
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

    object Serializer: KSerializer<CategoryId> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("CategoryId", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): CategoryId {
            return CategoryId(decoder.decodeString())
        }

        override fun serialize(encoder: Encoder, value: CategoryId) {
            encoder.encodeString(value.value)
        }
    }

    override fun toString(): String {
        return value
    }
}

@JvmInline
@Parcelize
@Serializable(with = ThreadId.Serializer::class)
value class ThreadId(val value: String): Parcelable {
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

    object Serializer: KSerializer<ThreadId> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ThreadId", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): ThreadId {
            return ThreadId(decoder.decodeString())
        }

        override fun serialize(encoder: Encoder, value: ThreadId) {
            encoder.encodeString(value.value)
        }
    }

    override fun toString(): String {
        return value
    }
}
