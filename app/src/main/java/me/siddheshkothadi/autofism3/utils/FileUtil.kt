package me.siddheshkothadi.autofism3.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.*
import java.net.URLEncoder

object FileUtil {
    @Throws(IOException::class)
    fun copy(source: InputStream, target: OutputStream) {
        val buf = ByteArray(8192)
        var length: Int
        while (source.read(buf).also { length = it } > 0) {
            target.write(buf, 0, length)
        }
    }

    fun getFile(context: Context, fileName: String): File {
        return File(context.filesDir, fileName)
    }

    fun generateUri(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            context.packageName + ".provider",
            file
        )
    }

    fun generateUri(context: Context, fileName: String): Uri {
        val file = getFile(context, fileName)
        return generateUri(context, file)
    }

    fun encodedUri(uri: Uri): String {
        return URLEncoder.encode(uri.toString(), "utf-8")
    }

    fun getOutputStream(file: File): FileOutputStream {
        return FileOutputStream(file)
    }

    fun getOutputStream(context: Context, fileName: String): FileOutputStream {
        val file = getFile(context, fileName)
        return FileOutputStream(file)
    }

    fun getInputStream(context: Context, uri: Uri): InputStream? {
        return context.contentResolver.openInputStream(uri)
    }

    fun createNewFile(context: Context, fileName: String): File {
        val file = getFile(context, fileName)
        file.createNewFile()
        return file
    }
}