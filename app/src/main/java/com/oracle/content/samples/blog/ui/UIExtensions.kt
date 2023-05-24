/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.content.samples.blog.ui

import android.content.res.Configuration
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.oracle.content.samples.blog.MainActivity
import com.oracle.content.samples.supremo.R
import com.oracle.content.sdk.ContentException


// extension to show SDK error as an alert
fun Fragment.showAlert(exception: ContentException) {

    val contentError = exception.contentError
    val title = if (contentError==null) getString(R.string.sdk_error) else contentError.title

    // This alert displays the internal verbose error message that provides technical
    // detail generally used for debugging.
    AlertDialog.Builder(activity!!)
            .setMessage(exception.verboseErrorMessage)
            .setTitle(title)
            .setPositiveButton("OK", null)
            .show()
}

// general handler for sdk errors
fun Fragment.handleSdkError(exception: ContentException) {

    // first show as an alert
    //showAlert(exception)

    // go to error fragment and display message

    val bundle = bundleOf(
            ErrorFragment.ERROR_MSG_KEY to exception.verboseErrorMessage
    )
    activity?.findNavController(R.id.nav_host_fragment)?.apply {
        navigate(R.id.go_to_error, bundle)
    }

}


fun Fragment.isLandscape() : Boolean
{ return (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)}

// show progress spinner
fun Fragment.showProgress() {
    (activity as MainActivity).showProgress()
}

// hide progress spinner
fun Fragment.hideProgress() {
    (activity as MainActivity).hideProgress()
}

// for loading images from a resource url
fun ImageView.loadImageFromUrl(imageUrl : String?) {
    Glide.with(context.applicationContext)
            .load(imageUrl)
            .apply(RequestOptions.bitmapTransform( RoundedCorners(14)))
            .into(this)
}