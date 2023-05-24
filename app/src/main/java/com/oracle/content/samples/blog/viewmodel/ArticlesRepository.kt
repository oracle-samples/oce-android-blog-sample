/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
 package com.oracle.content.samples.blog.viewmodel

import androidx.lifecycle.MutableLiveData
import com.oracle.content.sdk.model.AssetSearchResult
import com.oracle.content.sdk.model.field.FieldName
import com.oracle.content.sdk.request.SearchAssetsRequest


const val ARTICLES_TYPE = "OCEGettingStartedArticle"
const val TOPIC_FIELD = "topic"
const val PUBLISHED_DATE_FIELD = "published_date"
const val ARTICLE_IMAGE_FIELD = "image"

// to get the list of articles for a topic
class ArticlesRepository {

    // fetch and display the article list
    fun fetchArticles(apiResponse: MutableLiveData<ApiResponse<List<Article>>>, topicId : String?) {

        // restrict the search to only specific fields we need (not all fields from article)
        // this is not necessary as by default all fields would be returned, but it shows
        // how you can restrict to a specific set of fields if desired
        val fieldList = listOf(
                FieldName.NAME.value,
                FieldName.DESCRIPTION.value,
                ARTICLE_IMAGE_FIELD,
                PUBLISHED_DATE_FIELD)

        // SDK: create search request to get all the articles
        // sort by published_date field in descending order
        val searchRequest = SearchAssetsRequest(ContentApi.deliveryClient)
                .type(ARTICLES_TYPE)                // get "article" type
                .fieldEquals(TOPIC_FIELD, topicId)  // match topic (e.g. drinks, recipes)
                .sortByField(PUBLISHED_DATE_FIELD)  // sort by published date (optional)
                .sortOrderDescending(true)
                .fields(fieldList)      // restricted field list (optional)
                .linksNone()            // no need for links (optional)

        // SDK: make request to get articles using async callback method
        searchRequest.fetchAsync {response ->
            // successful SDK call?
            if (response.isSuccess) {
                // process the list and return response
                apiResponse.value = ApiResponse(getArticleList(response.result))
            } else {
                // handle exception in case of error
                apiResponse.value = ApiResponse(response.exception)
            }
        }
    }

    // process the article list SDK result
    private fun getArticleList(searchResult: AssetSearchResult) : List<Article> {

        // from the search result, get the complete list of items
        val contentItems = searchResult.contentItems

        if (!contentItems.isNullOrEmpty()) {

            // for every content item, map to the Article model
            return contentItems.map {item ->

                // init Article from content item fields
                Article(
                        name = item.name,
                        id = item.id,
                        imageUrl = item.getUrlForAssetField(ARTICLE_IMAGE_FIELD),
                        detail = item.description,
                        publishedDateString = item.getPublishedDateField(PUBLISHED_DATE_FIELD))
            }

        }

        // empty list if null or empty
        return listOf()
    }

}