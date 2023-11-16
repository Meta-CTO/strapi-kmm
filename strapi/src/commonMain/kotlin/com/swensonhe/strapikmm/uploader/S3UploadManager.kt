package com.swensonhe.strapikmm.uploader

import com.swensonhe.strapikmm.repos.UploaderRepository

expect class S3UploadManager constructor(
    bucket: String,
    accessKey: String,
    secretKey: String,
    awsS3BaseUrl: String,
    uploaderRepository: UploaderRepository,
    context: Any?
) : UploadManager
