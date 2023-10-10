package com.swensonhe.strapikmm.uploader

import com.swensonhe.strapikmm.model.file.File
import com.swensonhe.strapikmm.model.file.UploadFileRequest
import com.swensonhe.strapikmm.repos.UploaderRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

interface IUploadManager {
    val uploaderRepository: UploaderRepository

    /**
     * Uploads a single file.
     *
     * @param file The file to be uploaded.
     * @return The uploaded file.
     * @throws Throwable if an error occurs during the upload process.
     */
    @Throws(Throwable::class)
    suspend fun upload(file: UploadableFile): File {
        val uploadRequest = performUpload(file)
        return uploadToStrapi(listOf(uploadRequest)).first()
    }

    /**
     * Uploads multiple files in parallel.
     *
     * AWS S3 does not support uploading multiple files at once, so this function uploads them one by one and returns a list of files.
     * The use of 'async' ensures parallel execution, and 'awaitAll' ensures waiting for all uploads to complete.
     *
     * @param files The list of files to be uploaded.
     * @return The list of uploaded files.
     * @throws Throwable if an error occurs during the upload process.
     */
    @Throws(Throwable::class)
    suspend fun upload(files: List<UploadableFile>): List<File> = coroutineScope {
        val deferredFiles = files.map { file ->
            async {
                performUpload(file)
            }
        }

        val filesRequests = deferredFiles.awaitAll()
        return@coroutineScope uploadToStrapi(filesRequests)
    }

    /**
     * Performs the actual upload of a file.
     *
     * @param file The file to be uploaded.
     * @return The request object representing the uploaded file.
     * @throws Throwable if an error occurs during the upload process.
     */
    @Throws(Throwable::class)
    suspend fun performUpload(file: UploadableFile): UploadFileRequest

    /**
     * Uploads a list of files to the Strapi CMS using the UploaderRepository.
     *
     * @param files The list of file requests to be uploaded.
     * @return The list of uploaded files.
     * @throws Throwable if an error occurs during the upload process.
     */
    @Throws(Throwable::class)
    private suspend fun uploadToStrapi(files: List<UploadFileRequest>): List<File> {
        return uploaderRepository.uploadFiles(files)
    }
}
