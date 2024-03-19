package com.metaCTO.strapikmm.repos

import com.metaCTO.strapikmm.datasource.network.services.strapi.StrapiService
import com.metaCTO.strapikmm.model.PagingResponse
import com.metaCTO.strapikmm.model.file.File
import com.metaCTO.strapikmm.model.file.UploadFileRequest
import com.metaCTO.strapikmm.model.file.UploadFiles

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
