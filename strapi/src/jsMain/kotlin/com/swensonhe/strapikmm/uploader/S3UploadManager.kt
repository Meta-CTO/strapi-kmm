package com.swensonhe.strapikmm.uploader

 import com.swensonhe.strapikmm.model.file.File
 import com.swensonhe.strapikmm.repos.UploaderRepository
 actual class S3UploadManager constructor(
     actual val bucket: String,
     actual val accessKey: String,
     actual val secretKey: String,
     actual val awsS3BaseUrl: String,
     actual val uploaderRepository: UploaderRepository
 ) : IUploadManager {
     override suspend fun upload(file: UploadableFile): File {
         throw NotImplementedError()
     }
 }