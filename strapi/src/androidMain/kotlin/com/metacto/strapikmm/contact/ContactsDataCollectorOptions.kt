package com.metacto.strapikmm.contact

import android.content.Context
import androidx.activity.result.ActivityResultLauncher

actual class ContactsDataCollectorOptions(
    val context: Context,
    val launcher: ActivityResultLauncher<String>
) {
    var onResult: (Boolean) -> Unit = {}
}