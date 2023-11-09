package com.swensonhe.strapikmm.contact

import android.content.Context
import androidx.activity.result.ActivityResultLauncher

/**
 * A class that holds options for configuring the [ContactsDataCollector] class for collecting contact data.
 *
 * @property context The Android [Context] used to access the content resolver and other resources.
 * @property launcher An [ActivityResultLauncher] for launching the contact picker activity.
 */
actual class ContactsDataCollectorOptions(
    val context: Context,
    val launcher: ActivityResultLauncher<String>
) {
    /**
     * A callback function that will be invoked when contact permissions check is Completed.
     * The function takes a [Boolean] parameter indicating whether the permission is granted or not.
     */
    var onResult: (Boolean) -> Unit = {}
}