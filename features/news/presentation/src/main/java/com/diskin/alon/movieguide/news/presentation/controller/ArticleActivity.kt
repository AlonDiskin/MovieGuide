package com.diskin.alon.movieguide.news.presentation.controller

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
import androidx.lifecycle.ViewModelProvider
import com.diskin.alon.movieguide.common.presentation.ErrorViewData
import com.diskin.alon.movieguide.common.presentation.UpdateViewData
import com.diskin.alon.movieguide.common.presentation.createActivityViewModel
import com.diskin.alon.movieguide.news.presentation.R
import com.diskin.alon.movieguide.news.presentation.databinding.ActivityArticleBinding
import com.diskin.alon.movieguide.news.presentation.util.ArticleViewModelFactoryQualifier
import com.diskin.alon.movieguide.news.presentation.viewmodel.ArticleViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.migration.OptionalInject
import kotlinx.android.synthetic.main.activity_article.*
import javax.inject.Inject

@OptionalInject
@AndroidEntryPoint
class ArticleActivity : AppCompatActivity() {

    @Inject
    @ArticleViewModelFactoryQualifier
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: ArticleViewModel by createActivityViewModel(this) { viewModelFactory }
    private var errorSnackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityArticleBinding>(
            this,
            R.layout.activity_article
        )

        // Setup toolbar
        setSupportActionBar(toolbar)

        // Setup up navigation
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Observe view model article state
        viewModel.article.observe(this) { article -> binding.article = article }

        // Observe view model update state
        viewModel.update.observe(this) { update ->
            when(update) {
                is UpdateViewData.Update -> progress_bar?.visibility = View.VISIBLE
                is UpdateViewData.EndUpdate -> progress_bar?.visibility = View.GONE
            }
        }

        // Observe view model error state
        viewModel.error.observe(this) { error ->
            when(error) {
                is ErrorViewData.NoError -> errorSnackbar?.dismiss()
                is ErrorViewData.NotRetriable -> showNotRetriableError(error)
                is ErrorViewData.Retriable -> showRetriableError(error)
            }
        }

        // Set fab click listener
        fab.setOnClickListener {
            val article = binding.article
            when{
                article == null -> notifyActionNotAvailable()
                else -> openArticleWebUrl(article.articleUrl)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_article, menu)
        viewModel.article.observe(this) { article ->
            article?.let {
                val bookmarkingItem = menu.findItem(R.id.action_bookmarking)
                bookmarkingItem.isEnabled = true

                when (it.bookmarked) {
                    true -> bookmarkingItem
                        .setTitle(getString(R.string.title_action_unbookmark))
                        .setIcon(R.drawable.ic_bookmarked_24)
                        .setOnMenuItemClickListener {
                            viewModel.unBookmark()
                            true
                        }

                    false -> bookmarkingItem
                        .setTitle(getString(R.string.title_action_bookmark))
                        .setIcon(R.drawable.ic_not_bookmarked_24)
                        .setOnMenuItemClickListener {
                            viewModel.bookmark()
                            true
                        }
                }
            }
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_share -> {
                shareArticle()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun showNotRetriableError(error: ErrorViewData.NotRetriable) {
        errorSnackbar = Snackbar.make(
            nestedScrollView,
            error.reason,
            Snackbar.LENGTH_INDEFINITE)

        errorSnackbar?.show()
    }

    private fun showRetriableError(error: ErrorViewData.Retriable) {
        errorSnackbar = Snackbar.make(
            nestedScrollView,
            error.reason,
            Snackbar.LENGTH_INDEFINITE)
            .setAction(getString(R.string.action_retry)) { error.retry() }

        errorSnackbar?.show()
    }

    private fun shareArticle() {
        viewModel.article.value?.let { article ->
            ShareCompat.IntentBuilder
                .from(this)
                .setType(getString(R.string.mime_type_text))
                .setText(article.articleUrl)
                .setChooserTitle(getString(R.string.title_share_article_chooser))
                .startChooser()
        } ?: run {
            Toast.makeText(
                this,
                getString(R.string.title_action_not_available),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun openArticleWebUrl(url: String) {
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        val chooser = Intent.createChooser(webIntent, getString(R.string.title_read_article_origin))

        startActivity(chooser)
    }

    private fun notifyActionNotAvailable() {
        Toast.makeText(this,"Action not available",Toast.LENGTH_LONG).show()
    }
}