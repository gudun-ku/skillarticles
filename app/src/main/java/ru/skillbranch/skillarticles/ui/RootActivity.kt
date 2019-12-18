package ru.skillbranch.skillarticles.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_root.*
import kotlinx.android.synthetic.main.layout_bottombar.*
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.extensions.dpToIntPx
import ru.skillbranch.skillarticles.viewmodels.ArticleState
import ru.skillbranch.skillarticles.viewmodels.ArticleViewModel
import ru.skillbranch.skillarticles.viewmodels.Notify
import ru.skillbranch.skillarticles.viewmodels.ViewModelFactory

class RootActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)

        setupToolbar()

        btn_like.setOnClickListener {
            Snackbar.make(coordinator_container,"test", Snackbar.LENGTH_LONG).show()
        }

//        val vmFactory = ViewModelFactory("0")
//        viewModel = ViewModelProvider.of(this,vmFactory).get(ArticleViewModel::class.java)
//
//        viewModel.observeNotifications(this) {
//            renderNotification(it)
//        }

    }

    private fun renderNotification(notify: Notify) {
        //TODO
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDefaultDisplayHomeAsUpEnabled(true)
        var logo = if(toolbar.childCount > 2) toolbar.getChildAt(2) as ImageView else null
        logo?.scaleType = ImageView.ScaleType.CENTER_CROP
        val lp = logo?.layoutParams as? Toolbar.LayoutParams
        lp?.let {
            it.width = this.dpToIntPx(40)
            it.height = this.dpToIntPx(40)
            it.marginEnd = this.dpToIntPx(16)
            logo?.layoutParams = it
        }



//        btn_like.setOnClickListener{
//            Snackbar.make(coordinator_container, "test", Snackbar.LENGTH_LONG)
//        }
//
//        switchMode.setOnSwitchListener {
//
//        }
    }

    private fun setupSubmenu() {

    }

    private fun setupBottombar() {

    }

    private fun renderUi(data:ArticleState) {

    }

}
