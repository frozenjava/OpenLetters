package net.frozendevelopment.openletters

import android.content.Context
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import net.frozendevelopment.openletters.data.sqldelight.models.DocumentId
import net.frozendevelopment.openletters.util.DocumentManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File

@RunWith(RobolectricTestRunner::class)
class DocumentManagerTests {
    private lateinit var context: Context
    private lateinit var documentManager: DocumentManager

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        documentManager = DocumentManager(context)
    }

    @Test
    fun `persist and get document`() {
        val cacheFile = File.createTempFile("test", "tmp", context.cacheDir)
        cacheFile.writeText("test")
        val cacheUri = Uri.fromFile(cacheFile)
        val documentId = DocumentId("test")

        val persistedUri = documentManager.persist(cacheUri, documentId)

        assertNotNull(persistedUri)
        assertTrue(File(persistedUri.path!!).exists())
        assertEquals("test", File(persistedUri.path!!).readText())
    }

    @Test(expected = IllegalStateException::class)
    fun `persist without cache file`() {
        val cacheUri = Uri.parse("file:///dev/null/nonexistent.txt")
        val documentId = DocumentId("test")

        documentManager.persist(cacheUri, documentId)
    }

    @Test(expected = IllegalStateException::class)
    fun `persist with existing document`() {
        val cacheFile = File.createTempFile("test", "tmp", context.cacheDir)
        cacheFile.writeText("test")
        val cacheUri = Uri.fromFile(cacheFile)
        val documentId = DocumentId("test")

        documentManager.persist(cacheUri, documentId)
        documentManager.persist(cacheUri, documentId)
    }

    @Test
    fun `get returns existing document`() {
        val cacheFile = File.createTempFile("test", "tmp", context.cacheDir)
        cacheFile.writeText("test")
        val cacheUri = Uri.fromFile(cacheFile)
        val documentId = DocumentId("test")

        documentManager.persist(cacheUri, documentId)
        val persistedUri = documentManager.get(documentId)

        assertNotNull(persistedUri)
        assertEquals(persistedUri, Uri.fromFile(File(documentManager.documentDirectory, documentId.value)))
    }

    @Test
    fun `get returns null for non-existent document`() {
        val documentId = DocumentId("test")

        val persistedUri = documentManager.get(documentId)

        assertNull(persistedUri)
    }

    @Test
    fun `delete existing document`() {
        val cacheFile = File.createTempFile("test", "tmp", context.cacheDir)
        cacheFile.writeText("test")
        val cacheUri = Uri.fromFile(cacheFile)
        val documentId = DocumentId("test")

        documentManager.persist(cacheUri, documentId)
        assertTrue(File(documentManager.documentDirectory, documentId.value).exists())

        documentManager.delete(documentId)
        assertFalse(File(documentManager.documentDirectory, documentId.value).exists())
    }

    @Test
    fun `delete document that does not exist`() {
        val documentId = DocumentId("test")

        documentManager.delete(documentId)
        assertFalse(File(documentManager.documentDirectory, documentId.value).exists())
    }
}
