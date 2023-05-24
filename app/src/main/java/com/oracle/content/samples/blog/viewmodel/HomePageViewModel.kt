/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
 package com.oracle.content.samples.blog.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


// class to represent topic field
data class Topic(
        val id : String,
        var name : String? = null,
        var imageUrl : String? = null,
        var detail : String? = null)

// class to represent homepage info
data class HomePage(
        val companyName: String?,
        val logoImageUrl : String?,
        val contactUrl : String?,
        val aboutUrl : String?,
        val topics : Map<String, Topic>
)


class HomePageViewModel : ViewModel() {
    private var data = MutableLiveData<ApiResponse<HomePage>>()
    private val repository = HomePageRepository()

    fun getHomePage() : LiveData<ApiResponse<HomePage>> {

        // in this example since synchronous SDK methods are used
        // in the repository the call must be made from a separate thread

        viewModelScope.launch(Dispatchers.IO) {

            val apiResponse = repository.fetchHomePageData()

            // post value back to UI thread
            data.postValue(apiResponse)
        }
        return data
    }

}