//package com.swensonhe.strapikmm.util
//
//import android.content.Context
//import android.content.res.AssetManager
//import dev.icerock.moko.resources.AssetResource
//import io.michaelrocks.libphonenumber.kotlin.MetadataLoader
//import io.michaelrocks.libphonenumber.kotlin.PhoneNumberUtil
//import io.michaelrocks.libphonenumber.kotlin.PhoneNumberUtil.Companion.createInstance
//import java.io.IOException
//import java.io.InputStream
//
//
//internal actual class PhoneNumberUtilInstance actual constructor(context: Any?) {
//    init {
//        if (context == null || context !is android.content.Context) {
//            throw IllegalArgumentException("Context must be provided, and must be an Android Context")
//        }
//    }
//    actual val util: PhoneNumberUtil = createInstance(AssetsMetadataLoader((context as Context).assets))
//}
//
//class AssetsMetadataLoader(private val assetManager: AssetManager) : MetadataLoader {
//    override fun loadMetadata(phoneMetadataResource: AssetResource): InputStream? {
//        return try {
//            assetManager.open(phoneMetadataResource.path)
//        } catch (exception: IOException) {
//            null
//        }
//    }
//}