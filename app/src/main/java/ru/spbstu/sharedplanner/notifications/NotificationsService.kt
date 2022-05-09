package ru.spbstu.sharedplanner.notifications

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import ru.spbstu.common.network.Api
import ru.spbstu.sharedplanner.notifications.di.DaggerNotificationsComponent
import timber.log.Timber
import javax.inject.Inject

class NotificationsService : FirebaseMessagingService() {
    @Inject
    lateinit var api: Api

    override fun onCreate() {
        super.onCreate()
        Timber.tag(TAG).d("onCreate")
        inject()
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Timber.tag(TAG).d("onMessageReceived: $message")
    }

    private fun inject() {
        DaggerNotificationsComponent.factory()
            .create(applicationContext)
            .inject(this)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.tag(TAG).d("onNewToken: $token")
        // send token
    }

    companion object {
        private val TAG = NotificationsService::class.simpleName!!
    }
}