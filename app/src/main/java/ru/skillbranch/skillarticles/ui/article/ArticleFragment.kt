package ru.skillbranch.skillarticles.ui.article

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat.invalidateOptionsMenu
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions.circleCropTransform
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_root.*
import kotlinx.android.synthetic.main.fragment_article.*
import kotlinx.android.synthetic.main.layout_bottombar.*
import kotlinx.android.synthetic.main.layout_search_view.*
import kotlinx.android.synthetic.main.layout_submenu.*
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.data.repositories.MarkdownElement
import ru.skillbranch.skillarticles.extensions.dpToIntPx
import ru.skillbranch.skillarticles.extensions.format
import ru.skillbranch.skillarticles.extensions.hideKeyboard
import ru.skillbranch.skillarticles.ui.base.BaseFragment
import ru.skillbranch.skillarticles.ui.base.Binding
import ru.skillbranch.skillarticles.ui.delegates.RenderProp
import ru.skillbranch.skillarticles.viewmodels.article.ArticleState
import ru.skillbranch.skillarticles.viewmodels.article.ArticleViewModel
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState
import ru.skillbranch.skillarticles.viewmodels.base.ViewModelFactory

class ArticleFragment : BaseFragment<ArticleViewModel>(), IArticleView{

    private val args: ArticleFragmentArgs by navArgs()
    override val viewModel: ArticleViewModel by viewModels {
        ViewModelFactory(
            owner = this,
            params = "0"
        )
    }

    override val layout: Int = R.layout.fragment_article

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public override val binding: ArticleBinding by lazy { ArticleBinding() }


    inner class ArticleBinding : Binding() {

        var isFocusedSearch:Boolean = false
        var searchQueryText = ""

        private var isLoadingContent by RenderProp(true)

        /*
        private var isLike: Boolean by RenderProp(false){ btn_like.isChecked = it }
        private var isBookMark: Boolean by RenderProp(false){ btn_bookmark.isChecked = it }
        private var isShowMenu: Boolean by RenderProp(false){
            btn_settings.isChecked = it
            //if (it) submenu.open() else submenu.close()
        }

        private var isBigText: Boolean by RenderProp(false) {
            if (it) {
                tv_text_content.textSize = 18f
                btn_text_up.isChecked = true
                btn_text_down.isChecked = false
            } else {
                tv_text_content.textSize = 14f
                btn_text_up.isChecked = false
                btn_text_down.isChecked = true
            }
        }

        private var isDarkMode: Boolean by RenderProp(false, false) {
            switch_mode.isChecked = it
            root.delegate.localNightMode = if (it) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        }
        */


        var isSearch: Boolean by RenderProp(false) {
            /*
            if (it) {
                showSearchBar()
                with(toolbar) {
                    (layoutParams as AppBarLayout.LayoutParams).scrollFlags =
                        AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL
                }
            } else {
                hideSearchBar()
                with(toolbar) {
                    (layoutParams as AppBarLayout.LayoutParams).scrollFlags =
                        AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                }
            }

             */
        }

        private var searchResults: List<Pair<Int, Int>> by RenderProp(emptyList())
        private var searchPosition: Int by RenderProp(0)

        private var content: List<MarkdownElement> by RenderProp(emptyList()) {
            tv_text_content.isLoading = it.isEmpty()
            tv_text_content.setContent(it)
            if (it.isNotEmpty()) setupCopyListener()
        }

        override val afterInflated: (() -> Unit)? =  {
            dependsOn<Boolean, Boolean, List<Pair<Int, Int>>, Int>(
                ::isLoadingContent,
                ::isSearch,
                ::searchResults,
                ::searchPosition
            ){ ilc, iss, sr, sp ->
                if (!ilc && iss) {
                    tv_text_content.renderSearchResult(sr)
                    tv_text_content.renderSearchPosition(sr.getOrNull(sp))
                }
                if (!ilc &&!iss) {
                    tv_text_content.clearSearchResult()
                }

                //bottombar.bindSearchInfo(sr.size, sp)
            }
        }

        override fun bind(data: IViewModelState) {
            data as ArticleState

            //            isLike = data.isLike
            //            isBookMark = data.isBookmark
            //            isShowMenu = data.isShowMenu
            //            isBigText = data.isBigText
            //            isDarkMode = data.isDarkMode

            content = data.content

            isLoadingContent = data.isLoadingContent
            isSearch = data.isSearch
            searchQueryText = data.searchQuery ?: ""
            searchPosition = data.searchPosition
            searchResults = data.searchResults

        }

        override fun saveUi(outState: Bundle){
            outState.putBoolean(::isFocusedSearch.name, search_view?.hasFocus() ?: false)
        }

        override fun restoreUi(savedState: Bundle?) {
            isFocusedSearch = savedState?.getBoolean(::isFocusedSearch.name) ?: false
        }
    }

