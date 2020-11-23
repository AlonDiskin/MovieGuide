package com.diskin.alon.movieguide.news.presentation.controller

import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.diskin.alon.movieguide.common.presentation.ViewData
import com.diskin.alon.movieguide.news.appservices.data.BookmarkSorting
import com.diskin.alon.movieguide.news.presentation.R
import com.diskin.alon.movieguide.news.presentation.data.Headline
import com.diskin.alon.movieguide.news.presentation.viewmodel.BookmarksViewModel
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_bookmarks.*
import javax.inject.Inject

class BookmarksFragment : Fragment(){

    @Inject
    lateinit var viewModel: BookmarksViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bookmarks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup bookmarked headlines adapter
        val adapter = BookmarksAdapter(this::navigateToArticle)
        bookmarks.adapter = adapter

        // Observe bookmarks state from view model
        viewModel.bookmarks.observe(viewLifecycleOwner,{
            // Submit any existing update data
            adapter.submitList(it?.data)

            // Handle bookmarks view data state change
            when(it) {
                is ViewData.Updating -> progress_bar?.visibility = View.VISIBLE
                is ViewData.Data -> progress_bar?.visibility = View.GONE
                is ViewData.Error -> progress_bar?.visibility = View.GONE
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_bookmarks,menu)

        // Observe view model bookmarks sorting state
        viewModel.sorting.observe(viewLifecycleOwner, {
            when(it) {
                BookmarkSorting.NEWEST -> menu.findItem(R.id.action_sort_newest).isChecked = true
                BookmarkSorting.OLDEST -> menu.findItem(R.id.action_sort_oldest).isChecked = true
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // sort bookmarks according to selection
        return when(item.itemId) {
            R.id.action_sort_newest -> {
                if (!item.isChecked) {
                    viewModel.sortBookmarks(BookmarkSorting.NEWEST)
                }

                true
            }

            R.id.action_sort_oldest -> {
                if (!item.isChecked) {
                    viewModel.sortBookmarks(BookmarkSorting.OLDEST)
                }

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun navigateToArticle(headline: Headline) {
        val bundle = bundleOf(getString(R.string.key_article_id) to headline.id)
        findNavController().navigate(R.id.articleActivity, bundle)
    }
}
