package ru.skillbranch.skillarticles.viewmodels

import androidx.lifecycle.LiveData
import ru.skillbranch.skillarticles.data.ArticleData
import ru.skillbranch.skillarticles.data.ArticlePersonalInfo
import ru.skillbranch.skillarticles.data.repositories.ArticleRepository
import ru.skillbranch.skillarticles.extensions.data.toAppSettings
import ru.skillbranch.skillarticles.extensions.data.toArticlePersonalInfo
import ru.skillbranch.skillarticles.extensions.format

class ArticleViewModel(private val articleId: String): BaseViewModel<ArticleState>( ArticleState()) {
    private val repository = ArticleRepository
    private var menuIsShown:Boolean = false

    init {
        // subscribe on mutable data
        subscribeOnDataSource(getArticleData()) { article, state  ->
            article ?: return@subscribeOnDataSource null
            state.copy (
                shareLink = article.shareLink,
                title = article.title,
                category = article.category,
                categoryIcon = article.categoryIcon,
                date = article.date.format()
            )
        }

        subscribeOnDataSource(getArticleContent()) { content, state ->
            content ?: return@subscribeOnDataSource null
            state.copy (
                isLoadingContent = false,
                content = content
            )
        }

        subscribeOnDataSource(getArticlePersonalInfo()) {info, state ->
            info ?: return@subscribeOnDataSource null
            state.copy(
                isBookmark =  info.isBookmark,
                isLike = info.isLike
            )
        }

        // subscribe on settings
        subscribeOnDataSource(repository.getAppSettings()) {settings, state ->
            state.copy(
                isDarkMode = settings.isDarkMode,
                isBigText = settings.isBigText
            )
        }
    }

    // load text from network
    private fun getArticleContent(): LiveData<List<Any>?> {
        return repository.loadArticleContent(articleId)
    }

    // load data fro mdb
    private fun getArticleData(): LiveData<ArticleData?> {
        return repository.getArticle(articleId)
    }

    // load data from db
    private fun getArticlePersonalInfo(): LiveData<ArticlePersonalInfo?> {
        return repository.loadArticlePersonalInfo(articleId)
    }

    // session state
    fun handleToggleMenu() {
        updateState { state ->
            state.copy(isShowMenu = !state.isShowMenu).also { menuIsShown = !state.isShowMenu }
        }
    }

    // app settings
    fun handleNightMode() {
        val settings = currentState.toAppSettings()
        repository.updateSettings(settings.copy(isDarkMode = !settings.isDarkMode))
    }

    fun handleUpText() {
        repository.updateSettings(currentState.toAppSettings().copy(isBigText = true))
    }

    fun handleDownText() {
        repository.updateSettings(currentState.toAppSettings().copy(isBigText = false))
    }

    fun handleBookmark() {
        val toggleBookmark = {
            val info = currentState.toArticlePersonalInfo()
            repository.updateArticlePersonalInfo(info.copy(isBookmark = !info.isBookmark))
        }
        toggleBookmark()
    }

    fun handleLike() {
        val toggleLike = {
            val info = currentState.toArticlePersonalInfo()
            repository.updateArticlePersonalInfo(info.copy(isLike = !info.isLike))
        }

        toggleLike()

        val msg = if(currentState.isLike) Notify.TextMessage("Mark is liked")
        else {
            Notify.ActionMessage(
                "Don't like it anymore",    //message
                "No, still like it",    //action label
                toggleLike                       //lambda
            )
        }
        notify(msg)
    }

    // not implemented
    fun handleShare() {
        val msg = "Share is not implemented"
        notify(Notify.ErrorMessage(msg,"OK",null))
    }

    fun hideMenu() {
        updateState { it.copy(isShowMenu = false) }
    }

    fun showMenu() {
        updateState { it.copy(isShowMenu = menuIsShown) }
    }

}

data class ArticleState(
    val isAuth: Boolean = false, //пользователь авторизован
    val isLoadingContent: Boolean = true, //content загружается
    val isLoadingReviews: Boolean = true, //отзывы загружаются
    val isLike: Boolean = false, //лайкнуто
    val isBookmark: Boolean = false, //в закладках
    val isShowMenu: Boolean = false,
    val isBigText: Boolean = false,
    val isDarkMode: Boolean = false, //темный режим
    val isSearch: Boolean = false, //режим поиска
    val searchQuery: String? = null, //поисковый запрос
    val searchResults: List<Pair<Int, Int>> = emptyList(), //результаты поиска (стартовая и конечная позиции)
    val searchPosition: Int = 0, //текущая позиция найденного результата
    val shareLink: String? = null, //ссылка Share
    val title: String? = null, //заголовок статьи
    val category: String? = null, //категория
    val categoryIcon: Any? = null, //иконка категории
    val date: String? = null, //дата публикации
    val author: Any? = null,//автор статьи
    val poster: String? = null, //обложка статьи
    val content: List<Any> = emptyList(),//контент
    val reviews: List<Any> = emptyList() //отзывы
)