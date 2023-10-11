package com.swensonhe.strapikmm.uploader

 import com.swensonhe.strapikmm.repos.UploaderRepository
 import com.swensonhe.strapikmm.model.file.UploadFileRequest

actual class S3UploadManager actual constructor(
    private val bucket: String,
    private val accessKey: String,
    private val secretKey: String,
    private val awsS3BaseUrl: String,
    override val uploaderRepository: UploaderRepository,
    private val context: Any?
) : UploadManager {

     init {
         init()
     }

     fun init() {
         // TODO: Handle the configuration
     }

     override suspend fun performUpload(file: UploadableFile): UploadFileRequest {
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

            throw NotImplementedError()
     }
 }