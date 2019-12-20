package com.danielniklaus.cermatites.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.danielniklaus.cermatites.usersearch.ui.SearchViewModel
import com.danielniklaus.cermatites.viewmodel.ViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


@Suppress("unused")
@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    abstract fun bindSearchViewModel(searchViewModel: SearchViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}
