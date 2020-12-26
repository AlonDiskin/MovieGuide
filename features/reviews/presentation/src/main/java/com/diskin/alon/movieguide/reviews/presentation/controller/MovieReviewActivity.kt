package com.diskin.alon.movieguide.reviews.presentation.controller

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.databinding.DataBindingUtil
import com.diskin.alon.movieguide.common.presentation.ViewData
import com.diskin.alon.movieguide.common.presentation.ErrorViewData
import com.diskin.alon.movieguide.reviews.presentation.R
import com.diskin.alon.movieguide.reviews.presentation.data.Trailer
import com.diskin.alon.movieguide.reviews.presentation.databinding.ActivityMovieReviewBinding
import com.diskin.alon.movieguide.reviews.presentation.viewmodel.MovieReviewViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_movie_review.*
import javax.inject.Inject

/**
 * Display movie review info,and provide content engagement actions.
 */
class MovieReviewActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModel: MovieReviewViewModel
    private var snackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil
            .setContentView<ActivityMovieReviewBinding>(this,R.layout.activity_movie_review)

        // Inject activity
        AndroidInjection.inject(this)

        // Setup toolbar
        setSupportActionBar(toolbar)

        // Setup up navigation
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Setup trailers adapter
        val adapter = TrailersAdapter(this::openTrailerWebLink)
        binding.trailers.adapter = adapter

        // Observe view model review state
        viewModel.movieReview.observe(this,{ viewData ->
            // Update review data if available
            adapter.submitList(viewData.data?.trailers)
            binding.setReview(viewData.data)

            // Hide any prev error/loading notification
            snackbar?.dismiss()
            progress_bar?.visibility = View.GONE

            when(viewData) {
                is ViewData.Updating -> progress_bar?.visibility = View.VISIBLE
                is ViewData.Error -> handleReviewStateError(viewData.error)
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_movie_review, menu)
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
        snackbar = Snackbar.make(
            review_content,
            error.reason,
            Snackbar.LENGTH_INDEFINITE)

        snackbar?.show()
    }

    private fun showRetriableError(error: ErrorViewData.Retriable) {
        snackbar = Snackbar.make(
            review_content,
            error.reason,
            Snackbar.LENGTH_INDEFINITE)
            .setAction(getString(R.string.action_retry)) { error.retry() }

        snackbar?.show()
    }

    private fun shareReview() {
        viewModel.movieReview.value?.data?.let { review ->
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