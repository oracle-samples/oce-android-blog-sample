/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
 package com.oracle.content.samples.blog.viewmodel

import com.oracle.content.sdk.ContentException

// container for the sdk response data or error
class ApiResponse<T> {
    val data : T?
    val error : ContentException?

    // use this for successful result with object
    constructor(data : T ) {
        this.data = data
        this.error = null
    }

    // use this for error with content exception
    constructor(error : ContentException) {
        this.data = null
        this.error = error
    }
}