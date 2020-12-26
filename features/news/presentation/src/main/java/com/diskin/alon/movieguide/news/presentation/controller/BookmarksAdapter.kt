package com.diskin.alon.movieguide.news.presentation.controller

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
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
    private val bookmarkClickListener: (Headline, View) -> (Unit),
    private val bookmarkLongClickListener: (Headline, View) -> Boolean,
    private val optionsClickListener: (Headline, View) -> (Unit),
    private val selectedBookmarksIds: List<String>,
    var isMultiSelect: Boolean = false
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

        fun bind(headline: Headline,isMultiSelect: Boolean,selectedBookmarks: List<String>) {
            binding.bookmark = headline
            binding.root.setBackgroundColor(
                if (isMultiSelect && selectedBookmarks.contains(headline.id)) {
                    Color.LTGRAY
                } else {
                    Color.WHITE
                }
            )
        }
    }

    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it,isMultiSelect,selectedBookmarksIds) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        val binding = BookmarkBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        binding.bookmarkClickListener = bookmarkClickListener
        binding.optionsClickListener = optionsClickListener


        binding.root.setOnLongClickListener { view ->
            binding.bookmark?.let { headline ->
                bookmarkLongClickListener.invoke(headline,view)
            }
            true
        }
        return BookmarkViewHolder(binding)
    }
}