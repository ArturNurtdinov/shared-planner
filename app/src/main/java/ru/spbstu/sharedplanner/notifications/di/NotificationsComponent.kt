package ru.spbstu.sharedplanner.notifications.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.spbstu.common.di.modules.CommonModule
import ru.spbstu.common.di.modules.NetworkModule
import ru.spbstu.common.di.scope.ApplicationScope
import ru.spbstu.sharedplanner.notifications.NotificationsService

@Component(
    modules = [
        CommonModule::class,
        NetworkModule::class,
    ]
)
@ApplicationScope
interface NotificationsComponent {

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance context: Context
        ): NotificationsComponent
    }

    fun inject(service: NotificationsService)
}