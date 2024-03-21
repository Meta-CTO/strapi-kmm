package com.metaCTO.strapikmm.uploader

 import com.metaCTO.strapikmm.model.file.UploadFileRequest
 import com.metaCTO.strapikmm.repos.UploaderRepository
actual class S3UploadManager actual constructor(
    private val bucket: String,
    private val accessKey: String,
    private val secretKey: String,
    private val awsS3BaseUrl: String,
    override val uploaderRepository: UploaderRepository,
    private val context: Any?
) : UploadManager {
     override suspend fun performUpload(file: UploadableFile): UploadFileRequest {
         TODO("Not yet implemented")
     }
 }