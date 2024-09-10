package net.frozendevelopment.mailshare.data.mock

import androidx.annotation.VisibleForTesting
import net.frozendevelopment.mailshare.data.sqldelight.migrations.Letter
import net.frozendevelopment.mailshare.data.sqldelight.models.LetterId

@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
fun mockLetter(
    id: LetterId = LetterId.random(),
    sender: String? = """
        Jane Doe
        1234 Street St.
        City, State 12345
    """.trimIndent(),
    recipient: String? = """
        John Doe
        1234 Street St.
        City, State 12345
        Phone: 123-456-7890
    """.trimIndent(),
    body: String = """
        We've been trying to reach you about your cars extended warranty. Please call us a 867-5309.
        We've been trying to reach you about your cars extended warranty. Please call us a 867-5309.
        We've been trying to reach you about your cars extended warranty. Please call us a 867-5309.
        We've been trying to reach you about your cars extended warranty. Please call us a 867-5309.
        We've been trying to reach you about your cars extended warranty. Please call us a 867-5309.
        We've been trying to reach you about your cars extended warranty. Please call us a 867-5309.
    """.trimIndent(),
    created: Long = 0,
    lastModified: Long = 0,
) = Letter(
    id = id,
    sender = sender,
    recipient = recipient,
    body = body,
    created = created,
    lastModified = lastModified,
)
