package com.diskin.alon.movieguide.news.presentation.util

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.news.appservices.data.HeadlineDto
import com.diskin.alon.movieguide.news.presentation.data.Headline
import io.reactivex.Observable

class BookmarksModelRequestMapper(
    private val mapper: Mapper<HeadlineDto,Headline>
) : Mapper<Observable<Result<List<HeadlineDto>>>,Observable<Result<List<Headline>>>> {

    override fun map(source: Observable<Result<List<HeadlineDto>>>): Observable<Result<List<Headline>>> {
        return source
            .map { result ->
                when(result) {
                    is Result.Success -> {
                        Result.Success(
                            result.data.map { headlineDto ->
                                mapper.map(headlineDto)
                            }
                        )
                    }

                    is Result.Error -> Result.Error(result.error)
                }
            }
    }
}