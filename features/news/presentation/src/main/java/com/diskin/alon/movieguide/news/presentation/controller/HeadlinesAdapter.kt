package com.diskin.alon.movieguide.news.presentation.controller

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.diskin.alon.movieguide.news.presentation.data.Headline
import com.diskin.alon.movieguide.news.presentation.databinding.HeadlineBinding

/**
 * Layout adapter that display [Headline]s.
 */
class HeadlinesAdapter(
    private val shareClickListener: (Headline) -> (Unit),
    private val headlineClickListener: (Headline) -> (Unit)
) : PagingDataAdapter<Headline, HeadlinesAdapter.HeadlineViewHolder>(
    DIFF_CALLBACK
) {

    companion object {

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Headline>() {

            override fun areItemsTheSame(oldItem: Headline, newItem: Headline): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Headline, newItem: Headline) =
                oldItem == newItem
        }
    }

    class HeadlineViewHolder(
        private val binding: HeadlineBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(newsTitle: Headline) {
            binding.newsTitle = newsTitle
        }
    }

    override fun onBindViewHolder(holder: HeadlineViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeadlineViewHolder {
        val binding = HeadlineBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        binding.shareClickListener = shareClickListener
        binding.headlineClickListener = headlineClickListener

        return HeadlineViewHolder(binding)
    }
}