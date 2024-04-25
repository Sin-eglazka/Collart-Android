package com.example.collart.Tools.FileConverter

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class FileConverter {
    companion object {
        fun getImagePathFromInputStreamUri(uri: Uri, context: Context, filename: String): String? {
            var inputStream: InputStream? = null
            var filePath: String? = null
            if (uri.authority != null) {
                try {
                    inputStream = context.contentResolver.openInputStream(uri)
                    val photoFile = createTemporalFileFrom(inputStream, context, filename)
                    filePath = photoFile!!.path
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                } finally {
                    try {
                        inputStream!!.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            return filePath
        }

        @Throws(IOException::class)
        private fun createTemporalFileFrom(
            inputStream: InputStream?,
            context: Context,
            filename: String
        ): File? {
            var targetFile: File? = null
            if (inputStream != null) {
                var read: Int
                val buffer = ByteArray(8 * 1024)
                targetFile = createTemporalFile(filename, context)
                val outputStream: OutputStream = FileOutputStream(targetFile)
                while (inputStream.read(buffer).also { read = it } != -1) {
                    outputStream.write(buffer, 0, read)
                }
                outputStream.flush()
                try {
                    outputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return targetFile
        }

        private fun createTemporalFile(filename: String, context: Context): File {
            return File(context.externalCacheDir, filename)
        }
    }
}