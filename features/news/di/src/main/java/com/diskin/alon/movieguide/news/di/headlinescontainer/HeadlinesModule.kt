package com.diskin.alon.movieguide.news.di.headlinescontainer

import androidx.paging.PagingData
import com.diskin.alon.movieguide.common.appservices.UseCase
import com.diskin.alon.movieguide.common.presentation.Model
import com.diskin.alon.movieguide.common.presentation.ModelDispatcher
import com.diskin.alon.movieguide.common.presentation.ModelRequest
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.news.appservices.data.HeadlineDto
import com.diskin.alon.movieguide.news.appservices.usecase.GetHeadlinesUseCase
import com.diskin.alon.movieguide.news.appservices.util.HeadlinesDtoPagingMapper
import com.diskin.alon.movieguide.news.domain.ArticleEntity
import com.diskin.alon.movieguide.news.presentation.data.Headline
import com.diskin.alon.movieguide.news.presentation.data.HeadlinesModelRequest
import com.diskin.alon.movieguide.news.presentation.util.HeadlinesModelDispatcher
import com.diskin.alon.movieguide.news.presentation.util.HeadlinesPagingMapper
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import io.reactivex.Observable

@Module
@InstallIn(ViewModelComponent::class)
abstract class HeadlinesModule {

    companion object {

        @HeadlinesModelDispatcher
        @Provides
        fun provideModelDispatcherMap(
            getHeadlinesUseCase: GetHeadlinesUseCase,
            headlinesMapper: Mapper<Observable<PagingData<HeadlineDto>>, Observable<PagingData<Headline>>>
        ): Model {
            val map = HashMap<Class<out ModelRequest<*, *>>,Pair<UseCase<*, *>, Mapper<*, *>?>>()
            map[HeadlinesModelRequest::class.java] = Pair(getHeadlinesUseCase,headlinesMapper)

            return ModelDispatcher(map)
        }
    }

    @Binds
    abstract fun bindHeadlinesDtoPagingMapper(mapper: HeadlinesDtoPagingMapper): Mapper<PagingData<ArticleEntity>, PagingData<HeadlineDto>>

    @Binds
    abstract fun bindHeadlinesPagingMapper(mapper: HeadlinesPagingMapper): Mapper<Observable<PagingData<HeadlineDto>>, Observable<PagingData<Headline>>>
}