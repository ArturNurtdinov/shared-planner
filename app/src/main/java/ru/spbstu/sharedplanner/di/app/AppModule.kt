package ru.spbstu.sharedplanner.di.app

import android.content.ContentResolver
import android.content.Context
import dagger.Module
import dagger.Provides
import ru.spbstu.common.di.scope.ApplicationScope
import ru.spbstu.sharedplanner.App

@Module
class AppModule {

    @ApplicationScope
    @Provides
    fun provideContext(application: App): Context {
        return application
    }

    @Provides
    fun provideContentResolver(context: Context): ContentResolver {
        return context.contentResolver
    }
}
