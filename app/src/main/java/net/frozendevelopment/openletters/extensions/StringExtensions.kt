package net.frozendevelopment.openletters.extensions

fun String.sanitizeForSearch(): String {
    return replace(Regex("[-:()|\"\\[\\]{}*?+~,\\\\/]"), "*")
}
