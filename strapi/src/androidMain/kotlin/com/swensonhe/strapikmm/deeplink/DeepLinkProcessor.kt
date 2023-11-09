package com.swensonhe.strapikmm.deeplink

import android.net.Uri
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

/** Android platform-specific implementation of a deep link processor that processes deep links. */

actual object DeepLinkProcessor {
    /**
     * Processes the provided deep link URL and returns the result asynchronously.
     *
     * @param url The deep link URL to process.
     * @return A [Result] containing the processed deep link as a [String], or an exception if processing fails.
     * @throws Throwable if there is an error during the deep link processing.
     */
    actual suspend fun process(url: String) = suspendCancellableCoroutine { cont ->
        // Use Firebase Dynamic Links to process the URL and get the deep link.
        Firebase.dynamicLinks.getDynamicLink(Uri.parse(url))
            .addOnSuccessListener {
                // Get deep link from result (may be null if no link is found)
                val deepLink = it?.link
                deepLink?.let {
                    cont.resumeWith(Result.success(it.toString()))
                }
            }.addOnFailureListener {
                // An error occurred while processing the deep link.
                cont.resumeWithException(it)
            }
    }
}