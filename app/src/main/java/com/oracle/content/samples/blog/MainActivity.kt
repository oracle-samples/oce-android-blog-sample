/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.content.samples.blog

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.oracle.content.samples.blog.viewmodel.ContentApi
import com.oracle.content.samples.supremo.R


class MainActivity : AppCompatActivity() {


    // to show spinner when making network calls
    private lateinit var progressBar: ProgressBar
    private var showProgress = false
    companion object {
        private var fetcher: FetcherListener? = null
    }

    fun setFetcherListener(listener: FetcherListener) { fetcher = listener}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // create sdk client
        ContentApi.init(applicationContext)

        setContentView(R.layout.activity_main)

        progressBar = findViewById(R.id.progress_loader)

        setupNavigation()
    }

    private fun setupNavigation() {
        val navController = findNavController(R.id.nav_host_fragment)
        setupActionBarWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_settings, menu)
        return super.onCreateOptionsMenu(menu)
    }

    fun showProgress() {
        fetcher?.beginFetching()
        showProgress = true
        // only show progress after 1 second if no response
        Handler(Looper.getMainLooper()).postDelayed( {
            if (showProgress) progressBar.visibility = View.VISIBLE },
                1000)

    }

    fun hideProgress() {
        fetcher?.doneFetching()
        showProgress = false
        progressBar.visibility = View.GONE

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_settings) {
            findNavController(R.id.nav_host_fragment).navigate(R.id.go_to_settings)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp() =
            findNavController(R.id.nav_host_fragment).navigateUp()

}
