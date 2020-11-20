package com.diskin.alon.movieguide.reviews.presentation.controller

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.diskin.alon.movieguide.reviews.presentation.databinding.MovieBinding
import com.diskin.alon.movieguide.reviews.presentation.data.Movie

/**
 * Layout adapter that display [Movie]s data.
 */
class MoviesAdapter(
    private val movieClickListener: (Movie) -> (Unit)
) : PagingDataAdapter<Movie, MoviesAdapter.MovieViewHolder>(
    DIFF_CALLBACK
) {

    companion object {

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Movie>() {

            override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Movie, newItem: Movie) =
                oldItem == newItem
        }
    }

    class MovieViewHolder(
        private val binding: MovieBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) {
            binding.movie = movie
        }
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = MovieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        binding.movieClickListener = movieClickListener

        return MovieViewHolder(binding)
    }
}