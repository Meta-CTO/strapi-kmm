package com.swensonhe.strapikmm.repos

import com.swensonhe.strapikmm.datasource.network.services.strapi.StrapiService
import com.swensonhe.strapikmm.model.PagingResponse
import com.swensonhe.strapikmm.model.file.File
import com.swensonhe.strapikmm.model.file.UploadFileRequest
import com.swensonhe.strapikmm.model.file.UploadFiles

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
