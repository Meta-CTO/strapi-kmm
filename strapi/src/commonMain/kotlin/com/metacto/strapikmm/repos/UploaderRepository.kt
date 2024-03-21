package com.metacto.strapikmm.repos

import com.metacto.strapikmm.datasource.network.services.strapi.StrapiService
import com.metacto.strapikmm.model.PagingResponse
import com.metacto.strapikmm.model.file.File
import com.metacto.strapikmm.model.file.UploadFileRequest
import com.metacto.strapikmm.model.file.UploadFiles

class UploaderRepository(
    private val uploaderService: StrapiService
) {
    @Throws(Throwable::class)
    suspend fun uploadFiles(filesToUpload: List<UploadFileRequest>) =
        uploaderService.post<PagingResponse<File>> {
            endpoint("/custom-uploader")
            body(UploadFiles(filesToUpload))
            strapiQueryBuilder {
                populate("*")
            }
        }.data.orEmpty()
}
