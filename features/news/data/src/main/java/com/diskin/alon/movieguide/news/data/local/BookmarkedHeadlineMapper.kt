package com.diskin.alon.movieguide.news.data.local

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.news.domain.HeadlineEntity

class BookmarkedHeadlineMapper : Mapper<List<Bookmark>, Result<List<HeadlineEntity>>> {
    override fun map(source: List<Bookmark>): Result<List<HeadlineEntity>> {
        return Result.Success(
            source.map {
                HeadlineEntity(
                    it.articleId,
                    it.title,
                    it.date,
                    it.imageUrl,
                    it.articleUrl
                )
            }
        )
    }
}