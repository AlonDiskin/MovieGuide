package com.diskin.alon.movieguide.news.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.rxjava2.cachedIn
import com.diskin.alon.movieguide.common.appservices.UseCase
import com.diskin.alon.movieguide.news.appservices.HeadlineDto
import com.diskin.alon.movieguide.news.appservices.HeadlinesRequest
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

class MoviesHeadlinesViewModelImpl(
    useCase: UseCase<HeadlinesRequest, Observable<PagingData<HeadlineDto>>>
) : ViewModel(), MoviesHeadlinesViewModel {

    companion object {
        const val PAGE_SIZE = 20
    }

    private val _headlines = MutableLiveData<PagingData<NewsHeadline>>()
    override val headlines: LiveData<PagingData<NewsHeadline>> get() = _headlines
    private val disposable = CompositeDisposable()

    init {
        // Subscribe to headlines paging data updates, and update
        // live data headlines state, upon use case updates
        val headlinesSubscription = useCase.execute(
            HeadlinesRequest(PagingConfig(pageSize = PAGE_SIZE))
        )
            .cachedIn(viewModelScope)
            .subscribeOn(AndroidSchedulers.mainThread())
            .map(::mapDtoPagingToNewsHeadline)
            .subscribe { _headlines.value = it }

        disposable.add(headlinesSubscription)
    }

    override fun onCleared() {
        super.onCleared()
        // Dispose all rx subscriptions
        if (!disposable.isDisposed) {
            disposable.dispose()
        }
    }
}
