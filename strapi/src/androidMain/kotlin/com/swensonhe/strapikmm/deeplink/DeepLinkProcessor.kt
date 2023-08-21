package com.swensonhe.strapikmm.deeplink

import android.net.Uri
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

actual object DeepLinkProcessor {
    actual suspend fun process(url: String) = suspendCancellableCoroutine { cont ->
        Firebase.dynamicLinks.getDynamicLink(Uri.parse(url))
            .addOnSuccessListener {
                // Get deep link from result (may be null if no link is found)
                val deepLink = it?.link
                deepLink?.let {
                    cont.resumeWith(Result.success(it.toString()))
                }
            }.addOnFailureListener {
                cont.resumeWithException(it)
            }
    }
}