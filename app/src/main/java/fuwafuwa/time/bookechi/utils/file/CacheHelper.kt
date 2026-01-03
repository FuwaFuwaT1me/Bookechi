package fuwafuwa.time.bookechi.utils.file

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class CacheHelper(private val context: Context) {

    private val cacheDir: File
        get() = File(context.cacheDir, IMAGE_CACHE_DIR).apply {
            if (!exists()) mkdirs()
        }

    suspend fun cacheImage(uri: Uri, quality: Int = 85): File? = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return@withContext null

            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            bitmap?.let { cacheBitmap(it, quality) }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private suspend fun cacheBitmap(bitmap: Bitmap, quality: Int = 85): File? = withContext(Dispatchers.IO) {
        try {
            val fileName = "${UUID.randomUUID()}.jpg"
            val cachedFile = File(cacheDir, fileName)

            FileOutputStream(cachedFile).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            }

            cachedFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getCachedImage(fileName: String): File? {
        val file = File(cacheDir, fileName)
        return if (file.exists()) file else null
    }

    fun deleteCachedImage(fileName: String): Boolean {
        return File(cacheDir, fileName).delete()
    }

    fun clearImageCache() {
        cacheDir.listFiles()?.forEach { it.delete() }
    }

    fun getCacheSize(): Long {
        return cacheDir.listFiles()?.sumOf { it.length() } ?: 0L
    }

    companion object {

        private const val IMAGE_CACHE_DIR = "image_cache"
    }
}
