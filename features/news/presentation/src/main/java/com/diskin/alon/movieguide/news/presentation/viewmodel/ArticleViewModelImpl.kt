package com.diskin.alon.movieguide.news.presentation.viewmodel

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.appservices.UseCase
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.common.presentation.LoadState
import com.diskin.alon.movieguide.common.presentation.RxViewModel
import com.diskin.alon.movieguide.news.appservices.data.ArticleDto
import com.diskin.alon.movieguide.news.appservices.data.ArticleRequest
import com.diskin.alon.movieguide.news.presentation.R
import com.diskin.alon.movieguide.news.presentation.data.Article
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.BehaviorSubject

class ArticleViewModelImpl(
    useCase: UseCase<ArticleRequest,Observable<Result<ArticleDto>>>,
    mapper: Mapper<ArticleDto, Article>,
    private val stateHandle: SavedStateHandle,
    private val resources: Resources
) : RxViewModel(), ArticleViewModel {

    private val idSubject = BehaviorSubject.createDefault(getArticleIdState())
    private val _article = MutableLiveData<Article>()
    override val article: LiveData<Article> get() = _article
    private val _loading = MutableLiveData<LoadState>(LoadState.Loading)
    override val loading: LiveData<LoadState> get() = _loading

    init {
        val articleSubscription = idSubject
            .switchMap { useCase.execute(ArticleRequest(it)) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                when(it) {
                    is Result.Success -> {
                        _article.value = mapper.map(it.data)
                        _loading.value = LoadState.Success
                    }

                    is Result.Error -> _loading.value = LoadState.Error(it.error)
                }
            }

        addSubscription(articleSubscription)
    }

    override fun reload() {
        _loading.value = LoadState.Loading
        idSubject.onNext(getArticleIdState())
    }

    private fun getArticleIdState(): String {
        return stateHandle.get<String>(resources.getString(R.string.key_article_id)) ?:
        throw IllegalArgumentException("must pass article id arg to ArticleViewModel stateHandle!")
    }
}