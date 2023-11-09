package com.swensonhe.strapikmm.repos

import com.swensonhe.strapikmm.datasource.network.services.strapi.StrapiService
import com.swensonhe.strapikmm.model.PagingResponse
import com.swensonhe.strapikmm.model.file.File
import com.swensonhe.strapikmm.model.file.UploadFileRequest
import com.swensonhe.strapikmm.model.file.UploadFiles

/**
 * Repository for file uploading operations.
 *
 * @property uploaderService The service for handling file upload API calls.
 */
class UploaderRepository(
    private val uploaderService: StrapiService
) {
    /**
     * Upload files to a custom uploader endpoint using the provided list of file upload requests.
     *
     * @param filesToUpload The list of file upload requests to be processed.
     * @return A list of uploaded [File] as a result of the operation.
     * @throws Throwable in case of exceptions during the operation.
     */
    @Throws(Throwable::class)
    suspend fun uploadFiles(filesToUpload: List<UploadFileRequest>) =
        // Upload the files to the custom uploader endpoint.
        uploaderService.post<PagingResponse<File>> {
            endpoint("/custom-uploader")
            body(UploadFiles(filesToUpload))
            strapiQueryBuilder {
                populate("*")
            }
        }.data
}
