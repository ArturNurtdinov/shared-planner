package ru.spbstu.sharedplanner.root.presentation.di

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import ru.spbstu.common.di.prefs.PreferencesRepository
import ru.spbstu.common.di.viewmodel.ViewModelKey
import ru.spbstu.common.di.viewmodel.ViewModelModule
import ru.spbstu.common.network.Api
import ru.spbstu.sharedplanner.navigation.Navigator
import ru.spbstu.sharedplanner.root.presentation.MainActivityViewModel
import ru.spbstu.sharedplanner.root.presentation.RootRouter

@Module(
    includes = [
        ViewModelModule::class
    ]
)
class RootActivityModule {

    @Provides
    fun provideRootRouter(navigator: Navigator): RootRouter = navigator

    @Provides
    @IntoMap
    @ViewModelKey(MainActivityViewModel::class)
    fun provideViewModel(
        rootRouter: RootRouter,
        api: Api,
        preferencesRepository: PreferencesRepository,
    ): ViewModel {
        return MainActivityViewModel(
            rootRouter, api, preferencesRepository
        )
    }

    @Provides
    fun provideViewModelCreator(
        activity: AppCompatActivity,
        viewModelFactory: ViewModelProvider.Factory
    ): MainActivityViewModel {
        return ViewModelProvider(activity, viewModelFactory).get(MainActivityViewModel::class.java)
    }
}