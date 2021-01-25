package com.diskin.alon.movieguide.reviews.presentation.controller

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.databinding.DataBindingUtil
import com.diskin.alon.movieguide.common.presentation.ErrorViewData
import com.diskin.alon.movieguide.common.presentation.UpdateViewData
import com.diskin.alon.movieguide.reviews.presentation.R
import com.diskin.alon.movieguide.reviews.presentation.data.Trailer
import com.diskin.alon.movieguide.reviews.presentation.databinding.ActivityMovieReviewBinding
import com.diskin.alon.movieguide.reviews.presentation.viewmodel.MovieReviewViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.migration.OptionalInject
import kotlinx.android.synthetic.main.activity_movie_review.*

/**
 * Display movie review info,and provide content engagement actions.
 */
@OptionalInject
@AndroidEntryPoint
class MovieReviewActivity : AppCompatActivity() {

    private val viewModel: MovieReviewViewModel by viewModels()
    private var errorSnackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil
            .setContentView<ActivityMovieReviewBinding>(this,R.layout.activity_movie_review)

        // Setup toolbar
        setSupportActionBar(toolbar)

        // Setup up navigation
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Setup trailers adapter
        val adapter = TrailersAdapter(this::openTrailerWebLink)
        binding.trailers.adapter = adapter

        // Observe view model review state
        viewModel.movieReview.observe(this) {
            binding.movieReview = it
            adapter.submitList(it?.trailers)
        }

        // Observe view model review update state
        viewModel.reviewUpdate.observe(this) { update ->
            when(update) {
                is UpdateViewData.Update -> progress_bar?.visibility = View.VISIBLE
                is UpdateViewData.EndUpdate -> progress_bar?.visibility = View.GONE
            }
        }

        // Observe view model review error state
        viewModel.reviewError.observe(this) { error ->
            when(error) {
                is ErrorViewData.NoError -> errorSnackbar?.dismiss()
                is ErrorViewData.NotRetriable -> showNotRetriableError(error)
                is ErrorViewData.Retriable -> showRetriableError(error)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_movie_review, menu)
        viewModel.movieReview.observe(this, { review ->
            review?.let {
                val favoritingItem = menu.findItem(R.id.action_favoriting)
                favoritingItem.isEnabled = true

                when (it.favorite) {
                    true -> favoritingItem
                        .setTitle(getString(R.string.title_action_unfavorite_movie))
                        .setIcon(R.drawable.ic_favorite)
                        .setOnMenuItemClickListener {
                            viewModel.unFavoriteReviewedMovie()
                            true
                        }

                    false -> favoritingItem
                        .setTitle(getString(R.string.title_action_favorite_movie))
                        .setIcon(R.drawable.ic_not_favorite)
                        .setOnMenuItemClickListener {
                            viewModel.favoriteReviewedMovie()
                            true
                        }
                }
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_share -> {
                shareReview()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun handleReviewStateError(error: ErrorViewData) {
        when(error) {
            is ErrorViewData.Retriable -> showRetriableError(error)
            is ErrorViewData.NotRetriable -> showNotRetriableError(error)
        }
    }

    private fun showNotRetriableError(error: ErrorViewData.NotRetriable) {
        errorSnackbar = Snackbar.make(
            review_content,
            error.reason,
            Snackbar.LENGTH_INDEFINITE)

        errorSnackbar?.show()
    }

    private fun showRetriableError(error: ErrorViewData.Retriable) {
        errorSnackbar = Snackbar.make(
            review_content,
            error.reason,
            Snackbar.LENGTH_INDEFINITE)
            .setAction(getString(R.string.action_retry)) { error.retry() }

        errorSnackbar?.show()
    }

    private fun shareReview() {
        viewModel.movieReview.value?.let { review ->
            ShareCompat.IntentBuilder
                .from(this)
                .setType(getString(R.string.mime_type_text))
                .setText(review.webUrl)
                .setChooserTitle(getString(R.string.title_share_review_chooser))
                .startChooser()
        } ?: run {
            Toast.makeText(
                this,
                getString(R.string.title_action_not_available),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun openTrailerWebLink(trailer: Trailer) {
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(trailer.url))
        val chooser = Intent.createChooser(webIntent, getString(R.string.title_trailer_view_chooser))

        startActivity(chooser)
    }
}