package ru.spbstu.sharedplanner

import android.app.Application
import ru.spbstu.common.di.CommonApi
import ru.spbstu.common.di.FeatureContainer
import ru.spbstu.sharedplanner.di.app.AppComponent
import ru.spbstu.sharedplanner.di.app.DaggerAppComponent
import ru.spbstu.sharedplanner.di.deps.FeatureHolderManager
import ru.spbstu.sharedplanner.log.ReleaseTree
import timber.log.Timber
import javax.inject.Inject

class App: Application(), FeatureContainer {

    @Inject
    lateinit var featureHolderManager: FeatureHolderManager

    private lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent
            .builder()
            .application(this)
            .build()

        appComponent.inject(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(ReleaseTree())
        }
    }

    override fun <T> getFeature(key: Class<*>): T {
        return featureHolderManager.getFeature<T>(key)!!
    }

    override fun releaseFeature(key: Class<*>) {
        featureHolderManager.releaseFeature(key)
    }

    override fun commonApi(): CommonApi {
        return appComponent
    }
}