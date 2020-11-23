package com.diskin.alon.movieguide.news.presentation.viewmodel

import androidx.lifecycle.LiveData
import com.diskin.alon.movieguide.common.presentation.ViewData
import com.diskin.alon.movieguide.news.appservices.data.BookmarkSorting
import com.diskin.alon.movieguide.news.presentation.data.Headline

interface BookmarksViewModel {

    val bookmarks: LiveData<ViewData<List<Headline>>>

    val sorting: LiveData<BookmarkSorting>

    fun sortBookmarks(sorting: BookmarkSorting)
}