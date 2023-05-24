/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
 package com.oracle.content.samples.blog.viewmodel

import com.oracle.content.sdk.ContentException
import com.oracle.content.sdk.request.SearchAssetsRequest

const val HOMEPAGE_TYPE = "OCEGettingStartedHomePage"
const val HOMEPAGE_NAME = "HomePage"
const val LOGO_IMAGE_FIELD = "company_logo"
const val CONTACT_URL_FIELD = "contact_url"
const val ABOUT_URL_FIELD = "about_url"
const val COMPANY_NAME_FIELD = "company_name"
const val TOPIC_REFERENCE_LIST = "topics"
const val TOPIC_THUMBNAIL_FIELD = "thumbnail"

// this value is extracted from the homepage reference list of topics
//const val TOPICS_TYPE = "OCEGettingStartedTopic"

// to get home page item
class HomePageRepository {

    // one set of global home page data
    companion object {
        lateinit var homePage : HomePage
        lateinit var topicType : String
        fun isHomePageInitialized() = Companion::homePage.isInitialized
    }

    fun fetchHomePageData() : ApiResponse<HomePage> {

        // synchronous SDK calls since we are in separate thread
        try {

            // first get home page object
            homePage = getHomePage()

            // then get topic list from home page to get the image url and detail fields
            getTopicList()

            // trigger observe back in HomePageFragment now that we have the data
            return ApiResponse(homePage)


        } catch (exception: ContentException) {

            // set exception in response value
            return ApiResponse(exception)
        }
    }

    private fun getHomePage(): HomePage {

        // SDK: create search request to get home page
        val getHomePageRequest = SearchAssetsRequest(ContentApi.deliveryClient)
                .type(HOMEPAGE_TYPE)    // search by content type
                .name(HOMEPAGE_NAME)    // ...and by content name
                .linksNone()        // do not include links in the response
                .fieldsAll()        // do include all fields

        val searchResult = getHomePageRequest.fetchResult()

        // from the search result, only expect a single home page
        val contentItems = searchResult.contentItems

        if (contentItems.isNullOrEmpty())
            throw ContentException(ContentException.REASON.itemNotFound, "No home page found for type $HOMEPAGE_TYPE")

        val item = contentItems.first()

        // convert list of ids into topic list
        val topicList = item.getReferenceListIds(TOPIC_REFERENCE_LIST).map { Topic(it) }
        // grab the topic type from one of the references
        topicType = item.getReferenceListField(TOPIC_REFERENCE_LIST).first().value.type

        return HomePage(
                companyName = item.getTextField(COMPANY_NAME_FIELD),
                logoImageUrl = item.getDigitalAssetField(LOGO_IMAGE_FIELD).getThumbnailUrl(),
                contactUrl = item.getTextField(CONTACT_URL_FIELD),
                aboutUrl = item.getTextField(ABOUT_URL_FIELD),
                topics = topicList.associateBy { it.id }) // convert list to map

    }

    // process the topic list SDK result
    private fun getTopicList() {

        // SDK: create search request to get all the topics
        val getTopicsRequest = SearchAssetsRequest(ContentApi.deliveryClient)
                .type(topicType)
                .linksNone()
                .fieldsAll()

        val searchResult = getTopicsRequest.fetchResult()

        // from the search result, get the complete list of items
        val contentItems = searchResult.contentItems

        if (contentItems.isNullOrEmpty())
            throw ContentException(ContentException.REASON.itemNotFound, "No topics found for type $topicType")

        // update topic with image url and detail
        contentItems.map { item ->
            homePage.topics[item.id]?.apply {
                imageUrl = item.getUrlForAssetField(TOPIC_THUMBNAIL_FIELD)
                detail = item.description
                name = item.name
            }
        }
    }


}