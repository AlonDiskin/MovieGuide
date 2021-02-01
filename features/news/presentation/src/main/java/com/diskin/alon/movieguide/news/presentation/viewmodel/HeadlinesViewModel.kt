package com.diskin.alon.movieguide.news.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.rxjava2.cachedIn
import com.diskin.alon.movieguide.common.presentation.Model
import com.diskin.alon.movieguide.common.presentation.RxViewModel
import com.diskin.alon.movieguide.news.presentation.data.Headline
import com.diskin.alon.movieguide.news.presentation.data.HeadlinesModelRequest
import com.diskin.alon.movieguide.news.presentation.util.HeadlinesModelDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import javax.inject.Inject

/**
 * Stores and manage UI related data for the headlines UI controller.
 */
@HiltViewModel
class HeadlinesViewModel @Inject constructor(
    @HeadlinesModelDispatcher private val model: Model
) : RxViewModel() {

    companion object {
        const val PAGE_SIZE = 20
    }

    private val _headlines = MutableLiveData<PagingData<Headline>>()
    val headlines: LiveData<PagingData<Headline>> get() = _headlines

    init {
        addSubscription(createHeadlinesPagingSubscription())
    }

    private fun createHeadlinesPagingSubscription(): Disposable {
        val modelRequest = HeadlinesModelRequest(PagingConfig(pageSize = PAGE_SIZE))
        return model.execute(modelRequest)
            .cachedIn(viewModelScope)
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe { _headlines.value = it }
    }
}
