package com.swensonhe.strapikmm.util.files

 import android.content.Context
 import android.net.Uri
 import android.provider.MediaStore
 import android.webkit.MimeTypeMap
 import androidx.core.net.toFile
 import java.io.File

 fun File.getMimeType(): String {
     var type: String? = null
     val extension = MimeTypeMap.getFileExtensionFromUrl(absolutePath)
     if (extension != null) {
         type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
     }

     return type ?: "*/*"
 }
 fun File.getMimeType(fileExtension: String? = null): String {
     var type: String? = null
     val extension = fileExtension ?: MimeTypeMap.getFileExtensionFromUrl(absolutePath)
     if (extension != null) {
         type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
     }

     return type ?: "*/*"
 }

 fun Double.round(decimals: Int = 2): Double = "%.${decimals}f".format(this).toDouble()

 fun Uri.addUriParameter(key: String, newValue: String): Uri {
     val params = queryParameterNames
     val newUri = buildUpon().clearQuery()
     var isSameParamPresent = false
     for (param in params) {
         // if same param is present override it, otherwise add the old param back
         newUri.appendQueryParameter(
             param,
             if (param == key) newValue else getQueryParameter(param)
         )
         if (param == key) {
             // make sure we do not add new param again if already overridden
             isSameParamPresent = true
         }
     }
     if (!isSameParamPresent) {
         // never overrode same param so add new passed value now
         newUri.appendQueryParameter(
             key,
             newValue
         )
     }
     return newUri.build()
 }

 val File.size get() = if (!exists()) 0.0 else length().toDouble()
 val File.sizeInKb get() = size / 1024
 val File.sizeInMb get() = sizeInKb / 1024
 val File.sizeInGb get() = sizeInMb / 1024
 val File.sizeInTb get() = sizeInGb / 1024

 fun Uri.asFile(): File = File(toString())

 fun String?.asUri(): Uri? {
     try {
         return Uri.parse(this)
     } catch (e: Exception) {
     }
     return null
 }

 val File?.exists get() = this?.exists() ?: false

 fun Uri.getMimeType(context: Context): String? {
     var mimeType: String? = null
     when (scheme) {
         "file" -> {
             mimeType = this.toFile().getMimeType()
         }

         "content" -> {
             val cursor =
                 context.contentResolver.query(this, arrayOf(MediaStore.Images.Media.MIME_TYPE), null, null, null)
             if (cursor != null && cursor.count != 0) {
                 cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE).takeIf { it != -1 }?.let {
                     cursor.moveToFirst()
                     mimeType = cursor.getString(it)
                 }
             }
             cursor?.close()
         }
     }

     return mimeType
 }
