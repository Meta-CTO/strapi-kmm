package com.metacto.strapikmm.uploader

 import com.metacto.strapikmm.model.file.UploadFileRequest
 import com.metacto.strapikmm.repos.UploaderRepository
actual class S3UploadManager actual constructor(
    private val bucket: String,
    private val accessKey: String,
    private val secretKey: String,
    private val awsS3BaseUrl: String,
    actual override val uploaderRepository: UploaderRepository,
    private val context: Any?
) : UploadManager {
    actual override suspend fun performUpload(file: UploadableFile): UploadFileRequest {
         TODO("Not yet implemented")
     }
 }