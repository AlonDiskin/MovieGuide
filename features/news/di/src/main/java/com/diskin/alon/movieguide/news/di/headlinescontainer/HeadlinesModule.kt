package com.diskin.alon.movieguide.news.di.headlinescontainer

import androidx.lifecycle.ViewModelProvider
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
import com.diskin.alon.movieguide.news.presentation.controller.HeadlinesFragment
import com.diskin.alon.movieguide.news.presentation.data.Headline
import com.diskin.alon.movieguide.news.presentation.data.HeadlinesModelRequest
import com.diskin.alon.movieguide.news.presentation.util.HeadlineMapper
import com.diskin.alon.movieguide.news.presentation.util.HeadlinesPagingMapper
import com.diskin.alon.movieguide.news.presentation.viewmodel.HeadlinesViewModel
import com.diskin.alon.movieguide.news.presentation.viewmodel.HeadlinesViewModelImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import io.reactivex.Observable

@Module
abstract class HeadlinesModule {

    @Module
    companion object {

        @JvmStatic
        @Provides
        fun provideModelDispatcher(
            getHeadlinesUseCase: GetHeadlinesUseCase,
            headlinesMapper: Mapper<Observable<PagingData<HeadlineDto>>, Observable<PagingData<Headline>>>
        ): Model {
            val map = HashMap<Class<out ModelRequest<*, *>>,Pair<UseCase<*, *>, Mapper<*, *>?>>()
            map[HeadlinesModelRequest::class.java] = Pair(getHeadlinesUseCase,headlinesMapper)

            return ModelDispatcher(map)
        }

        @JvmStatic
        @Provides
        fun provideHeadlinesViewModel(
            fragment: HeadlinesFragment,
            factory: HeadlinesViewModelFactory
        ): HeadlinesViewModel {
            return ViewModelProvider(fragment, factory)
                .get(HeadlinesViewModelImpl::class.java)
        }
    }

    @Binds
    abstract fun bindHeadlinesDtoPagingMapper(mapper: HeadlinesDtoPagingMapper): Mapper<PagingData<ArticleEntity>, PagingData<HeadlineDto>>

    @Binds
    abstract fun bindsHeadlineMapper(mapper: HeadlineMapper): Mapper<HeadlineDto,Headline>

    @Binds
    abstract fun bindHeadlinesPagingMapper(mapper: HeadlinesPagingMapper): Mapper<Observable<PagingData<HeadlineDto>>, Observable<PagingData<Headline>>>
}