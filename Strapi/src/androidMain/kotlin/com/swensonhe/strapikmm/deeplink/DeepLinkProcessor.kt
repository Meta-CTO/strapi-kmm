package com.swensonhe.strapikmm.deeplink

import android.net.Uri
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase

actual object DeepLinkProcessor {
    actual fun process(url: String, onProcessed: (String) -> Unit) {
        Firebase.dynamicLinks.getDynamicLink(Uri.parse(url))
            .addOnSuccessListener {
                // Get deep link from result (may be null if no link is found)
                val deepLink = it?.link
                deepLink?.let {
                    onProcessed.invoke(it.toString())
                }
            }.addOnFailureListener {
                println("getDynamicLink:onFailure $it")
            }
    }
}