    override fun setupViews() {
        setupBottombar()
        setupSubmenu()

        // init views
        val avatarSize = root.dpToIntPx(40)
        val cornerRadius = root.dpToIntPx(8)

        Glide.with(root)
            .load(args.authorAvatar)
            .apply(circleCropTransform())
            .override(avatarSize)
            .into(iv_author_avatar)

        Glide.with(root)
            .load(args.poster)
            .transform(CenterCrop(),RoundedCorners(cornerRadius))
            .into(iv_poster)

        tv_title.text = args.title
        tv_author.text = args.author
        tv_date.text = args.date.format()
    }

    override fun showSearchBar() {
    //   bottombar.setSearchState(true)
    //   scroll.setMarginOptionally(bottom = dpToIntPx(56))
    }

    override fun hideSearchBar() {
    //   bottombar.setSearchState(false)
    //   scroll.setMarginOptionally(bottom = dpToIntPx(0))
    }

    private fun setupCopyListener() {
    tv_text_content.setCopyListener { copy ->
            val clipboard = root.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Copied code", copy)
            clipboard.setPrimaryClip(clip)
            viewModel.handleCopyCode()
        }
    }


    //    private var title: String by RenderProp("loading") { toolbar.title = it }
    //    private var category: String by RenderProp("loading") { toolbar.subtitle = it }
    //    private var categoryIcon: Int by RenderProp(R.drawable.logo_placeholder) {
    //        toolbar.logo = getDrawable(requireContext())
    //    }

    private fun setupBottombar() {

        /*
        val searchView = findViewById<SearchView>(R.id.search_view)

        btn_like.setOnClickListener { viewModel.handleLike() }
        btn_bookmark.setOnClickListener { viewModel.handleBookmark() }
        btn_share.setOnClickListener { viewModel.handleShare() }
        btn_settings.setOnClickListener { viewModel.handleToggleMenu() }

        btn_result_up.setOnClickListener {
        if (!tv_text_content.hasFocus()) tv_text_content.requestFocus()
            root.hideKeyboard(btn_result_up)
            viewModel.handleUpResult()
        }

        btn_result_down.setOnClickListener {
            if (!tv_text_content.hasFocus()) tv_text_content.requestFocus()
            root.hideKeyboard(btn_result_down)
            viewModel.handleDownResult()
        }

        btn_search_close.setOnClickListener {
            viewModel.handleSearchMode(false)
            root.invalidateOptionsMenu()
        }

        */

    }

    private fun setupSubmenu() {
        /*
        btn_text_up.setOnClickListener{ viewModel.handleUpText() }
        btn_text_down.setOnClickListener{ viewModel.handleDownText() }
        switch_mode.setOnClickListener { viewModel.handleNightMode() }

        */
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //        if (item.itemId == android.R.id.home) {
        //            binding.isSearch = false
        //        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
    super.onPrepareOptionsMenu(menu)
        /*
        menuInflater.inflate(R.menu.menu_search, menu)
        val menuItem = menu?.findItem(R.id.action_search)
        val searchView = menuItem?.actionView as? SearchView
        searchView?.queryHint = getString(R.string.article_search_placeholder)

        if (binding.isSearch) {
        menuItem?.expandActionView()
        searchView?.setQuery(binding.searchQueryText, false)

        if (binding.isFocusedSearch) searchView?.requestFocus()
        else searchView?.clearFocus()
        }

        menuItem?.setOnActionExpandListener(object: MenuItem.OnActionExpandListener {
        override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
            viewModel.handleSearchMode(true)
            return true
        }

        override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
            viewModel.handleSearchMode(false)
            return true
        }
        })

        searchView?.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            viewModel.handleSearch(query)
            return true
        }

        override fun onQueryTextChange(queryText: String?): Boolean {
            binding.searchQueryText = queryText ?: ""
            viewModel.handleSearch(binding.searchQueryText)


            return true
        }

    })

    */

    }
}
