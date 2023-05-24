/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.content.samples.blog.ui

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.oracle.content.samples.supremo.BuildConfig
import com.oracle.content.samples.supremo.R
import com.oracle.content.samples.blog.viewmodel.ContentApi
import com.oracle.content.samples.blog.viewmodel.HOMEPAGE_TYPE
import com.oracle.content.samples.blog.viewmodel.HomePageRepository
import com.oracle.content.samples.blog.viewmodel.HomePageRepository.Companion.homePage
import com.oracle.content.sdk.request.SearchAssetsRequest


const val SERVER_URL_PREF_KEY = "pref_key_server_url"
const val CHANNEL_TOKEN_PREF_KEY = "pref_key_channel_token"
const val ENABLE_CACHE_PREF_KEY = "pref_key_cache_enabled"
const val ENABLE_LOGGING_PREF_KEY = "pref_key_logging_enabled"

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {


    private var serverPref : EditTextPreference? = null
    private var channelPref : EditTextPreference? = null
    private var testConnectPref : Preference? = null


    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == SERVER_URL_PREF_KEY || key == CHANNEL_TOKEN_PREF_KEY) {
            updateConnectionSummaryFields()
        }

        // for changes in shared preference settings, re-create the delivery client
        ContentApi.createDeliveryClient()
    }

    override fun onResume() {
        preferenceManager.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
        super.onResume()
    }

    override fun onPause() {
        preferenceManager.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)


        preferenceScreen.apply {

            serverPref = findPreference(SERVER_URL_PREF_KEY)
            channelPref = findPreference(CHANNEL_TOKEN_PREF_KEY)

            // first-time, full in connection info from defaults from build.gradle
            if (serverPref?.text.isNullOrEmpty()) {
                restoreDefaultConnection()
            }

            // set summary values to refect values
            updateConnectionSummaryFields()

            testConnectPref = findPreference("pref_key_test_connection")
            val restoreDefault = findPreference<Preference>("pref_key_restore_default")

            val aboutPref = findPreference<Preference>("pref_key_about")
            val contactPref = findPreference<Preference>("pref_key_contact_us")
            if (HomePageRepository.isHomePageInitialized()) {
                aboutPref?.summary = homePage.aboutUrl
                contactPref?.summary = homePage.contactUrl
            } else {
                // disable if not available
                aboutPref?.isEnabled = false
                contactPref?.isEnabled = false
            }

            // handlers for clicking on test connection and restore
            testConnectPref?.setOnPreferenceClickListener {
                testConnection()
                true
            }

            restoreDefault?.setOnPreferenceClickListener {
                restoreDefaultConnection()
                ContentApi.createDeliveryClient()
                true
            }

            // launch to external browser for about or contact
            aboutPref?.setOnPreferenceClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.toString())))
                true
            }

            contactPref?.setOnPreferenceClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.summary.toString())))
                true
            }

        }

        //setHasOptionsMenu(true)

    }


    // no settings menu in settings screen
    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.action_settings).isVisible = false
        //super.onPrepareOptionsMenu(menu)
    }

    // restore the built-in url and channel token
    private fun restoreDefaultConnection() {
        serverPref?.text = BuildConfig.SERVER_URL
        channelPref?.text = BuildConfig.CHANNEL_TOKEN
        updateConnectionSummaryFields()
    }

    // update the summary fields to contain the values
    private fun updateConnectionSummaryFields() {
        serverPref?.summary = serverPref?.text
        channelPref?.summary = channelPref?.text
    }

    // test server connection by making very simple search call
    private fun testConnection() {
        showProgress()

        val request = SearchAssetsRequest(ContentApi.deliveryClient).type(HOMEPAGE_TYPE).linksNone()
        request.fetchAsync { response ->
            hideProgress()
            if (response.isSuccess) {
                testConnectPref?.summary = "OK"
            } else {
                // show error
                showAlert(response.exception)
                testConnectPref?.summary = "FAIL"
            }
        }
    }


}