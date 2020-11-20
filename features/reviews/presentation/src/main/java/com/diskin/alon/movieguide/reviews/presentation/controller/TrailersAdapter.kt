package com.diskin.alon.movieguide.reviews.presentation.controller

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.diskin.alon.movieguide.reviews.presentation.databinding.MovieTrailerBinding

/**
 * Layout adapter that display [MovieReview]s trailers data.
 */
class TrailersAdapter : ListAdapter<String,TrailersAdapter.MovieTrailerViewHolder>(
    DIFF_CALLBACK
) {

    companion object {

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<String>() {

            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String) =
                oldItem == newItem
        }
    }

    class MovieTrailerViewHolder(
        private val binding: MovieTrailerBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(url: String) {
            binding.url = url
        }
    }

    override fun onBindViewHolder(holder: MovieTrailerViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieTrailerViewHolder {
        val binding = MovieTrailerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return MovieTrailerViewHolder(binding)
    }
}