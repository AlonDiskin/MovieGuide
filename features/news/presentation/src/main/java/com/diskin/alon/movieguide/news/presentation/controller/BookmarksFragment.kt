package com.diskin.alon.movieguide.news.presentation.controller

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ShareCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.diskin.alon.movieguide.common.presentation.ErrorViewData
import com.diskin.alon.movieguide.common.presentation.UpdateViewData
import com.diskin.alon.movieguide.common.presentation.setCustomDecoration
import com.diskin.alon.movieguide.news.appservices.data.BookmarkSorting
import com.diskin.alon.movieguide.news.presentation.R
import com.diskin.alon.movieguide.news.presentation.data.Headline
import com.diskin.alon.movieguide.news.presentation.viewmodel.BookmarksViewModel
import com.google.android.material.color.MaterialColors
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.migration.OptionalInject
import kotlinx.android.synthetic.main.fragment_bookmarks.*

@OptionalInject
@AndroidEntryPoint
class BookmarksFragment : Fragment(), ActionMode.Callback{

    companion object {
        private const val KEY_ACTION_MODE = "action_mode"
        private const val KEY_SELECTED = "selected"
    }

    private val viewModel: BookmarksViewModel by viewModels()
    private var errorSnackbar: Snackbar? = null
    private val selectedBookmarksIds: MutableList<String> = mutableListOf()
    private var actionMode: ActionMode? = null
    private var multiSelect: Boolean = false
    private lateinit var adapter: BookmarksAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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

        // Restore pres state if needed,before adapter setup
        savedInstanceState?.let {
            if (it.getBoolean(KEY_ACTION_MODE)) {
                @Suppress("UNCHECKED_CAST")
                this.selectedBookmarksIds.addAll(
                    (it.getStringArray(KEY_SELECTED) as Array<String>).toMutableList()
                )
                this.actionMode = activity?.startActionMode(this)
            }
        }

        // Setup bookmarked headlines adapter
        adapter = BookmarksAdapter(
            this::onBookmarkClick,
            this::onLongBookmarkClick,
            this::onBookmarkOptionsClick,
            selectedBookmarksIds,
            multiSelect
        )
        bookmarked_articles.adapter = adapter

        setCustomDecoration(
            requireContext(),
            bookmarked_articles,
            requireContext().resources.getInteger(R.integer.headlines_span)
        )

        MaterialColors.getColor(requireActivity(), R.attr.colorSurface, Color.GREEN)


        // Observe bookmarks state from view model
        viewModel.bookmarks.observe(viewLifecycleOwner,
            { headlines -> adapter.submitList(headlines) })

        // Observe view model update state
        viewModel.update.observe(viewLifecycleOwner, { update ->
            when (update) {
                is UpdateViewData.Update -> progress_bar?.visibility = View.VISIBLE
                is UpdateViewData.EndUpdate -> progress_bar?.visibility = View.GONE
            }
        })

