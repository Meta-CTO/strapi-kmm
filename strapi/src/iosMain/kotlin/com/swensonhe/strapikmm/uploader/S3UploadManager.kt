package com.swensonhe.strapikmm.uploader

import com.swensonhe.strapikmm.model.file.File
import com.swensonhe.strapikmm.repos.UploaderRepository
import cocoapods.AWSS3.AWSS3TransferUtility
import cocoapods.AWSS3.AWSS3TransferUtilityTask
import cocoapods.AWSS3.AWSS3TransferUtilityUploadExpression
import com.swensonhe.strapikmm.model.file.UploadFileRequest
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

actual class S3UploadManager actual constructor(
    private val bucket: String,
    private val accessKey: String,
    private val secretKey: String,
    private val awsS3BaseUrl: String,
    private val context: Any?,
    private val uploaderRepository: UploaderRepository
) : IUploadManager {

    init {
        init()
    }

    fun init() {
        // TODO: Handle the configuration
    }

    override suspend fun upload(file: UploadableFile): File {
        // TODO: Implement the upload and verify that following code works for iOS
//        val expression = AWSS3TransferUtilityUploadExpression()
//        expression.setValue("public-read",  "x-amz-acl")
//
//        suspendCancellableCoroutine<AWSS3TransferUtilityTask> {
//            AWSS3TransferUtility.defaultS3TransferUtility().uploadData(
//                file.data,
//                bucket = bucket,
//                key = file.fileName,
//                contentType = file.contentType,
//                expression = expression,
//                completionHandler = { task, error ->
//                    if (error != null) {
//                        it.resumeWithException(Throwable(error.localizedDescription))
//                    } else if (task != null){
//                        it.resumeWith(Result.success(task))
//                    }
//                }
//            ).continueWithBlock {
//                return@continueWithBlock null
//            }
//        }
//
//        return uploadToStrapi(file)
    }

    @Throws(Throwable::class)
    private suspend fun uploadToStrapi(
        file: UploadableFile
    ): File {
       val fileSizeInMb = file.data.length.toFloat() / 1024f / 1024f
        val fileUrl = "$awsS3BaseUrl/${file.fileName}"
        val fileRequest = UploadFileRequest(
            name = file.fileName,
            mime = file.contentType,
            ext = file.extension,
            size = fileSizeInMb.toString(),
            url = fileUrl
        )

        return uploaderRepository.uploadFiles(listOf(fileRequest))[0]
    }
}