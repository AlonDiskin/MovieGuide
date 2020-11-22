package com.diskin.alon.movieguide.reviews.presentation.controller

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.diskin.alon.movieguide.reviews.presentation.data.Trailer
import com.diskin.alon.movieguide.reviews.presentation.databinding.MovieTrailerBinding

/**
 * Layout adapter that display [MovieReview]s trailers data.
 */
class TrailersAdapter(
    private val trailerClickListener: (Trailer) -> (Unit)
) : ListAdapter<Trailer,TrailersAdapter.MovieTrailerViewHolder>(
    DIFF_CALLBACK
) {

    companion object {

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Trailer>() {

            override fun areItemsTheSame(oldItem: Trailer, newItem: Trailer): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Trailer, newItem: Trailer) =
                oldItem == newItem
        }
    }

    class MovieTrailerViewHolder(
        private val binding: MovieTrailerBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(trailer: Trailer) {
            binding.trailer = trailer
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

        binding.trailerClickListener = trailerClickListener
        return MovieTrailerViewHolder(binding)
    }
}