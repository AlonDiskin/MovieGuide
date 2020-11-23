package com.diskin.alon.movieguide.news.di.feature

import androidx.paging.PagingData
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.news.appservices.data.ArticleDto
import com.diskin.alon.movieguide.news.appservices.data.HeadlineDto
import com.diskin.alon.movieguide.news.appservices.util.ArticleDtoMapper
import com.diskin.alon.movieguide.news.appservices.util.HeadlinesDtoPagingMapper
import com.diskin.alon.movieguide.news.appservices.util.HeadlinesMapper
import com.diskin.alon.movieguide.news.domain.ArticleEntity
import com.diskin.alon.movieguide.news.domain.HeadlineEntity
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class NewsAppServiceModule {

    @Module
    companion object {

        @JvmStatic
        @Provides
        fun provideHeadlinesMapper(): Mapper<Result<List<HeadlineEntity>>, Result<List<HeadlineDto>>> {
            return HeadlinesMapper()
        }
    }

    @Binds
    abstract fun bindArticleDtoMapper(mapper: ArticleDtoMapper): Mapper<Result<ArticleEntity>, Result<ArticleDto>>

    @Binds
    abstract fun bindHeadlinesDtoPagingMapper(mapper: HeadlinesDtoPagingMapper): Mapper<PagingData<HeadlineEntity>, PagingData<HeadlineDto>>
}