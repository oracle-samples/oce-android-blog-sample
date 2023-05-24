/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.content.samples.blog.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.oracle.content.samples.supremo.R
import com.oracle.content.samples.blog.viewmodel.ApiResponse
import com.oracle.content.samples.blog.viewmodel.HomePage
import com.oracle.content.samples.blog.viewmodel.HomePageViewModel
import com.oracle.content.samples.blog.viewmodel.Topic
import com.oracle.content.samples.supremo.databinding.ItemTopicCardBinding


class HomePageFragment : Fragment() {

    companion object {
        const val TOPIC_ID_KEY = "topicId"
        const val TOPIC_NAME_KEY = "topicName"
    }

    private lateinit var homePageViewModel: HomePageViewModel
    private var topicListAdapter = TopicListAdapter()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_homepage, container, false)

        view.findViewById<RecyclerView>(R.id.topic_list).apply {
            setHasFixedSize(true)

            layoutManager = GridLayoutManager(
                    context,
                    if (isLandscape()) 2 else 1,
                    GridLayoutManager.VERTICAL,
                    false)

            adapter = topicListAdapter

        }

        homePageViewModel = ViewModelProvider(this).get(HomePageViewModel::class.java)


        // show spinner
        showProgress()


        // observe the viewmodel, which will be called when home page is fetched
        homePageViewModel.getHomePage().observe(viewLifecycleOwner,
                Observer<ApiResponse<HomePage>> { response ->

                    // hide spinner
                    hideProgress()

                    if (response.error != null) {
                        handleSdkError(response.error)

                    } else if (response.data != null) {

                        //view.findViewById<ImageView>(R.id.logo_image).loadImageFromUrl(response.data.logoImageUrl)

                        // set list in recycler view
                        topicListAdapter.updateList(response.data.topics.values.toList())

                    }
                }
        )

        return view

    }


    class TopicListAdapter : RecyclerView.Adapter<TopicListAdapter.ViewHolder>() {

        private var topicList: List<Topic> = listOf()

        // Return the size of list
        override fun getItemCount() = topicList.size

        fun updateList(updateList: List<Topic>) {
            topicList = updateList
            notifyDataSetChanged()
            //notifyItemRangeChanged(0, topicList.size)
        }

        class ViewHolder(val item: ItemTopicCardBinding) : RecyclerView.ViewHolder(item.root)


        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(parent: ViewGroup,
                                        viewType: Int): ViewHolder {
            // create a new view
            val itemView = ItemTopicCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)

            return ViewHolder(itemView)
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            topicList[position].apply {

                holder.item.topicName.text = name
                holder.item.topicDetail.text = detail
                holder.item.topicImage.loadImageFromUrl(imageUrl)

                holder.itemView.setOnClickListener {
                    // navigate to the topic companyName to get list of articles

                    val bundle = bundleOf(
                            TOPIC_ID_KEY to id,
                            TOPIC_NAME_KEY to name
                    )

                    Navigation.findNavController(holder.itemView).navigate(
                            R.id.action_topic_to_articles,
                            bundle)

                }
            }
        }

    }

}
