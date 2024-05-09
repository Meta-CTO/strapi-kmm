package com.metacto.strapikmm.util.files

 import android.content.ContentResolver
 import android.content.Context
 import android.net.Uri
 import android.provider.OpenableColumns
 import kotlinx.coroutines.Dispatchers
 import kotlinx.coroutines.withContext
 import java.io.File
 import java.io.FileOutputStream
 import java.io.OutputStream

 const val UNKNOWN_FILE_SIZE = -1

 interface FileUtils {
     fun createCopyAndReturnRealFile(uri: Uri): File
     fun getContentSchemeSize(uri: Uri): Int
     fun getContentSchemeFileName(uri: Uri): String?
     suspend fun copyToFolder(folderUri: Uri, fileUri: Uri)
 }

 class FileUtilsImpl constructor(
     private val context: Context
 ) : FileUtils {

     override fun createCopyAndReturnRealFile(uri: Uri): File {
         val resolver: ContentResolver = context.contentResolver
         val filePath = "${context.cacheDir}${File.separator}${getContentSchemeFileName(uri)}"
         val file = File(filePath)
         resolver.openInputStream(uri)?.use { inputStream ->
             val outputStream: OutputStream = FileOutputStream(file)
             val buf = ByteArray(1024)
             var len: Int
             while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
             outputStream.close()
             inputStream.close()
         }
         return file
     }

     override fun getContentSchemeSize(uri: Uri): Int {
         return context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
             if (!cursor.moveToFirst()) return@use null
             val size = cursor.getColumnIndex(OpenableColumns.SIZE)
             cursor.getInt(size)
         } ?: UNKNOWN_FILE_SIZE
     }

     override fun getContentSchemeFileName(uri: Uri): String? {
         return context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
             if (!cursor.moveToFirst()) return@use null
             val fileName = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
             cursor.getString(fileName)
         }
     }

     override suspend fun copyToFolder(folderUri: Uri, fileUri: Uri): Unit = withContext(Dispatchers.IO) {
         val file = folderUri.let { context.contentResolver.openFileDescriptor(it, "w") }
         file?.let { parcelFile ->
             val fileOutputStream = FileOutputStream(parcelFile.fileDescriptor)
             context.contentResolver.openInputStream(fileUri)?.let {
                 val buf = ByteArray(1024)
                 var len: Int
                 while (it.read(buf).also { len = it } > 0) fileOutputStream.write(buf, 0, len)
                 fileOutputStream.close()
                 it.close()
                 parcelFile.close()
             }
         }
     }
 }