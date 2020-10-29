package com.diskin.alon.movieguide.news.presentation.controller

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.diskin.alon.movieguide.news.presentation.databinding.NewsHeadlineBinding
import com.diskin.alon.movieguide.news.presentation.model.NewsHeadline

/**
 * Layout adapter that display [NewsHeadline]s.
 */
class NewsHeadlinesAdapter(
    private val shareClickListener: (NewsHeadline) -> (Unit),
    private val headlineClickListener: (NewsHeadline) -> (Unit)
) : PagingDataAdapter<NewsHeadline, NewsHeadlinesAdapter.NewsHeadlineViewHolder>(
    DIFF_CALLBACK
) {

    companion object {

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<NewsHeadline>() {

            override fun areItemsTheSame(oldItem: NewsHeadline, newItem: NewsHeadline): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: NewsHeadline, newItem: NewsHeadline) =
                oldItem == newItem
        }
    }

    class NewsHeadlineViewHolder(
        private val binding: NewsHeadlineBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(newsTitle: NewsHeadline) {
            binding.newsTitle = newsTitle
        }
    }

    override fun onBindViewHolder(holder: NewsHeadlineViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsHeadlineViewHolder {
        val binding = NewsHeadlineBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        binding.shareClickListener = shareClickListener
        binding.headlineClickListener = headlineClickListener

        return NewsHeadlineViewHolder(binding)
    }
}