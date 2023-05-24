/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.content.samples.blog

import androidx.test.espresso.IdlingResource

interface FetcherListener {
    fun doneFetching()
    fun beginFetching()
}

class FetchingIdlingResource: IdlingResource, FetcherListener {
    private var idle = true
    private var resourceCallback: 
            IdlingResource.ResourceCallback? = null

    override fun getName(): String {
        return FetchingIdlingResource::class.java.simpleName
    }

    override fun isIdleNow() = idle

    override fun registerIdleTransitionCallback(
        callback: IdlingResource.ResourceCallback?) {
        resourceCallback = callback
    }

    override fun doneFetching() {
        idle = true
        resourceCallback?.onTransitionToIdle()
    }

    override fun beginFetching() {
        idle = false
    }
}