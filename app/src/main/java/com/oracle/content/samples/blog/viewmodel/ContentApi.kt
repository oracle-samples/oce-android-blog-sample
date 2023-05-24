/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
 package com.oracle.content.samples.blog.viewmodel

import android.content.Context
import androidx.preference.PreferenceManager
import com.oracle.content.samples.blog.ui.CHANNEL_TOKEN_PREF_KEY
import com.oracle.content.samples.blog.ui.ENABLE_CACHE_PREF_KEY
import com.oracle.content.samples.blog.ui.ENABLE_LOGGING_PREF_KEY
import com.oracle.content.samples.blog.ui.SERVER_URL_PREF_KEY
import com.oracle.content.samples.supremo.BuildConfig
import com.oracle.content.sdk.ContentDeliveryClient
import com.oracle.content.sdk.ContentLogging
import com.oracle.content.sdk.ContentSDK
import com.oracle.content.sdk.ContentSettings
import com.oracle.content.sdk.model.date.ContentDate
import com.oracle.content.sdk.model.date.ContentDateDisplayType
import com.oracle.content.sdk.model.digital.DigitalAsset
import com.oracle.content.sdk.model.item.ContentItem


// encapsulates the content delivery client creation using preferences from the settings screen
object ContentApi {

        // delivery client
    lateinit var deliveryClient: ContentDeliveryClient
    lateinit var appContext : Context;

    // first initialization
    fun init(context : Context) {
        appContext = context;
        createDeliveryClient()
    }

    // creates the delivery client based on preferences (can be called again if settings change)
    fun createDeliveryClient() {

        // preferences for cache and logging
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext)
        val cacheEnabled = sharedPreferences.getBoolean(ENABLE_CACHE_PREF_KEY, true)
        val loggingEnabled = sharedPreferences.getBoolean(ENABLE_LOGGING_PREF_KEY, true)

        val serverUrl = sharedPreferences.getString(SERVER_URL_PREF_KEY, BuildConfig.SERVER_URL)
        val channelToken = sharedPreferences.getString(CHANNEL_TOKEN_PREF_KEY, BuildConfig.CHANNEL_TOKEN)

        // set global logging level prior to creating delivery client
        ContentSDK.setLogLevel(if (loggingEnabled) ContentLogging.LogLevel.HTTP else ContentLogging.LogLevel.NONE)

        val settings = ContentSettings()
        if (cacheEnabled) {
            // enable caches, using default settings
            settings.enableCache(appContext.cacheDir)
        }

        // SDK: create client API we'll use to make calls
        deliveryClient =
                ContentSDK.createDeliveryClient(
                        serverUrl,
                        channelToken,
                        settings)
    }

}


// SDK Extension functions

// date format to use for publication dates
const val DATE_FORMAT = "MMMM d, yyyy"
const val UNKNOWN_DATE = "Published date unknown"

// get published date string from item field
fun ContentItem.getPublishedDateField(dateField : String) : String {
    val publishedDate = this.getDateField(dateField)
    return publishedDate?.getPublishedDateString() ?: UNKNOWN_DATE
}

// get download url for digital asset field
fun ContentItem.getUrlForAssetField(assetField : String) : String {
    return this.getDigitalAssetField(assetField)?.getThumbnailUrl() ?: ""
}

// extension function on ContentDate to get published date string in format we want
fun ContentDate.getPublishedDateString(): String {
    // parse date value to get a value to display for the date or "?" if null
    return this.dateParser.getDisplayString(ContentDateDisplayType.Date, DATE_FORMAT)
}

// extension function on DigitalAsset to get downloadUrl
fun DigitalAsset.getThumbnailUrl() : String {
    return ContentApi.deliveryClient.buildDigitalAssetThumbnailUrl(this.id)
}