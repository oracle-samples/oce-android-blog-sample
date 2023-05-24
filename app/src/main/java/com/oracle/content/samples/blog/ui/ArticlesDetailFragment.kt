/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.content.samples.blog.ui

import android.os.Bundle
import android.text.Html
import android.text.Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oracle.content.samples.blog.ui.ArticlesFragment.Companion.ARTICLE_ID_KEY
import com.oracle.content.samples.blog.viewmodel.ApiResponse
import com.oracle.content.samples.blog.viewmodel.ArticleDetail
import com.oracle.content.samples.blog.viewmodel.ArticleDetailViewModel
import com.oracle.content.samples.supremo.databinding.FragmentArticleDetailBinding


class ArticleDetailFragment : Fragment() {

    @Suppress("UNCHECKED_CAST")
    class ArticleDetailViewModelFactory(private val articleId: String?) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ArticleDetailViewModel(articleId) as T
        }

    }

    private lateinit var articleDetailViewModel: ArticleDetailViewModel

    private var _binding: FragmentArticleDetailBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

       // val view = inflater.inflate(R.layout.fragment_article_detail, container, false)

        _binding = FragmentArticleDetailBinding.inflate(inflater, container, false)
        val view = binding.root

        val articleId = arguments?.getString(ARTICLE_ID_KEY)

        articleDetailViewModel =
                ViewModelProvider(this,
                        ArticleDetailViewModelFactory(articleId)).get(ArticleDetailViewModel::class.java)


        // show spinner
        showProgress()

        // observe the viewmodel, which will be called when article detail comes back
        articleDetailViewModel.getArticleDetail().observe(viewLifecycleOwner,
                Observer<ApiResponse<ArticleDetail>> { response ->
                    // hide spinner
                    hideProgress()

                    if (response.error != null) {
                        handleSdkError(response.error)
                    } else if (response.data != null) {
                        updateData(response.data)
                    }
                }
        )

        return view

    }

    private fun updateData(article : ArticleDetail) {

        article.apply {

            binding.articleText.text = Html.fromHtml(content, TO_HTML_PARAGRAPH_LINES_INDIVIDUAL)
            binding.articleMageCaption.text = imageCaption
            binding.articlePublishedDate.text = publishedDateString
            binding.articleImage.loadImageFromUrl(imageUrl)
            binding.articleTitle.text = article.name

            // author name and image
            //view.author_name.text = author?.name
            binding.authorImage.loadImageFromUrl(author?.avatarImageUrl)

        }
    }

}
