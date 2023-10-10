package com.swensonhe.strapikmm.uploader

 import com.swensonhe.strapikmm.repos.UploaderRepository

 expect class S3UploadManager : IUploadManager{
     val bucket: String
     val accessKey: String
     val secretKey: String
     val awsS3BaseUrl: String
     val uploaderRepository: UploaderRepository
 }
