package ru.spbstu.sharedplanner.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.*
import ru.spbstu.common.di.prefs.PreferencesRepository
import ru.spbstu.common.domain.EventTypes
import ru.spbstu.common.domain.NotificationsTypes
import ru.spbstu.common.network.Api
import ru.spbstu.common.network.model.PushTokenBody
import ru.spbstu.sharedplanner.R
import ru.spbstu.sharedplanner.notifications.di.DaggerNotificationsComponent
import ru.spbstu.sharedplanner.root.presentation.MainActivity
import timber.log.Timber
import javax.inject.Inject

class NotificationsService : FirebaseMessagingService() {
    @Inject
    lateinit var api: Api

    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    private val job = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + job)

    override fun onCreate() {
        super.onCreate()
        Timber.tag(TAG).d("onCreate")
        inject()
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Timber.tag(TAG).d("onMessageReceived: $message")
        if (!preferencesRepository.notifsEnabled) return
        val eventType = EventTypes.fromInt(message.data["event_type"]?.toInt() ?: return)
        val groupId = message.data["group_id"]?.toLong() ?: return
        if (!preferencesRepository.getNotifsEnabledForGroupId(groupId)) return
        val notificationType =
            NotificationsTypes.fromInt(message.data["notification_type"]?.toInt() ?: return)
        val title = message.data["event_title"] ?: return
        createNotificationChannel()
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val flag =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE
            else PendingIntent.FLAG_MUTABLE
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, flag)
        val builder = NotificationCompat.Builder(this, NOTIFICATIONS_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_app_icon_24_white)
            .setContentTitle(title)
            .setContentIntent(pendingIntent)
            .setContentText(getNotificationTextFromItem(notificationType))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            Timber.tag(TAG).d("onMessageReceived: notified")
            notify(title.hashCode(), builder.build())
        }
    }

    private fun getNotificationTextFromItem(repeatItem: NotificationsTypes): String {
        return when (repeatItem) {
            NotificationsTypes.MIN_5 -> getString(ru.spbstu.calendar.R.string.notif_minutes_5)
            NotificationsTypes.MIN_10 -> getString(ru.spbstu.calendar.R.string.notif_minutes_10)
            NotificationsTypes.MIN_15 -> getString(ru.spbstu.calendar.R.string.notif_minutes_15)
            NotificationsTypes.MIN_30 -> getString(ru.spbstu.calendar.R.string.notif_minutes_30)
            NotificationsTypes.HOUR -> getString(ru.spbstu.calendar.R.string.notif_in_hour)
            NotificationsTypes.DAY -> getString(ru.spbstu.calendar.R.string.notif_in_day)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notification_channel_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATIONS_CHANNEL_ID, name, importance)
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun inject() {
        DaggerNotificationsComponent.factory()
            .create(applicationContext)
            .inject(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        serviceScope.cancel()
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.tag(TAG).d("onNewToken: $token")
        // send token
        serviceScope.launch {
            try {
                api.pushToken(PushTokenBody(token))
            } catch (e: Exception) {

            } finally {
                stopSelf()
            }
        }
    }

    companion object {
        private val TAG = NotificationsService::class.simpleName!!
        private const val NOTIFICATIONS_CHANNEL_ID =
            "ru.spbstu.sharedplanned.NOTIFICATIONS_CHANNEL_ID"
    }
}