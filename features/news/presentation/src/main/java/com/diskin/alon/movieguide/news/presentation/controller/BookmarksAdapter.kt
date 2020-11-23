package com.diskin.alon.movieguide.news.presentation.controller

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.diskin.alon.movieguide.news.presentation.data.Headline
import com.diskin.alon.movieguide.news.presentation.databinding.BookmarkBinding

/**
 * Layout adapter that display bookmarked [Headline]s.
 */
class BookmarksAdapter(
    private val bookmarkClickListener: (Headline) -> (Unit)
) : ListAdapter<Headline, BookmarksAdapter.BookmarkViewHolder>(
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

    class BookmarkViewHolder(
        private val binding: BookmarkBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(headline: Headline) {
            binding.bookmark = headline
        }
    }

    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        val binding = BookmarkBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        binding.bookmarkClickListener = bookmarkClickListener
        return BookmarkViewHolder(binding)
    }
}