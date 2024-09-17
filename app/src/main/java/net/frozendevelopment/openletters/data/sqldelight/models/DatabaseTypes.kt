package net.frozendevelopment.openletters.data.sqldelight.models

import android.os.Parcelable
import app.cash.sqldelight.ColumnAdapter
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDateTime
import java.time.ZoneOffset
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
@Serializable(with = ReminderId.Serializer::class)
value class ReminderId(val value: String): Parcelable {
    companion object {
        val adapter = object: ColumnAdapter<ReminderId, String> {
            override fun decode(databaseValue: String): ReminderId {
                return ReminderId(databaseValue)
            }

            override fun encode(value: ReminderId): String {
                return value.value
            }
        }

        fun random(): ReminderId {
            return ReminderId(UUID.randomUUID().toString())
        }
    }

    object Serializer: KSerializer<ReminderId> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ReminderId", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): ReminderId {
            return ReminderId(decoder.decodeString())
        }

        override fun serialize(encoder: Encoder, value: ReminderId) {
            encoder.encodeString(value.value)
        }
    }

    override fun toString(): String {
        return value
    }
}

object LocalDateTimeAdapter: ColumnAdapter<LocalDateTime, Long> {
    override fun decode(databaseValue: Long): LocalDateTime {
        return LocalDateTime.ofEpochSecond(databaseValue, 0, ZoneOffset.UTC)
    }

    override fun encode(value: LocalDateTime): Long {
        return value.toEpochSecond(ZoneOffset.UTC)
    }
}

object BooleanAdapter: ColumnAdapter<Boolean, Int> {
    override fun decode(databaseValue: Int): Boolean {
        return databaseValue != 0
    }

    override fun encode(value: Boolean): Int {
        return if (value) 1 else 0
    }
}
