/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
 package com.oracle.content.samples.blog.viewmodel

import androidx.lifecycle.MutableLiveData
import com.oracle.content.sdk.model.item.ContentItem

import com.oracle.content.sdk.request.GetContentItemRequest


const val ARTICLE_CONTENT_FIELD = "article_content"
const val ARTICLE_IMAGE_CAPTION = "image_caption"
const val ARTICLE_AUTHOR = "author"
const val AUTHOR_AVATAR_FIELD = "avatar"

// for getting article detail
class ArticleDetailRepository {

    // fetch and display the article detail
    fun fetchArticleDetail(article : MutableLiveData<ApiResponse<ArticleDetail>>, articleId: String?) {

        // make a call to get the article, expanding the "author" field
        val request  = GetContentItemRequest(ContentApi.deliveryClient, articleId)
                .expand(ARTICLE_AUTHOR)     // only expand the author field
                .linksNone()

        // get the article detail as an asynchronous call
        request.fetchAsync {response ->
            // successful SDK call?
            if (response.isSuccess) {
                // return the article detail
                article.value = ApiResponse(getArticleDetail(response.result))
            } else {
                // handle exception in case of error
                article.value = ApiResponse(response.exception)
            }
        }
    }

    // extract fields from content item into ArticleDetail
    private fun getArticleDetail(articleItem : ContentItem) : ArticleDetail {

        // field references item author
        val author = articleItem.getContentItemField(ARTICLE_AUTHOR)

        // return article detail with all of the fields from content item and author
        return ArticleDetail(
                name = articleItem.name,
                imageUrl = articleItem.getUrlForAssetField(ARTICLE_IMAGE_FIELD),
                imageCaption = articleItem.getTextField(ARTICLE_IMAGE_CAPTION),
                content = articleItem.getTextField(ARTICLE_CONTENT_FIELD),
                publishedDateString = articleItem.getPublishedDateField(PUBLISHED_DATE_FIELD),
                author = Author(author.name, author.getUrlForAssetField(AUTHOR_AVATAR_FIELD)))

    }


}