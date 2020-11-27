package com.diskin.alon.movieguide.news.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.diskin.alon.movieguide.common.appservices.AppError
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.presentation.Model
import com.diskin.alon.movieguide.common.presentation.RxViewModel
import com.diskin.alon.movieguide.common.presentation.ViewData
import com.diskin.alon.movieguide.common.presentation.ViewDataError
import com.diskin.alon.movieguide.news.presentation.data.Article
import com.diskin.alon.movieguide.news.presentation.data.ArticleModelRequest
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

/**
 * Stores and manage UI related data for the article UI controller.
 */
class ArticleViewModelImpl(
    private val model: Model,
    private val stateHandle: SavedStateHandle,
) : RxViewModel(), ArticleViewModel {

    companion object {
        const val KEY_ARTICLE_ID = "article_id"
    }

    private val idSubject = BehaviorSubject.createDefault(getArticleId())
    private val _article = MutableLiveData<ViewData<Article>>(ViewData.Updating())
    override val article: LiveData<ViewData<Article>> get() = _article

    init {
        addSubscription(createArticleSubscription())
    }

    private fun getArticleId(): String {
        return stateHandle.get<String>(KEY_ARTICLE_ID) ?:
        throw IllegalArgumentException("must pass article id arg to ArticleViewModel stateHandle!")
    }

    private fun createArticleSubscription(): Disposable {
        return idSubject.switchMap { model.execute(ArticleModelRequest(it)) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { handleArticleModelResult(it) }
    }

    private fun handleArticleModelResult(result: Result<Article>) {
        when(result) {
            is Result.Success -> handleModelArticleSuccessResult(result.data)
            is Result.Error -> handleModelArticleError(result.error)
        }
    }

    private fun handleModelArticleSuccessResult(article: Article) {
        _article.value = ViewData.Data(article)
    }

    private fun handleModelArticleError(error: AppError) {
        _article.value = if (error.retriable) {
            val retryErrorAction = {
                _article.value = ViewData.Updating(_article.value?.data)
                idSubject.onNext(getArticleId())
            }

             ViewData.Error(
                ViewDataError.Retriable(error.cause,retryErrorAction)
            )
        } else {
            ViewData.Error(
                ViewDataError.NotRetriable(error.cause)
            )
        }
    }
}