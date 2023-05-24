/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
 package com.oracle.content.samples.blog.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// class to represent article fields
data class Article(
        val name : String,
        val id : String,
        val imageUrl : String?,
        val detail : String?,
        var publishedDateString : String?)

class ArticlesViewModel(private val topicId : String?) : ViewModel() {
    private var data = MutableLiveData<ApiResponse<List<Article>>>()
    private val repository = ArticlesRepository()

    fun getArticles() : LiveData<ApiResponse<List<Article>>>  {
        repository.fetchArticles(data, topicId)
        return data
    }

}