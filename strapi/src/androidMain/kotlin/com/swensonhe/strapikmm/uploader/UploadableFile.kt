package com.swensonhe.strapikmm.uploader

import android.net.Uri
import java.io.File

actual class UploadableFile(
    val fileName: String,
    val mimeType: String? = null,
    val file: File? = null,
    val uri: Uri? = null
)