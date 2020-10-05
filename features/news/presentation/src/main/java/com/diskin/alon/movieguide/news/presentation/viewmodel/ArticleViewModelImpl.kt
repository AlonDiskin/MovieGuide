package com.diskin.alon.movieguide.news.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.diskin.alon.movieguide.common.appservices.UseCase
import com.diskin.alon.movieguide.common.presentation.RxViewModel
import com.diskin.alon.movieguide.news.appservices.model.ArticleDto
import com.diskin.alon.movieguide.news.appservices.model.ArticleRequest
import com.diskin.alon.movieguide.news.presentation.model.Article
import io.reactivex.Observable

class ArticleViewModelImpl(
    useCase: UseCase<ArticleRequest,Observable<ArticleDto>>,
    stateHandle: SavedStateHandle
) : RxViewModel(), ArticleViewModel {

    companion object {
        const val KEY_ARTICLE_ID = "article_id"
    }

    private val _article = MutableLiveData<Article>()
    override val article: LiveData<Article> get() = _article

    init {
        requireNotNull(stateHandle.get<String>(KEY_ARTICLE_ID))
        { "must pass article id arg to ArticleViewModel stateHandle!"}
        val articleId = stateHandle.get<String>(KEY_ARTICLE_ID)!!

        val subscription = useCase.execute(ArticleRequest(articleId))
            .map(::mapArticleDto)
            .subscribe { _article.value = it }

        this.disposable.add(subscription)
    }
}