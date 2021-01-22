package com.diskin.alon.movieguide.news.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.diskin.alon.movieguide.common.appservices.AppError
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.presentation.ErrorViewData
import com.diskin.alon.movieguide.common.presentation.Model
import com.diskin.alon.movieguide.common.presentation.RxViewModel
import com.diskin.alon.movieguide.common.presentation.UpdateViewData
import com.diskin.alon.movieguide.news.presentation.data.Article
import com.diskin.alon.movieguide.news.presentation.data.ArticleModelRequest
import com.diskin.alon.movieguide.news.presentation.data.BookmarkingModelRequest
import com.diskin.alon.movieguide.news.presentation.data.UnBookmarkingModelRequest
import com.diskin.alon.movieguide.news.presentation.util.ArticleModelDispatcher
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

/**
 * Stores and manage UI related data for the article UI controller.
 */
class ArticleViewModelImpl constructor(
    @ArticleModelDispatcher private val model: Model,
    private val stateHandle: SavedStateHandle,
) : RxViewModel(), ArticleViewModel {

    companion object { const val KEY_ARTICLE_ID = "article_id" }

    private val bookmarkingSubject =  BehaviorSubject.create<Pair<String,Boolean>>()
    private val idSubject = BehaviorSubject.createDefault(getArticleId())
    private val _article = MutableLiveData<Article>()
    override val article: LiveData<Article> get() = _article
    private val _error = MutableLiveData<ErrorViewData>()
    override val error: LiveData<ErrorViewData> get() = _error
    private val _update = MutableLiveData<UpdateViewData>(UpdateViewData.Update)
    override val update: LiveData<UpdateViewData> get() = _update

    init {
        addSubscription(createArticleSubscription())
        addSubscription(createBookmarkingSubscription())
    }

    override fun bookmark() {
        bookmarkingSubject.onNext(Pair(getArticleId(),true))
    }

    override fun unBookmark() {
        bookmarkingSubject.onNext(Pair(getArticleId(),false))
    }

    private fun createBookmarkingSubscription(): Disposable {
        return bookmarkingSubject
            .switchMapSingle { pair ->
                val request = when(pair.second) {
                    true -> BookmarkingModelRequest(pair.first)
                    false -> UnBookmarkingModelRequest(listOf(pair.first))
                }

                model.execute(request)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::handleModelBookmarkResult)
    }

    private fun handleModelBookmarkResult(result: Result<Unit>) {
        _error.value = ErrorViewData.NoError
        if (result is Result.Error) handleModelBookmarkingError(result.error)
    }

    private fun handleModelBookmarkingError(error: AppError) {
        _error.value = when(error.retriable) {
            true -> ErrorViewData.Retriable(error.description,::retryModelArticleBookmarking)
            false -> ErrorViewData.NotRetriable(error.description)
        }
    }

    private fun getArticleId(): String {
        return stateHandle.get<String>(KEY_ARTICLE_ID) ?:
        throw IllegalArgumentException("must pass article id arg to ArticleViewModel stateHandle!")
    }

    private fun createArticleSubscription(): Disposable {
        return idSubject.switchMap { model.execute(ArticleModelRequest(it)) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::handleArticleModelResult)
    }

    private fun handleArticleModelResult(result: Result<Article>) {
        _update.value = UpdateViewData.EndUpdate
        _error.value = ErrorViewData.NoError
        when(result) {
            is Result.Success -> _article.value = result.data
            is Result.Error -> handleModelArticleError(result.error)
        }
    }

    private fun handleModelArticleError(error: AppError) {
        _error.value = when(error.retriable) {
            true -> ErrorViewData.Retriable(error.description,::retryModelArticleUpdate)
            false -> ErrorViewData.NotRetriable(error.description)
        }
    }

    private fun retryModelArticleUpdate() {
        _update.value = UpdateViewData.Update
        idSubject.onNext(getArticleId())
    }

    private fun retryModelArticleBookmarking() {
        bookmarkingSubject.value?.let {
            _update.value = UpdateViewData.Update
            bookmarkingSubject.onNext(it)
        }
    }
}