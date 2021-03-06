package com.sakurafish.pockettoushituryou.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.sakurafish.pockettoushituryou.R
import com.sakurafish.pockettoushituryou.databinding.ActivitySearchresultBinding
import com.sakurafish.pockettoushituryou.shared.ext.replaceFragment
import com.sakurafish.pockettoushituryou.view.fragment.SearchResultFragment
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class SearchResultActivity : AppCompatActivity(), HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    private lateinit var binding: ActivitySearchresultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val query = intent.getStringExtra(EXTRA_QUERY)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_searchresult)
        initView()
        replaceFragment(SearchResultFragment.newInstance(query), R.id.content_view)
    }

    private fun initView() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = getString(R.string.search_result_title) + " (" + intent.getStringExtra(EXTRA_QUERY) + ")"
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    override fun androidInjector(): AndroidInjector<Any> = androidInjector

    companion object {
        const val EXTRA_QUERY = "query"

        fun createIntent(context: Context, query: String): Intent {
            val intent = Intent(context, SearchResultActivity::class.java)
            intent.putExtra(EXTRA_QUERY, query)
            return intent
        }
    }
}