        // Observe view model error state
        viewModel.error.observe(viewLifecycleOwner) { error ->
            when(error) {
                is ErrorViewData.NoError -> errorSnackbar?.dismiss()
                is ErrorViewData.NotRetriable -> showNotRetriableError(error)
                is ErrorViewData.Retriable -> showRetriableError(error)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(KEY_ACTION_MODE, this.multiSelect)
        outState.putStringArray(KEY_SELECTED, selectedBookmarksIds.toTypedArray())

        super.onSaveInstanceState(outState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_bookmarks, menu)

        // Observe view model bookmarks sorting state
        viewModel.sorting.observe(viewLifecycleOwner, { sorting ->
            when (sorting) {
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

    private fun showNotRetriableError(error: ErrorViewData.NotRetriable) {
        errorSnackbar = Snackbar.make(
            bookmarked_articles,
            error.reason,
            Snackbar.LENGTH_INDEFINITE
        )

        errorSnackbar?.show()
    }

    private fun showRetriableError(error: ErrorViewData.Retriable) {
        errorSnackbar = Snackbar.make(
            bookmarked_articles,
            error.reason,
            Snackbar.LENGTH_INDEFINITE
        )
            .setAction(getString(R.string.action_retry)) { error.retry() }

        errorSnackbar?.show()
    }

    private fun onBookmarkClick(headline: Headline, view: View) {
        if(multiSelect) {
            if (selectedBookmarksIds.contains(headline.id)) {
                selectedBookmarksIds.remove(headline.id)
                view.isSelected = false
                view.findViewById<View>(R.id.selected_background).setBackgroundColor(Color.TRANSPARENT)

            } else {
                selectedBookmarksIds.add(headline.id)
                view.findViewById<View>(R.id.selected_background).setBackgroundColor(Color.LTGRAY)
            }

            if (selectedBookmarksIds.isEmpty()) {
                actionMode?.finish()
            }

        } else {
            val bundle = bundleOf(getString(R.string.key_article_id) to headline.id)
            findNavController().navigate(R.id.articleActivity, bundle)
        }
    }

    private fun onLongBookmarkClick(headline: Headline, view: View): Boolean {
        if (!multiSelect) {
            actionMode =  activity?.startActionMode(this)
            selectedBookmarksIds.add(headline.id)
            view.findViewById<View>(R.id.selected_background)?.setBackgroundColor(Color.LTGRAY)
        }

        return true
    }

    private fun removeSelectedBookmarks() {
        AlertDialog.Builder(requireActivity())
            .setMessage(getString(R.string.remove_bookamrks_dialog_message))
            .setTitle(getString(R.string.remove_bookmarks_dialog_title))
            .setPositiveButton(getString(R.string.dialog_pos_label)) { _, _ ->
                viewModel.removeBookmarks(selectedBookmarksIds.toList())
                actionMode?.finish()
            }
            .setNegativeButton(getString(R.string.dialog_neg_label), null)
            .create()
            .show()
    }

    private fun onBookmarkOptionsClick(headline: Headline, view: View) {
        PopupMenu(requireActivity(), view).apply {
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_share_bookmark -> {
                        shareBookmark(headline)
                        true
                    }
                    R.id.action_delete_bookmark -> {
                        removeBookmark(headline)
                        true
                    }
                    else -> false
                }
            }
            inflate(R.menu.menu_bookmark)
            show()
        }
    }

    private fun shareBookmark(headline: Headline) {
        activity?.let {
            ShareCompat.IntentBuilder
                .from(it)
                .setType(getString(R.string.mime_type_text))
                .setText(headline.articleUrl)
                .setChooserTitle(getString(R.string.title_share_bookmark_chooser))
                .startChooser()
        }
    }

    private fun removeBookmark(headline: Headline) {
        AlertDialog.Builder(requireActivity())
            .setMessage(getString(R.string.remove_bookamrk_dialog_message))
            .setTitle(getString(R.string.remove_bookmark_dialog_title))
            .setPositiveButton(getString(R.string.dialog_pos_label)) { _, _ ->
                viewModel.removeBookmarks(listOf(headline.id))
                actionMode?.finish()
            }
            .setNegativeButton(getString(R.string.dialog_neg_label), null)
            .create()
            .show()
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        multiSelect = true

        if (this::adapter.isInitialized) {
            adapter.isMultiSelect = true
        }

        menu?.add(getString(R.string.title_action_remove_bookmark))?.setIcon(
            R.drawable.ic_delete_24
        )

        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return false
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        when(item?.title) {
            getString(R.string.title_action_remove_bookmark) -> removeSelectedBookmarks()
        }

        return true
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        this.multiSelect = false

        if (this::adapter.isInitialized) {
            adapter.isMultiSelect = false

            for (i in 0 until adapter.itemCount) {
                if (selectedBookmarksIds.contains(this.adapter.currentList[i].id)) {
                    this.adapter.notifyItemChanged(i)
                }
            }
        }

        selectedBookmarksIds.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (multiSelect) {
            actionMode?.finish()
        }

        errorSnackbar?.dismiss()
    }
}
