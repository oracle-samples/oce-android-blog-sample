/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
 package com.oracle.content.samples.blog.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


// class to represent author details
data class Author(
    val name : String,
    val avatarImageUrl : String?)

// class to represent article detail
data class ArticleDetail(
    val name : String,
    val imageUrl : String?,
    val imageCaption : String?,
    val content : String?,
    val publishedDateString : String?,
    var author : Author? = null)


class ArticleDetailViewModel(private val articleId : String?) : ViewModel() {
    private var data = MutableLiveData<ApiResponse<ArticleDetail>>()
    private val repository = ArticleDetailRepository()

    fun getArticleDetail() : LiveData<ApiResponse<ArticleDetail>>  {
        repository.fetchArticleDetail(data, articleId)
        return data
    }

}