package com.sakurafish.pockettoushituryou.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sakurafish.pockettoushituryou.di.ViewModelFactory
import com.sakurafish.pockettoushituryou.di.ViewModelKey
import com.sakurafish.pockettoushituryou.view.activity.FavoritesActivity
import com.sakurafish.pockettoushituryou.view.activity.HelpActivity
import com.sakurafish.pockettoushituryou.view.activity.MainActivity
import com.sakurafish.pockettoushituryou.view.activity.SearchResultActivity
import com.sakurafish.pockettoushituryou.viewmodel.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
internal abstract class ActivityModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(viewModel: MainViewModel): ViewModel

    @ContributesAndroidInjector
    internal abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector
    internal abstract fun contributeSearchResultActivity(): SearchResultActivity

    @ContributesAndroidInjector
    internal abstract fun contributeFavoritesActivity(): FavoritesActivity

    @ContributesAndroidInjector
    internal abstract fun contributeHelpActivity(): HelpActivity
}