package com.diskin.alon.movieguide.news.presentation.controller

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.diskin.alon.movieguide.common.presentation.ErrorViewData
import com.diskin.alon.movieguide.common.presentation.UpdateViewData
import com.diskin.alon.movieguide.news.presentation.R
import com.diskin.alon.movieguide.news.presentation.databinding.FragmentArticleBinding
import com.diskin.alon.movieguide.news.presentation.viewmodel.ArticleViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.migration.OptionalInject

@OptionalInject
@AndroidEntryPoint
class ArticleFragment : Fragment() {

    private val viewModel: ArticleViewModel by viewModels()
    private var errorSnackbar: Snackbar? = null
    private lateinit var binding: FragmentArticleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentArticleBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Observe view model article state
        viewModel.article.observe(viewLifecycleOwner) { article -> binding.article = article }

        // Observe view model update state
        viewModel.update.observe(viewLifecycleOwner) { update ->
            when(update) {
                is UpdateViewData.Update -> binding.progressBar.visibility = View.VISIBLE
                is UpdateViewData.EndUpdate -> binding.progressBar.visibility = View.GONE
            }
        }

        // Observe view model error state
        viewModel.error.observe(viewLifecycleOwner) { error ->
            when(error) {
                is ErrorViewData.NoError -> errorSnackbar?.dismiss()
                is ErrorViewData.NotRetriable -> showNotRetriableError(error)
                is ErrorViewData.Retriable -> showRetriableError(error)
            }
        }

        // Set fab click listener
        binding.fab.setOnClickListener {
            val article = binding.article
            when{
                article == null -> notifyActionNotAvailable()
                else -> openArticleWebUrl(article.articleUrl)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_article, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
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

    private fun shareArticle() {
        viewModel.article.value?.let { article ->
            ShareCompat.IntentBuilder
                .from(requireActivity())
                .setType(getString(R.string.mime_type_text))
                .setText(article.articleUrl)
                .setChooserTitle(getString(R.string.title_share_article_chooser))
                .startChooser()
        } ?: run {
            Toast.makeText(
                requireActivity(),
                getString(R.string.title_action_not_available),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun showNotRetriableError(error: ErrorViewData.NotRetriable) {
        errorSnackbar = Snackbar.make(
            binding.nestedScrollView,
            error.reason,
            Snackbar.LENGTH_INDEFINITE)

        errorSnackbar?.show()
    }

    private fun showRetriableError(error: ErrorViewData.Retriable) {
        errorSnackbar = Snackbar.make(
            binding.nestedScrollView,
            error.reason,
            Snackbar.LENGTH_INDEFINITE)
            .setAction(getString(R.string.action_retry)) { error.retry() }

        errorSnackbar?.show()
    }

    private fun openArticleWebUrl(url: String) {
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        val chooser = Intent.createChooser(webIntent, getString(R.string.title_read_article_origin))

        startActivity(chooser)
    }

    private fun notifyActionNotAvailable() {
        Toast.makeText(requireActivity(),"Action not available", Toast.LENGTH_LONG).show()
    }
}