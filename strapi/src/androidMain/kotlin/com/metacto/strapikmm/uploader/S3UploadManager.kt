@file:OptIn(ExperimentalCoroutinesApi::class)

package com.metacto.strapikmm.uploader

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
import com.metacto.strapikmm.errorhandling.NetworkErrorMapper
import com.metacto.strapikmm.model.file.UploadFileRequest
import com.metacto.strapikmm.repos.UploaderRepository
import com.metacto.strapikmm.util.files.FileUtilsImpl
import com.metacto.strapikmm.util.files.getMimeType
import com.metacto.strapikmm.util.files.sizeInMb
import com.metacto.strapikmm.util.nullIfEmpty
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resumeWithException

/**
 * Implementation of [UploadManager] for uploading files to AWS S3.
 *
 * @param bucket The name of the AWS S3 bucket.
 * @param accessKey The AWS access key.
 * @param secretKey The AWS secret key.
 * @param awsS3BaseUrl The base URL for AWS S3.
 * @param uploaderRepository The repository for uploading files.
 * @param context The Android context used for certain operations.
 */
actual class S3UploadManager actual constructor(
    private val bucket: String,
    private val accessKey: String,
    private val secretKey: String,
    private val awsS3BaseUrl: String,
    override val uploaderRepository: UploaderRepository,
    private val context: Any?
) : UploadManager {
    private val fileUtils by lazy { FileUtilsImpl(context as Context) }

    private val transferUtility by lazy {
        createTransferUtility()
    }

    /**
     * Validates that a valid Android Context is provided.
     * An [IllegalArgumentException] is thrown if the context is null or not an instance of Android [Context].
     */
    init {
        if (context == null || context !is Context) {
            throw NetworkErrorMapper.mapToAppException(
                "Context must be provided, and must be an Android Context",
                -1
            )
        }
    }

    /**
     * Uploads a file to AWS S3 and returns the corresponding [UploadFileRequest].
     *
     * @param file The file to be uploaded.
     * @return The [UploadFileRequest] representing the uploaded file.
     * @throws IllegalArgumentException if the file or URI is not provided.
     * @throws Throwable if an error occurs during the upload process.
     */
    override suspend fun performUpload(file: UploadableFile): UploadFileRequest {
        val currentFile =
            file.file ?: file.uri?.let { fileUtils.createCopyAndReturnRealFile(file.uri) }
            ?: throw NetworkErrorMapper.mapToAppException(
                "File or Uri must be provided",
                -1
            )
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

        return UploadFileRequest(
            name = fileNameWithExtension,
            mime = file.mimeType ?: currentFile.getMimeType(),
            ext = fileExtension,
            size = currentFile.sizeInMb.toString(),
            url = "$awsS3BaseUrl/$fileName"
        )
    }

    /**
     * Uploads a file to AWS S3.
     *
     * @param file The file to be uploaded.
     * @param fileName The name of the file in AWS S3.
     * @param onUploadCompleted Callback when the upload is completed.
     * @param onUploadFailed Callback when the upload fails.
     */
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

    /**
     * Creates a [TransferUtility] instance for AWS S3 uploads.
     *
     * @return The [TransferUtility] instance.
     */
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


}