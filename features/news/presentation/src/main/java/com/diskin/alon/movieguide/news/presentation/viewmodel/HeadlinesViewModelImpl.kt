package com.diskin.alon.movieguide.news.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.rxjava2.cachedIn
import com.diskin.alon.movieguide.common.appservices.UseCase
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.common.presentation.RxViewModel
import com.diskin.alon.movieguide.news.appservices.data.HeadlineDto
import com.diskin.alon.movieguide.news.appservices.data.HeadlinesRequest
import com.diskin.alon.movieguide.news.presentation.data.Headline
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers

class HeadlinesViewModelImpl(
    useCase: UseCase<HeadlinesRequest, Observable<PagingData<HeadlineDto>>>,
    private val pagingMapper: Mapper<PagingData<HeadlineDto>, PagingData<Headline>>
) : RxViewModel(), HeadlinesViewModel {

    companion object {
        const val PAGE_SIZE = 20
    }

    private val _headlines = MutableLiveData<PagingData<Headline>>()
    override val headlines: LiveData<PagingData<Headline>> get() = _headlines

    init {
        // Subscribe to headlines paging data updates, and update
        // live data headlines state, upon use case updates
        val headlinesSubscription = useCase.execute(
            HeadlinesRequest(PagingConfig(pageSize = PAGE_SIZE))
        )
            .cachedIn(viewModelScope)
            .subscribeOn(AndroidSchedulers.mainThread())
            .map(pagingMapper::map)
            .subscribe { _headlines.value = it }

        addSubscription(headlinesSubscription)
    }
}
