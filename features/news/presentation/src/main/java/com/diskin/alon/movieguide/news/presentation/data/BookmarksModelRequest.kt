package com.diskin.alon.movieguide.news.presentation.data

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.presentation.ModelRequest
import com.diskin.alon.movieguide.news.appservices.data.BookmarkSorting
import com.diskin.alon.movieguide.news.appservices.data.BookmarksRequest
import io.reactivex.Observable

data class BookmarksModelRequest(
    val sorting: BookmarkSorting
) : ModelRequest<BookmarksRequest,Observable<Result<List<Headline>>>>(
    BookmarksRequest(sorting)
)