package com.swensonhe.strapikmm.uploader

import com.swensonhe.strapikmm.model.file.File
import com.swensonhe.strapikmm.repos.UploaderRepository
actual class S3UploadManager actual constructor(
    bucket: String,
    accessKey: String,
    secretKey: String,
    awsS3BaseUrl: String,
    context: Any?,
    uploaderRepository: UploaderRepository
) : IUploadManager {
    override suspend fun upload(file: UploadableFile): File {
        throw NotImplementedError()
    }
}