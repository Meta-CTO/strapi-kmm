@file:OptIn(ExperimentalCoroutinesApi::class)

 package com.swensonhe.strapikmm.uploader

 import android.content.Context
 import android.webkit.MimeTypeMap
 import com.amazonaws.ClientConfiguration
 import com.amazonaws.auth.BasicAWSCredentials
 import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
 import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
 import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
 import com.amazonaws.regions.Region
 import com.amazonaws.regions.Regions
 import com.amazonaws.services.s3.AmazonS3Client
 import com.amazonaws.services.s3.model.CannedAccessControlList
 import com.amazonaws.services.s3.model.ObjectMetadata
 import com.swensonhe.strapikmm.model.file.File
 import com.swensonhe.strapikmm.model.file.UploadFileRequest
 import com.swensonhe.strapikmm.repos.UploaderRepository
 import com.swensonhe.strapikmm.util.files.FileUtilsImpl
 import com.swensonhe.strapikmm.util.files.getMimeType
 import com.swensonhe.strapikmm.util.files.sizeInMb
 import com.swensonhe.strapikmm.util.nullIfEmpty
 import kotlinx.coroutines.ExperimentalCoroutinesApi
 import kotlinx.coroutines.suspendCancellableCoroutine
 import java.util.UUID
 import java.util.concurrent.TimeUnit
 import kotlin.coroutines.resumeWithException

 actual class S3UploadManager constructor(
     actual val bucket: String,
     actual val accessKey: String,
     actual val secretKey: String,
     actual val awsS3BaseUrl: String,
     private val context: Context?,
     actual val uploaderRepository: UploaderRepository
 ) : IUploadManager {
     private val fileUtils by lazy { FileUtilsImpl(context as Context) }

     private val transferUtility by lazy {
         createTransferUtility()
     }

     init {
         if (context == null || context !is Context) {
             throw IllegalArgumentException("Context must be provided, and must be an Android Context")
         }
     }

     override suspend fun upload(file: UploadableFile): File {
         val currentFile =
             file.file ?: file.uri?.let { fileUtils.createCopyAndReturnRealFile(file.uri) }
             ?: throw IllegalArgumentException("File or Uri must be provided")
         val fileExtension =
             currentFile.extension.nullIfEmpty() ?: file.uri?.path?.split(".")?.last()?.nullIfEmpty()
             ?: MimeTypeMap.getSingleton()
                 .getExtensionFromMimeType(file.uri?.getMimeType(context as Context).orEmpty())
             ?: "jpg"
         val fileName = "${UUID.randomUUID()}.$fileExtension"
         suspendCancellableCoroutine { cont ->
             uploadToS3(
                 file = currentFile,
                 fileName = fileName,
                 onUploadCompleted = {
                     if (cont.isActive) cont.resume(Unit, null)
                 },
                 onUploadFailed = {
                     if (cont.isActive) cont.resumeWithException(it)
                 }
             )
         }

         val fileNameWithExtension = if (currentFile.name != "null") {
             if (currentFile.name.split(".").size > 1) {
                 currentFile.name
             } else {
                 "${currentFile.name}.$fileExtension"
             }
         } else {
             fileName
         }

         return uploadToStrapi(
             fileSize = currentFile.sizeInMb,
             fileUUID = fileName,
             fileName = fileNameWithExtension,
             fileExtension = fileExtension,
             fileMimeType = file.mimeType ?: currentFile.getMimeType()
         )
     }

     private fun uploadToS3(
         file: java.io.File,
         fileName: String,
         onUploadCompleted: (() -> Unit)?,
         onUploadFailed: ((Throwable) -> Unit)?
     ) {
         val observer = transferUtility.upload(
             bucket,
             fileName,
             file,
             ObjectMetadata(),
             CannedAccessControlList.PublicRead
         )

         observer.setTransferListener(object : TransferListener {
             override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                 // Not Used
             }

             override fun onStateChanged(id: Int, state: TransferState?) {
                 when (state) {
                     TransferState.COMPLETED -> onUploadCompleted?.invoke()
                     TransferState.FAILED -> onUploadFailed?.invoke(Throwable("Upload failed"))
                     TransferState.WAITING_FOR_NETWORK -> onUploadFailed?.invoke(Throwable("Upload failed due to network issue"))
                     else -> {
                         // do nothing, let's add more cases later
                     }
                 }
             }

             override fun onError(id: Int, ex: Exception?) {
                 onUploadFailed?.invoke(Throwable(ex))
             }
         })
     }

     private fun createTransferUtility(): TransferUtility {
         val clientConfig = ClientConfiguration()
         clientConfig.socketTimeout = TimeUnit.MINUTES.toMillis(2).toInt()
         clientConfig.connectionTimeout = TimeUnit.MINUTES.toMillis(2).toInt()
         clientConfig.maxErrorRetry = 2

         val credentials = BasicAWSCredentials(accessKey, secretKey)
         val s3Client =
             AmazonS3Client(credentials, Region.getRegion(Regions.US_WEST_2), clientConfig)
         return TransferUtility.builder()
             .s3Client(s3Client)
             .context(context as Context)
             .build()
     }

     @Throws(Throwable::class)
     private suspend fun uploadToStrapi(
         fileSize: Double,
         fileUUID: String,
         fileName: String,
         fileExtension: String,
         fileMimeType: String
     ): File {
         val fileUrl = "$awsS3BaseUrl/$fileUUID"
         val fileRequest = UploadFileRequest(
             name = fileName,
             mime = fileMimeType,
             ext = fileExtension,
             size = fileSize.toString(),
             url = fileUrl
         )

         return uploaderRepository.uploadFiles(listOf(fileRequest))[0]
     }
 }