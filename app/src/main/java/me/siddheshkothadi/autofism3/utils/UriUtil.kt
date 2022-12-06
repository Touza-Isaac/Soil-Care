package me.siddheshkothadi.autofism3.utils

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap

fun Uri.getFileExtension(context: Context): String? {
    val fileType: String? = context.contentResolver.getType(this)
    return MimeTypeMap.getSingleton().getExtensionFromMimeType(fileType)
}