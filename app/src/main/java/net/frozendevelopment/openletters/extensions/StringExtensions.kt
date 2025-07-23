package net.frozendevelopment.openletters.extensions

fun String.sanitizeForSearch(): String = replace(Regex("[-:()|\"\\[\\]{}*?+~,\\\\/]"), "*")
