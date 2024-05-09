package com.metacto.strapikmm.uploader

 import platform.Foundation.NSData

 actual class UploadableFile(
     val fileName: String,
     val data: NSData,
     val contentType: String,
     val extension: String
 )