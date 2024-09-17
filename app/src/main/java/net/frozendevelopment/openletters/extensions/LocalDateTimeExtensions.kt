package net.frozendevelopment.openletters.extensions

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

val LocalDateTime.dateTimeString: String
    get() {
        val dateTimeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a")
        return this.format(dateTimeFormatter)
    }

val LocalDateTime.dateString: String
    get() {
        val dateTimeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
        return this.format(dateTimeFormatter)
    }
