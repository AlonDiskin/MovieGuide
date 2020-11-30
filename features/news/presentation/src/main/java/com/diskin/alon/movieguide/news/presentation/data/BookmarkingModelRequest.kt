package com.diskin.alon.movieguide.news.presentation.data

import com.diskin.alon.movieguide.common.presentation.ModelRequest
import com.diskin.alon.movieguide.news.appservices.data.BookmarkArticleRequest
import com.diskin.alon.movieguide.common.appservices.Result
import io.reactivex.Single

data class BookmarkingModelRequest(
    val id: String
) : ModelRequest<BookmarkArticleRequest,Single<Result<Unit>>>(
    BookmarkArticleRequest(id)
)