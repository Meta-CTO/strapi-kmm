package com.metacto.strapikmm.uploader

import com.metacto.strapikmm.repos.UploaderRepository

expect class S3UploadManager constructor(
    bucket: String,
    accessKey: String,
    secretKey: String,
    awsS3BaseUrl: String,
    uploaderRepository: UploaderRepository,
    context: Any?
) : UploadManager {
    override val uploaderRepository: UploaderRepository
}
