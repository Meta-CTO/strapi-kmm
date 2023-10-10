package com.swensonhe.strapikmm.uploader

 import com.swensonhe.strapikmm.model.file.UploadFileRequest
 import com.swensonhe.strapikmm.repos.UploaderRepository
actual class S3UploadManager actual constructor(
    private val bucket: String,
    private val accessKey: String,
    private val secretKey: String,
    private val awsS3BaseUrl: String,
    override val uploaderRepository: UploaderRepository,
    private val context: Any?
) : IUploadManager {
     override suspend fun performUpload(file: UploadableFile): UploadFileRequest {
         throw NotImplementedError()
     }
 }