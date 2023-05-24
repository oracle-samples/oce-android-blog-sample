/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.content.samples.blog.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.oracle.content.samples.supremo.R
import com.oracle.content.samples.blog.ui.HomePageFragment.Companion.TOPIC_ID_KEY
import com.oracle.content.samples.blog.ui.HomePageFragment.Companion.TOPIC_NAME_KEY
import com.oracle.content.samples.blog.viewmodel.ApiResponse
import com.oracle.content.samples.blog.viewmodel.Article
import com.oracle.content.samples.supremo.databinding.FragmentArticlesBinding
import com.oracle.content.samples.supremo.databinding.ItemArticleItemBinding
import com.oracle.content.samples.blog.viewmodel.ArticlesViewModel as ArticlesViewModel1


class ArticlesFragment : Fragment() {

    companion object {
        const val ARTICLE_ID_KEY = "articleId"
        const val ARTICLE_NAME_KEY = "articleName"
    }

    @Suppress("UNCHECKED_CAST")
    class ArticlesViewModelFactory(private val topicId: String?) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ArticlesViewModel1(topicId) as T
        }


    }

    private lateinit var articlesViewModel: ArticlesViewModel1

    private var _binding: FragmentArticlesBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        _binding = FragmentArticlesBinding.inflate(inflater, container, false)
        val view = binding.root

        val topicId = arguments?.getString(TOPIC_ID_KEY)

        // create adapter passing in the topic list and glide instance to download images
        val articlesListAdapter = ArticleListAdapter()

        view.findViewById<RecyclerView>(R.id.article_list).apply {
            setHasFixedSize(true)

            layoutManager = GridLayoutManager(
                    context,
                    if (isLandscape()) 2 else 1,
                    GridLayoutManager.VERTICAL,
                    false)

            // line separators
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))

            adapter = articlesListAdapter

        }

        articlesViewModel = ViewModelProvider(this,
                ArticlesViewModelFactory(topicId)).get(ArticlesViewModel1::class.java)


        // show spinner
        showProgress()

        // observe the viewmodel, which will be called when articles are fetched
        articlesViewModel.getArticles().observe(viewLifecycleOwner,
                Observer<ApiResponse<List<Article>>> { response ->
                    // hide spinner
                    hideProgress()

                    if (response.error != null) {
                        handleSdkError(response.error)
                    } else if (response.data != null) {
                        articlesListAdapter.updateList(response.data)
                    }
                }
        )

        return view

    }

    override fun onResume() {
        super.onResume()

        val topicName = arguments?.getString(TOPIC_NAME_KEY)
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = topicName
            subtitle = ""
        }

    }

    class ArticleListAdapter : RecyclerView.Adapter<ArticleListAdapter.ViewHolder>() {

        class ViewHolder(val item: ItemArticleItemBinding) : RecyclerView.ViewHolder(item.root);

        private var articleList : List<Article> = listOf()

        fun updateList(updateList : List<Article> ) {
            articleList = updateList
            notifyDataSetChanged()
            //notifyItemRangeChanged(0, articleList.size)
        }


        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(parent: ViewGroup,
                                        viewType: Int): ViewHolder {

            val itemView = ItemArticleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

            return ViewHolder(itemView)
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            articleList[position].apply {

                holder.item.articleDate.text = publishedDateString
                holder.item.articleDetail.text = detail
                holder.item.articleImage.loadImageFromUrl(imageUrl)
                holder.item.articleName.text = name;

                holder.itemView.setOnClickListener {

                    // navigate to the topic companyName to get list of articles

                    val bundle = bundleOf(
                        ARTICLE_ID_KEY to id,
                        ARTICLE_NAME_KEY to name
                    )

                    Navigation.findNavController(holder.itemView).navigate(
                            R.id.action_article_to_detail,
                            bundle)

                }
            }
        }

        // Return the size of list
        override fun getItemCount() = articleList.size
    }



}
