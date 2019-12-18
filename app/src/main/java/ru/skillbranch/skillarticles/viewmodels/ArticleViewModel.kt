package ru.skillbranch.skillarticles.viewmodels

import androidx.lifecycle.LiveData
import ru.skillbranch.skillarticles.data.ArticleData
import ru.skillbranch.skillarticles.data.NetworkDataHolder.content
import ru.skillbranch.skillarticles.data.repositories.ArticleRepository

class ArticleViewModel: BaseViewModel<ArticleState>( ArticleState()) {
    private val repository = ArticleRepository

    init {
//        subscribeOnDataSource(getArticleData()) {
//            article ?: return@subscribeOnDataSource
//            state.copy(
//                shareLink = article.shareLink,
//                title = article.title,
//                category = article.category,
//                categoryIcon = article.categoryIcon,
//                date = article.date.format()
//            )
//        }
//
//        subscribeOnDataSource(getArticleContent()) { content, state ->
//            content ?: return@subscribeOnDataSource null
//            state.copy(
//                isLoadingContent = false,
//                content = content
//            )
//        }
//
//        subscribeOnDataSource(getArticlePersonalInfo()) { info, state ->
////            content ?: return@subscribeOnDataSource null
////            state.copy(
////                isBookmark = info.isBookMark,
////                isLike = info.isLike
////            )
//        }
    }


//    private fun getArticleData(): LiveData<ArticleData?> {
//        //return repository.getArticle(articleId)
//    }

    // app settings
    fun handleNightMode() {

    }

    fun handleUpText() {
        //repository.updateSettings(currentState.toAppSettings().copy(isBigText))
    }


}

data class ArticleState(
    val text: String? = null
)