package com.diskin.alon.movieguide.news.presentation.viewmodel

import androidx.lifecycle.LiveData
import com.diskin.alon.movieguide.common.presentation.ErrorViewData
import com.diskin.alon.movieguide.common.presentation.UpdateViewData
import com.diskin.alon.movieguide.common.presentation.ViewData
import com.diskin.alon.movieguide.news.appservices.data.BookmarkSorting
import com.diskin.alon.movieguide.news.presentation.data.Headline

interface BookmarksViewModel {

    val bookmarks: LiveData<List<Headline>>

    val sorting: LiveData<BookmarkSorting>

    val error: LiveData<ErrorViewData>

    val update: LiveData<UpdateViewData>

    fun sortBookmarks(sorting: BookmarkSorting)

    fun removeBookmarks(ids: List<String>)
}