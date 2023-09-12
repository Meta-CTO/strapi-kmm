package com.swensonhe.strapikmm.uploader

import com.swensonhe.strapikmm.repos.UploaderRepository

expect class S3UploadManager(
    bucket: String,
    accessKey: String,
    secretKey: String,
    awsS3BaseUrl: String,
    context: Any?,
    uploaderRepository: UploaderRepository
) : IUploadManager