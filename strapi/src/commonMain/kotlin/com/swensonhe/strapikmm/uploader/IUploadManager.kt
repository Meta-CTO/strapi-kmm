package com.swensonhe.strapikmm.uploader

 import com.swensonhe.strapikmm.model.file.File

 interface IUploadManager {
     @Throws(Throwable::class)
     suspend fun upload(file: UploadableFile): File
 }
